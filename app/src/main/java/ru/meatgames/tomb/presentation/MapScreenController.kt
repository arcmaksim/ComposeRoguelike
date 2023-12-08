package ru.meatgames.tomb.presentation

import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.meatgames.tomb.di.MAP_VIEWPORT_HEIGHT_KEY
import ru.meatgames.tomb.di.MAP_VIEWPORT_WIDTH_KEY
import ru.meatgames.tomb.domain.GameController
import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.domain.component.PositionComponent
import ru.meatgames.tomb.domain.component.calculateVectorTo
import ru.meatgames.tomb.domain.component.isInExactProximity
import ru.meatgames.tomb.domain.map.LevelMapWrapper
import ru.meatgames.tomb.domain.map.MapController
import ru.meatgames.tomb.domain.map.MapScreenState
import ru.meatgames.tomb.domain.map.MapState
import ru.meatgames.tomb.domain.map.MapTile
import ru.meatgames.tomb.domain.map.MapTileWrapper
import ru.meatgames.tomb.domain.player.CharacterController
import ru.meatgames.tomb.domain.render.GameMapRenderPipeline
import ru.meatgames.tomb.domain.render.computeFov
import ru.meatgames.tomb.map
import ru.meatgames.tomb.model.theme.ThemeAssets
import ru.meatgames.tomb.model.theme.TilesController
import ru.meatgames.tomb.presentation.camera.animation.CameraAnimationState
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MapScreenController @Inject constructor(
    private val themeAssets: ThemeAssets,
    @Named(MAP_VIEWPORT_WIDTH_KEY)
    private val viewportWidth: Int,
    @Named(MAP_VIEWPORT_HEIGHT_KEY)
    private val viewportHeight: Int,
    private val mapController: MapController,
    private val characterController: CharacterController,
    private val tilesController: TilesController,
    private val gameMapRenderPipeline: GameMapRenderPipeline,
    private val gameController: GameController,
) {
    
    private val characterRenderData = themeAssets.characterRenderData
    
    private val _state = MutableStateFlow<MapScreenState>(MapScreenState.Loading)
    val state: StateFlow<MapScreenState> = _state
    
    private val cachedVisibilityMask = BooleanArray(viewportWidth * viewportWidth) { false }
    
    private val preProcessingBufferSizeModifier: Int = 1
    private val preProcessingViewportWidth: Int =
        viewportWidth + 2 * preProcessingBufferSizeModifier
    private val preProcessingViewportHeight: Int =
        viewportHeight + 2 * preProcessingBufferSizeModifier
    
    private val characterPositionFlow = characterController.characterStateFlow
        .map(GlobalScope) { it.position }
    
    private var previousCharacterPosition: PositionComponent? = null
    
    init {
        mapController.mapFlow
            .flatMapLatest { map ->
                when (map) {
                    is MapState.MapUnavailable -> flow { MapScreenState.Loading }
                    is MapState.MapAvailable -> produceMapFlow(map.mapWrapper)
                }
            }
            .onEach(_state::emit)
            .launchIn(GlobalScope)
    }
    
    private fun produceMapFlow(
        mapWrapper: LevelMapWrapper,
    ): Flow<MapScreenState> = combine(
        mapWrapper.state,
        characterPositionFlow,
    ) { streamedTiles, characterPosition ->
        streamedTiles.toMapState(
            mapWidth = mapWrapper.width,
            mapHeight = mapWrapper.height,
            characterPosition = characterPosition,
        ).also {
            previousCharacterPosition = characterPosition
        }
    }
    
    private fun List<MapTile>.toMapState(
        mapWidth: Int,
        mapHeight: Int,
        characterPosition: PositionComponent,
    ): MapScreenState {
        if (!characterPosition.isValid) return MapScreenState.Loading
        
        val leftXCoordinate = characterPosition.x - preProcessingViewportWidth / 2
        val topYCoordinate = characterPosition.y - preProcessingViewportHeight / 2
        
        val reducedTiles = reduceToViewportSize(
            leftXCoordinate = leftXCoordinate,
            topYCoordinate = topYCoordinate,
            mapWidth = mapWidth,
            mapHeight = mapHeight,
        ).also {
            it.calculateFov(
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight,
            )
        }
        
        val pipelineRenderData = gameMapRenderPipeline.run(
            tiles = reducedTiles,
            tilesLineWidth = preProcessingViewportWidth,
            startCoordinates = characterPosition.x - viewportWidth / 2 to characterPosition.y - viewportHeight / 2,
            shouldRenderTile = { index ->
                cachedVisibilityMask[index]
            },
        )
        
        val tileToFadeIn = pipelineRenderData.tilesToFadeIn.toSet()
        val tileToFadeOut = pipelineRenderData.tilesToFadeOut.toSet()
        
        return MapScreenState.Ready(
            tilesWidth = preProcessingViewportWidth,
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
            tilesPadding = preProcessingBufferSizeModifier,
            tiles = pipelineRenderData.tiles,
            tilesToFadeIn = tileToFadeIn,
            tilesToFadeOut = tileToFadeOut,
            characterRenderData = characterRenderData,
            playerHealth = HealthComponent(10),
            cameraAnimation = characterPosition.produceCameraAnimation()
        )
    }
    
    private fun List<MapTileWrapper?>.calculateFov(
        viewportWidth: Int,
        viewportHeight: Int,
    ) {
        cachedVisibilityMask.updateVisibilityMask()
        
        val characterScreenSpaceX = viewportWidth / 2
        val characterScreenSpaceY = viewportHeight / 2
        
        computeFov(
            originX = characterScreenSpaceX,
            originY = characterScreenSpaceY,
            maxDepth = viewportWidth / 2 + 1,
            revealTile = { x, y -> cachedVisibilityMask[x + y * viewportWidth] = true },
            checkIfTileIsBlocking = { x, y ->
                val index = (x + 1) + (y + 1) * preProcessingViewportWidth
                val objectEntity = this[index]?.tile?.objectEntityTile ?: return@computeFov false
                !tilesController.isObjectEntityVisibleThrough(
                    objectEntity = objectEntity,
                )
            }
        )
    }
    
    private fun BooleanArray.updateVisibilityMask() {
        fill(false)
    }
    
    private fun List<MapTile>.reduceToViewportSize(
        leftXCoordinate: Int,
        topYCoordinate: Int,
        mapWidth: Int,
        mapHeight: Int,
    ): List<MapTileWrapper?> = (0 until preProcessingViewportHeight).map { line ->
        val start = (topYCoordinate + line) * mapWidth + leftXCoordinate
        val end = start + preProcessingViewportWidth
        when {
            topYCoordinate + line !in 0 until mapHeight -> {
                List(preProcessingViewportWidth) { null }
            }
            
            leftXCoordinate < 0 -> {
                List(preProcessingViewportWidth) { index ->
                    val tileIndex = start + index
                    when {
                        leftXCoordinate + index < 0 -> null
                        else -> this[tileIndex]
                    }?.toMapTileWrapper(
                        tileIndex = tileIndex,
                        mapWidth = mapWidth,
                    )
                }
            }
            
            leftXCoordinate + preProcessingViewportWidth > mapWidth -> {
                List(preProcessingViewportWidth) { index ->
                    val tileIndex = start + index
                    when {
                        leftXCoordinate + index < mapWidth -> this[tileIndex]
                        else -> null
                    }?.toMapTileWrapper(
                        tileIndex = tileIndex,
                        mapWidth = mapWidth,
                    )
                }
            }
            
            else -> this.subList(start, end).mapIndexed { index, tile ->
                tile.toMapTileWrapper(
                    tileIndex = start + index,
                    mapWidth = mapWidth,
                )
            }
        }
    }.fold(emptyList()) { acc, item -> acc + item }
    
    private fun MapTile.toMapTileWrapper(
        tileIndex: Int,
        mapWidth: Int,
    ): MapTileWrapper = MapTileWrapper(
        x = tileIndex % mapWidth,
        y = tileIndex / mapWidth,
        tile = this,
    )
    
    private fun PositionComponent.produceCameraAnimation(
        otherPosition: PositionComponent? = previousCharacterPosition,
    ): CameraAnimationState? {
        if (this == otherPosition) return null
        if (otherPosition == null) {
            return CameraAnimationState.Instant(System.currentTimeMillis())
        }
        
        val vector = calculateVectorTo(otherPosition)
        if (vector.isInExactProximity()) {
            return CameraAnimationState.Smooth(
                IntOffset(vector.first, vector.second),
                System.currentTimeMillis()
            )
        }
        
        return CameraAnimationState.Instant(System.currentTimeMillis())
    }
    
}
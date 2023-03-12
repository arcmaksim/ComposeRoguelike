package ru.meatgames.tomb.domain

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import ru.meatgames.tomb.di.MAP_VIEWPORT_HEIGHT_KEY
import ru.meatgames.tomb.di.MAP_VIEWPORT_WIDTH_KEY
import ru.meatgames.tomb.model.temp.TilesController
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.screen.compose.game.render.GameMapRenderPipeline
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MapScreenController @Inject constructor(
    @Named(MAP_VIEWPORT_WIDTH_KEY)
    private val viewportWidth: Int,
    @Named(MAP_VIEWPORT_HEIGHT_KEY)
    private val viewportHeight: Int,
    private val mapController: MapController,
    private val characterController: CharacterController,
    private val tilesController: TilesController,
    private val gameMapRenderPipeline: GameMapRenderPipeline,
) {
    
    private val _state = MutableStateFlow<MapScreenState>(MapScreenState.Loading)
    val state: StateFlow<MapScreenState> = _state
    
    private val cachedVisibilityMask = BooleanArray(viewportWidth * viewportWidth) { false }
    
    private val preProcessingBufferSizeModifier: Int = 1
    private val preProcessingViewportWidth: Int =
        viewportWidth + 2 * preProcessingBufferSizeModifier
    private val preProcessingViewportHeight: Int =
        viewportHeight + 2 * preProcessingBufferSizeModifier
    
    init {
        GlobalScope.launch {
            mapController.mapFlow.flatMapLatest { map ->
                if (map !is MapState.MapAvailable) {
                    return@flatMapLatest flow { MapScreenState.Loading }
                }
                
                val mapWidth = map.mapWrapper.width
                val mapHeight = map.mapWrapper.height
                
                map.mapWrapper.state.combine(
                    characterController.characterStateFlow
                ) { streamedTiles, characterState ->
                    streamedTiles.toMapState(
                        mapWidth = mapWidth,
                        mapHeight = mapHeight,
                        characterState = characterState,
                    )
                }
            }.collect(_state::emit)
        }
    }
    
    private fun List<MapTile>.toMapState(
        mapWidth: Int,
        mapHeight: Int,
        characterState: CharacterState,
    ): MapScreenState {
        if (characterState.mapX == -1 && characterState.mapY == -1) {
            return MapScreenState.Loading
        }
        
        val reducedTiles = reduceToViewportSize(
            mapX = characterState.mapX - preProcessingViewportWidth / 2,
            mapY = characterState.mapY - preProcessingViewportHeight / 2,
            mapWidth = mapWidth,
            mapHeight = mapHeight,
        )
    
        reducedTiles.calculateFov(
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
        )
        
        val pipelineRenderData = gameMapRenderPipeline.run(
            tiles = reducedTiles,
            tilesLineWidth = preProcessingViewportWidth,
            startCoordinates = characterState.mapX - viewportWidth / 2 to characterState.mapY - viewportHeight / 2,
            shouldRenderTile = { index ->
                cachedVisibilityMask[index]
            },
        )
        
        return MapScreenState.Ready(
            tilesWidth = preProcessingViewportWidth,
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
            tilesPadding = preProcessingBufferSizeModifier,
            tiles = pipelineRenderData.tiles,
            tilesToReveal = pipelineRenderData.tilesToReveal.toSet(),
            tilesToFade = pipelineRenderData.tilesToFade.toSet(),
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
        mapX: Int,
        mapY: Int,
        mapWidth: Int,
        mapHeight: Int,
    ): List<MapTileWrapper?> = (0 until preProcessingViewportHeight).map { line ->
        val start = (mapY + line) * mapWidth + mapX
        val end = start + preProcessingViewportWidth
        when {
            mapY + line !in 0 until mapHeight -> {
                List(preProcessingViewportWidth) { null }
            }
            
            mapX < 0 -> {
                List(preProcessingViewportWidth) { index ->
                    val tileIndex = start + index
                    when {
                        mapX + index < 0 -> null
                        else -> this[tileIndex]
                    }?.toMapTileWrapper(
                        tileIndex = tileIndex,
                        mapWidth = mapWidth,
                    )
                }
            }
            
            mapX + preProcessingViewportWidth > mapWidth -> {
                List(preProcessingViewportWidth) { index ->
                    val tileIndex = start + index
                    when {
                        mapX + index < mapWidth -> this[tileIndex]
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
    
    sealed class MapScreenState {
        
        data class Ready(
            val tiles: List<MapRenderTile?>,
            val tilesWidth: Int,
            val tilesPadding: Int,
            val viewportWidth: Int,
            val viewportHeight: Int,
            val tilesToReveal: Set<ScreenSpaceCoordinates>,
            val tilesToFade: Set<ScreenSpaceCoordinates>,
        ) : MapScreenState()
        
        object Loading : MapScreenState()
        
    }
    
}

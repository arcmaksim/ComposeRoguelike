package ru.meatgames.tomb.domain.map

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import ru.meatgames.tomb.di.MAP_VIEWPORT_HEIGHT_KEY
import ru.meatgames.tomb.di.MAP_VIEWPORT_WIDTH_KEY
import ru.meatgames.tomb.domain.Coordinates
import ru.meatgames.tomb.domain.GameController
import ru.meatgames.tomb.domain.GameState
import ru.meatgames.tomb.domain.component.minus
import ru.meatgames.tomb.domain.component.plus
import ru.meatgames.tomb.domain.enemy.EnemyAnimation
import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.domain.player.CharacterController
import ru.meatgames.tomb.domain.player.CharacterState
import ru.meatgames.tomb.domain.render.GameMapRenderPipeline
import ru.meatgames.tomb.domain.render.computeFov
import ru.meatgames.tomb.domain.turn.EnemyTurnResult
import ru.meatgames.tomb.model.temp.ThemeAssets
import ru.meatgames.tomb.model.temp.TilesController
import ru.meatgames.tomb.render.Icon
import ru.meatgames.tomb.resolvedOffset
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

typealias EnemiesAnimations = List<Pair<EnemyId, EnemyAnimation>>

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
    
    private var cachedMapState: MapScreenState = MapScreenState.Loading
    
    private var latestGameState: GameState = GameState.Loading
    
    init {
        GlobalScope.launch {
            mapController.mapFlow.flatMapLatest { map ->
                if (map !is MapState.MapAvailable) {
                    return@flatMapLatest flow { MapScreenState.Loading }
                }
                
                val mapWidth = map.mapWrapper.width
                val mapHeight = map.mapWrapper.height
                
                combine(
                    map.mapWrapper.state,
                    characterController.characterStateFlow,
                    gameController.state,
                ) { streamedTiles, characterState, gameState ->
                    if (latestGameState == gameState) return@combine cachedMapState
                    
                    latestGameState = gameState
                    
                    if (gameState.updatesState()) {
                        return@combine streamedTiles.toMapState(
                            mapWidth = mapWidth,
                            mapHeight = mapHeight,
                            characterState = characterState,
                            gameState = gameState,
                        ).also {
                            cachedMapState = it
                        }
                    }
                    
                    cachedMapState
                }
            }.collect(_state::emit)
        }
    }
    
    private fun GameState.updatesState(): Boolean {
        return this is GameState.AnimatingCharacter || this is GameState.AnimatingEnemies ||
            this is GameState.PrepareForEnemies || this is GameState.WaitingForInput
    }
    
    private fun List<MapTile>.toMapState(
        mapWidth: Int,
        mapHeight: Int,
        characterState: CharacterState,
        gameState: GameState,
    ): MapScreenState {
        if (characterState.position.x == -1 && characterState.position.y == -1) {
            return MapScreenState.Loading
        }
        
        val leftXCoordinate = characterState.position.x - preProcessingViewportWidth / 2
        val topYCoordinate = characterState.position.y - preProcessingViewportHeight / 2
        val viewportZeroPosition = (leftXCoordinate + preProcessingBufferSizeModifier) to (topYCoordinate + preProcessingBufferSizeModifier)
        
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
            startCoordinates = characterState.position.x - viewportWidth / 2 to characterState.position.y - viewportHeight / 2,
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
            playerHealth = characterState.health,
            turnResultsToAnimate = gameState.toMapScreenCharacterAnimations(
                viewportZeroPosition = viewportZeroPosition,
                viewportWidth = viewportWidth,
            ),
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
    
    private fun GameState.toMapScreenCharacterAnimations(
        viewportWidth: Int,
        viewportZeroPosition: Coordinates,
    ): MapScreenCharacterAnimations? = when (this) {
        is GameState.AnimatingCharacter -> {
            MapScreenCharacterAnimations.Player(turnResult)
        }
        
        is GameState.AnimatingEnemies -> {
            MapScreenCharacterAnimations.Enemies(
                results.filterNonVisibleAnimations(
                    viewportWidth = viewportWidth,
                    viewportZeroPosition = viewportZeroPosition,
                ).toEnemiesAnimations(
                    viewportWidth = viewportWidth,
                    viewportZeroPosition = viewportZeroPosition,
                ),
            )
        }
        
        is GameState.WaitingForInput, is GameState.PrepareForEnemies -> null
        
        else -> throw IllegalArgumentException("Unexpected game state: $this")
    }
    
    private fun List<EnemyTurnResult>.filterNonVisibleAnimations(
        viewportZeroPosition: Coordinates,
        viewportWidth: Int,
    ): List<EnemyTurnResult> = filter { result ->
        when (result) {
            is EnemyTurnResult.Move -> {
                listOf(
                    result.position - viewportZeroPosition,
                    result.position + result.direction.resolvedOffset - viewportZeroPosition,
                )
            }
            
            else -> listOf(result.position - viewportZeroPosition)
        }.filter { (x, y) -> x in 0 until viewportWidth && y in 0 until viewportHeight }
            .any { (x, y) -> cachedVisibilityMask[x + y * viewportWidth] }
    }
    
    private fun List<EnemyTurnResult>.toEnemiesAnimations(
        viewportZeroPosition: Coordinates,
        viewportWidth: Int,
    ): EnemiesAnimations = map { result ->
        when (result) {
            is EnemyTurnResult.Move -> {
                val currentScreenSpacePosition = result.position - viewportZeroPosition
                val currentScreenSpaceIndex = currentScreenSpacePosition.first + currentScreenSpacePosition.second * viewportWidth
                val currentTileVisibility = cachedVisibilityMask.getOrElse(currentScreenSpaceIndex) { false }
                
                val nextScreenSpacePosition = currentScreenSpacePosition + result.direction.resolvedOffset
                val nextScreenSpaceIndex = nextScreenSpacePosition.first + nextScreenSpacePosition.second * viewportWidth
                val nextTileVisibility = cachedVisibilityMask.getOrElse(nextScreenSpaceIndex) { false }
                
                result.enemyId to EnemyAnimation.Move(
                    direction = result.direction,
                    fade = when {
                        !currentTileVisibility && nextTileVisibility -> EnemyAnimation.Move.Fade.IN
                        currentTileVisibility && !nextTileVisibility -> EnemyAnimation.Move.Fade.OUT
                        else -> EnemyAnimation.Move.Fade.NONE
                    },
                )
            }
            
            is EnemyTurnResult.Attack -> {
                result.enemyId to EnemyAnimation.Attack(
                    direction = result.direction,
                )
            }
            
            is EnemyTurnResult.SkipTurn -> {
                result.enemyId to EnemyAnimation.Icon(themeAssets.getIconRenderData(Icon.Clock))
            }
        }
    }
    
}

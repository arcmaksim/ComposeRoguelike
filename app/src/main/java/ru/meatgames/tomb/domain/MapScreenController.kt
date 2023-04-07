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
import ru.meatgames.tomb.domain.component.HealthComponent
import ru.meatgames.tomb.domain.turn.EnemyTurnResult
import ru.meatgames.tomb.domain.turn.PlayerTurnResult
import ru.meatgames.tomb.logMessage
import ru.meatgames.tomb.model.temp.ThemeAssets
import ru.meatgames.tomb.model.temp.TilesController
import ru.meatgames.tomb.render.AnimationRenderData
import ru.meatgames.tomb.render.MapRenderTile
import ru.meatgames.tomb.screen.compose.game.render.GameMapRenderPipeline
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MapScreenController @Inject constructor(
    themeAssets: ThemeAssets,
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
                    logMessage("ZXC", "$gameState -> ${gameState.updatesState()}")
                    if (latestGameState == gameState) {
                        logMessage("ZXC", "Game state gated")
                        return@combine cachedMapState
                    }
                    
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
        
        val reducedTiles = reduceToViewportSize(
            mapX = characterState.position.x - preProcessingViewportWidth / 2,
            mapY = characterState.position.y - preProcessingViewportHeight / 2,
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
            startCoordinates = characterState.position.x - viewportWidth / 2 to characterState.position.y - viewportHeight / 2,
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
            tilesToFadeIn = pipelineRenderData.tilesToFadeIn.toSet(),
            tilesToFadeOut = pipelineRenderData.tilesToFadeOut.toSet(),
            characterRenderData = characterRenderData,
            playerHealth = characterState.health,
            turnResultsToAnimate = when (gameState) {
                is GameState.AnimatingCharacter -> MapScreenCharacterTurnResults.Player(gameState.turnResult)
                is GameState.AnimatingEnemies -> MapScreenCharacterTurnResults.Enemies(gameState.results)
                is GameState.WaitingForInput, is GameState.PrepareForEnemies -> null
                else -> throw IllegalArgumentException("Unexpected game state: $gameState")
            },
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
            val characterRenderData: AnimationRenderData,
            val tiles: List<MapRenderTile?>,
            val tilesWidth: Int,
            val tilesPadding: Int,
            val viewportWidth: Int,
            val viewportHeight: Int,
            val tilesToFadeIn: Set<ScreenSpaceCoordinates>,
            val tilesToFadeOut: Set<ScreenSpaceCoordinates>,
            val playerHealth: HealthComponent,
            val turnResultsToAnimate: MapScreenCharacterTurnResults?,
        ) : MapScreenState()
        
        object Loading : MapScreenState()
        
    }
    
    sealed class MapScreenCharacterTurnResults {
        
        data class Player(
            val turnResult: PlayerTurnResult,
        ) : MapScreenCharacterTurnResults()
        
        data class Enemies(
            val turnResults: List<EnemyTurnResult>,
        ) : MapScreenCharacterTurnResults()
        
    }
    
}

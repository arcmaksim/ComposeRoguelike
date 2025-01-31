package ru.meatgames.tomb.domain.map

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
import ru.meatgames.tomb.domain.GameState
import ru.meatgames.tomb.domain.component.PositionComponent
import ru.meatgames.tomb.domain.component.StatusComponent
import ru.meatgames.tomb.domain.component.minus
import ru.meatgames.tomb.domain.component.plus
import ru.meatgames.tomb.domain.enemy.EnemyAnimation
import ru.meatgames.tomb.domain.enemy.EnemyId
import ru.meatgames.tomb.domain.player.CharacterController
import ru.meatgames.tomb.domain.player.PlayerState
import ru.meatgames.tomb.domain.render.BUFFER_SIZE_MODIFIER
import ru.meatgames.tomb.domain.render.BufferHolder
import ru.meatgames.tomb.domain.render.BufferHolderFactory
import ru.meatgames.tomb.domain.render.GameMapRenderPipeline
import ru.meatgames.tomb.domain.render.computeFov
import ru.meatgames.tomb.domain.status.Status
import ru.meatgames.tomb.domain.turn.EnemyTurnResult
import ru.meatgames.tomb.model.theme.ThemeAssets
import ru.meatgames.tomb.model.theme.TilesController
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
    mapController: MapController,
    private val characterController: CharacterController,
    private val tilesController: TilesController,
    private val gameMapRenderPipeline: GameMapRenderPipeline,
    private val gameController: GameController,
    private val bufferHolderFactory: BufferHolderFactory,
) {

    private val characterRenderData = themeAssets.characterRenderData

    private val _state = MutableStateFlow<MapScreenState>(MapScreenState.Loading)
    val state: StateFlow<MapScreenState> = _state

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
    ): Flow<MapScreenState> {
        var cachedMapState: MapScreenState = MapScreenState.Loading
        var latestGameState: GameState = GameState.Loading

        return combine(
            mapWrapper.state,
            characterController.playerStateFlow,
            gameController.state,
        ) { streamedTiles, characterState, gameState ->
            if (latestGameState == gameState) return@combine cachedMapState

            latestGameState = gameState

            if (gameState.updatesState()) {
                return@combine streamedTiles.toMapState(
                    mapWidth = mapWrapper.width,
                    mapHeight = mapWrapper.height,
                    playerState = characterState,
                    gameState = gameState,
                ).also {
                    cachedMapState = it
                }
            }

            cachedMapState
        }
    }

    private fun GameState.updatesState(): Boolean {
        return this is GameState.AnimatingCharacter || this is GameState.AnimatingEnemies ||
            this is GameState.PrepareForEnemies || this is GameState.WaitingForInput
    }

    private fun List<MapTile>.toMapState(
        mapWidth: Int,
        mapHeight: Int,
        playerState: PlayerState,
        gameState: GameState,
    ): MapScreenState {
        val position = playerState.getComponent<PositionComponent>()
        if (position.x == -1 && position.y == -1) {
            return MapScreenState.Loading
        }

        val bufferHolder = bufferHolderFactory.get(viewportWidth, viewportHeight)

        bufferHolder.refresh(
            horizontalOffset = position.x - bufferHolder.horizontalCenter,
            verticalOffset = position.y - bufferHolder.verticalCenter,
        )

        bufferHolder.fillMapBuffer(
            tiles = this,
            mapWidth = mapWidth,
            mapHeight = mapHeight,
        )

        bufferHolder.calculateFov()

        val renderData = gameMapRenderPipeline.run()

        val tileToFadeIn = renderData.tilesToFadeIn.toSet()
        val tileToFadeOut = renderData.tilesToFadeOut.toSet()

        val characterAnimatedRenderData = if (playerState.getComponent<StatusComponent>().has(Status.Invisible)) {
            characterRenderData.copy(
                alpha = .5f,
            )
        } else {
            characterRenderData
        }

        return MapScreenState.Ready(
            tilesWidth = bufferHolder.width,
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
            tilesPadding = BUFFER_SIZE_MODIFIER,
            tiles = renderData.tiles,
            tilesToFadeIn = tileToFadeIn,
            tilesToFadeOut = tileToFadeOut,
            characterRenderData = characterAnimatedRenderData,
            playerHealth = playerState.getComponent(),
            turnResultsToAnimate = gameState.toMapScreenCharacterAnimations(bufferHolder),
        )
    }

    private fun BufferHolder.calculateFov() {
        fovBuffer.fill(false)
        visibilityCache.fill(false)

        computeFov(
            originX = horizontalCenter,
            originY = verticalCenter,
            maxDepth = horizontalCenter + 1,
            revealTile = { x, y ->
                fovBuffer[x + y * width] = true
                visibilityCache[x + y * width] = true
            },
            checkIfTileIsBlocking = { x, y ->
                val index = x + y * width
                val objectEntity = mapBuffer[index]?.objectEntityTile ?: return@computeFov false
                !tilesController.isObjectEntityVisibleThrough(
                    objectEntity = objectEntity,
                )
            }
        )

        for (i in 0 until width) {
            fovBuffer[i] = false
            fovBuffer[(height - 1) * width + i] = false
        }

        for (i in 0 until height) {
            fovBuffer[i * width] = false
            fovBuffer[(i + 1) * width - 1] = false
        }
    }

    private fun BufferHolder.fillMapBuffer(
        tiles: List<MapTile>,
        mapWidth: Int,
        mapHeight: Int,
    ) {
        (0 until height).map { line ->
            val start = (verticalOffset + line) * mapWidth + horizontalOffset

            (0 until width).map { index ->

                val tileIndex = start + index

                val tile = when {
                    verticalOffset + line !in 0 until mapHeight -> {
                        null
                    }

                    horizontalOffset < 0 -> {
                        when {
                            horizontalOffset + index < 0 -> null
                            else -> tiles[tileIndex]
                        }
                    }

                    horizontalOffset + width > mapWidth -> {
                        when {
                            horizontalOffset + index < mapWidth -> tiles[tileIndex]
                            else -> null
                        }
                    }

                    else -> {
                        tiles[start + index]
                    }
                }

                tile?.let {
                    mapBuffer.set(
                        index = line * width + index,
                        value = it,
                    )
                }
            }
        }
    }

    private fun GameState.toMapScreenCharacterAnimations(
        bufferHolder: BufferHolder,
    ): MapScreenCharacterAnimations? = when (this) {
        is GameState.AnimatingCharacter -> {
            MapScreenCharacterAnimations.Player(turnResult)
        }

        is GameState.AnimatingEnemies -> {
            MapScreenCharacterAnimations.Enemies(
                results.filterNonVisibleAnimations(
                    bufferHolder = bufferHolder,
                ).toEnemiesAnimations(
                    bufferHolder = bufferHolder,
                ),
            )
        }

        is GameState.WaitingForInput, is GameState.PrepareForEnemies -> null

        else -> throw IllegalArgumentException("Unexpected game state: $this")
    }

    private fun List<EnemyTurnResult>.filterNonVisibleAnimations(
        bufferHolder: BufferHolder,
    ): List<EnemyTurnResult> = filter { result ->
        when (result) {
            is EnemyTurnResult.Move -> {
                listOf(
                    result.position - bufferHolder.offset,
                    result.position - result.direction.resolvedOffset - bufferHolder.offset,
                )
            }

            else -> listOf(result.position - bufferHolder.offset)
        }.filter { (x, y) -> x in 0 until bufferHolder.width && y in 0 until bufferHolder.height }
            .any { (x, y) -> bufferHolder.fovBuffer[x + y * bufferHolder.width] }
    }

    private fun List<EnemyTurnResult>.toEnemiesAnimations(
        bufferHolder: BufferHolder,
    ): EnemiesAnimations = map { result ->
        when (result) {
            is EnemyTurnResult.Alert -> {
                result.enemyId to EnemyAnimation.Icon(
                    renderData = themeAssets.getIconRenderData(Icon.Alert),
                    durationModifier = 2f,
                )
            }

            is EnemyTurnResult.Move -> {
                val currentScreenSpacePosition = result.position - bufferHolder.offset
                val currentScreenSpaceIndex =
                    currentScreenSpacePosition.first + currentScreenSpacePosition.second * bufferHolder.width
                val currentTileVisibility =
                    bufferHolder.fovBuffer.getOrElse(currentScreenSpaceIndex) { false }

                val previousScreenSpacePosition =
                    currentScreenSpacePosition - result.direction.resolvedOffset
                val previousScreenSpaceIndex =
                    previousScreenSpacePosition.first + previousScreenSpacePosition.second * bufferHolder.width
                val previousTileVisibility =
                    bufferHolder.fovBuffer.getOrElse(previousScreenSpaceIndex) { false }

                result.enemyId to EnemyAnimation.Move(
                    direction = result.direction,
                    fade = when {
                        !currentTileVisibility && previousTileVisibility -> EnemyAnimation.Move.Fade.OUT
                        currentTileVisibility && !previousTileVisibility -> EnemyAnimation.Move.Fade.IN
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

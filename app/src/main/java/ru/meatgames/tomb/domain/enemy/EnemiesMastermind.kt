package ru.meatgames.tomb.domain.enemy

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.component.GoalComponent
import ru.meatgames.tomb.domain.component.PositionComponent
import ru.meatgames.tomb.domain.component.StatusComponent
import ru.meatgames.tomb.domain.component.Vector
import ru.meatgames.tomb.domain.component.asDirections
import ru.meatgames.tomb.domain.component.calculateVectorTo
import ru.meatgames.tomb.domain.component.isNextTo
import ru.meatgames.tomb.domain.component.isZero
import ru.meatgames.tomb.domain.component.toCoordinates
import ru.meatgames.tomb.domain.map.MapController
import ru.meatgames.tomb.domain.minus
import ru.meatgames.tomb.domain.player.CharacterController
import ru.meatgames.tomb.domain.player.PlayerState
import ru.meatgames.tomb.domain.render.BufferHolder
import ru.meatgames.tomb.domain.render.BufferHolderFactory
import ru.meatgames.tomb.domain.status.Status
import ru.meatgames.tomb.domain.turn.EnemyTurnResult
import ru.meatgames.tomb.model.theme.TilesController
import ru.meatgames.tomb.resolvedOffset
import javax.inject.Inject

private const val IS_VISIBLE_BY_PLAYER = 0
private const val IS_PLAYER_VISIBLE = 1
private const val VISIBLE_CONTACT = 2

class EnemiesMastermind @Inject constructor(
    private val mapController: MapController,
    private val tilesController: TilesController,
    private val characterController: CharacterController,
    private val enemiesController: EnemiesController,
    private val bufferHolderFactory: BufferHolderFactory,
) {

    private val flags = BooleanArray(3)

    fun takeTurn(
        enemy: Enemy,
    ): EnemyTurnResult = with(enemy) {
        val bufferHolder = bufferHolderFactory.cachedBufferHolder
        val playerState = characterController.playerStateSnapshot
        val vectorToPlayer = getComponent<PositionComponent>()
            .calculateVectorTo(playerState.getComponent<PositionComponent>())

        updateFlags(
            playerState = playerState,
            enemy = enemy,
            bufferHolder = bufferHolder,
        )

        val result = when (val goal = getComponent<GoalComponent>().activeGoal) {
            is GoalComponent.Goal.Player -> targetPlayer(
                playerState = playerState,
                vectorToPlayer = vectorToPlayer,
            )
            is GoalComponent.Goal.Position -> targetPosition(
                characterController = characterController,
                vectorToTarget = getComponent<PositionComponent>().calculateVectorTo(goal.position),
            )
            else -> observe()
        }

        return@with result ?: toSkipResult()
    }

    private fun updateFlags(
        playerState: PlayerState,
        enemy: Enemy,
        bufferHolder: BufferHolder,
    ) {
        flags[IS_VISIBLE_BY_PLAYER] = bufferHolder.areCoordinatesVisibleInCache(
            (enemy.getComponent<PositionComponent>().toCoordinates() - bufferHolder.offset)
        )
        flags[IS_PLAYER_VISIBLE] = !playerState.getComponent<StatusComponent>().has(Status.Invisible)
        flags[VISIBLE_CONTACT] = flags[IS_PLAYER_VISIBLE] && flags[IS_VISIBLE_BY_PLAYER]
    }

    private fun Enemy.targetPlayer(
        playerState: PlayerState,
        vectorToPlayer: Vector,
    ): EnemyTurnResult? {
        if (!flags[IS_PLAYER_VISIBLE]) {
            updateComponent<GoalComponent> {
                GoalComponent(
                    activeGoal = GoalComponent.Goal.Position(
                        playerState.getComponent<PositionComponent>().toCoordinates(),
                    ),
                )
            }
            updateComponent<StatusComponent> {
                add(Status.Confused)
            }
            return toAlertResult()
        }

        if (!flags[IS_VISIBLE_BY_PLAYER]) {
            updateComponent<GoalComponent> {
                GoalComponent(
                    activeGoal = GoalComponent.Goal.Position(
                        playerState.getComponent<PositionComponent>().toCoordinates(),
                    ),
                )
            }
        }

        if (vectorToPlayer.isNextTo()) {
            val damage = 1
            attackPlayer(damage)

            return toAttackResult(
                direction = vectorToPlayer.asDirections().first(),
                damage = damage,
            )
        }

        return move(vectorToPlayer)
    }

    private fun Enemy.targetPosition(
        characterController: CharacterController,
        vectorToTarget: Vector,
    ): EnemyTurnResult? {
        if (flags[VISIBLE_CONTACT] && getComponent<StatusComponent>().has(Status.Confused)) {
            updateComponent<GoalComponent> {
                GoalComponent(
                    activeGoal = GoalComponent.Goal.Player,
                )
            }
            updateComponent<StatusComponent> {
                remove(Status.Confused)
            }
            return toAlertResult()
        }

        if (!vectorToTarget.isZero) {
            val playerPosition = characterController.playerStateSnapshot.getComponent<PositionComponent>().toCoordinates()
            vectorToTarget.asDirections().forEach { direction ->
                val newPosition = (getComponent<PositionComponent>() + direction.resolvedOffset).toCoordinates()

                // Stumbles on player upon movement
                if (playerPosition == newPosition) {
                    updateComponent<GoalComponent> {
                        GoalComponent(
                            activeGoal = GoalComponent.Goal.Player,
                        )
                    }
                    updateComponent<StatusComponent> {
                        remove(Status.Confused)
                    }
                    characterController.removeStatus(Status.Invisible)
                    return toAlertResult()
                }

                mapController.getTile(newPosition)?.let { tile ->
                    val hasNoTileInteraction = tile.objectEntityTile
                        ?.let(tilesController::hasObjectEntityNoInteraction) != false
                    if (hasNoTileInteraction && enemiesController.moveEnemy(id, direction)) {
                        return toMoveResult(direction)
                    }
                }
            }
        }

        if (flags[VISIBLE_CONTACT]) {
            updateComponent<GoalComponent> {
                GoalComponent(
                    activeGoal = GoalComponent.Goal.Player,
                )
            }
            return toAlertResult()
        }

        if (flags[IS_PLAYER_VISIBLE] xor flags[IS_VISIBLE_BY_PLAYER]) {
            updateComponent<GoalComponent> {
                GoalComponent(
                    activeGoal = null,
                )
            }
            return null
        }

        return null
    }

    private fun Enemy.move(
        vectorToPlayer: Vector,
    ): EnemyTurnResult? {
        val directionsToPlayer = vectorToPlayer.asDirections()
        directionsToPlayer.forEach { direction ->
            val newPosition = (getComponent<PositionComponent>() + direction.resolvedOffset).toCoordinates()
            mapController.getTile(newPosition)?.let { tile ->
                val tileInteraction = tile.objectEntityTile
                    ?.let(tilesController::hasObjectEntityNoInteraction) != false
                if (tileInteraction && enemiesController.moveEnemy(id, direction)) {
                    return toMoveResult(direction)
                }
            }
        }

        return null
    }

    private fun Enemy.observe(): EnemyTurnResult? {
        if (flags[VISIBLE_CONTACT]) {
            updateComponent<GoalComponent> {
                GoalComponent(
                    activeGoal = GoalComponent.Goal.Player,
                )
            }

            return toAlertResult()
        }

        return null
    }

    private fun Enemy.attackPlayer(
        damage: Int,
    ) = characterController.modifyHealth(-damage)

    private fun Enemy.toMoveResult(
        direction: Direction,
    ): EnemyTurnResult.Move = EnemyTurnResult.Move(
        enemyId = id,
        position = getComponent<PositionComponent>().toCoordinates(),
        direction = direction,
    )

    private fun Enemy.toAttackResult(
        direction: Direction,
        damage: Int,
    ): EnemyTurnResult.Attack = EnemyTurnResult.Attack(
        enemyId = id,
        position = getComponent<PositionComponent>().toCoordinates(),
        direction = direction,
        amount = damage,
    )

    private fun Enemy.toAlertResult(): EnemyTurnResult.Alert = EnemyTurnResult.Alert(
        enemyId = id,
        position = getComponent<PositionComponent>().toCoordinates(),
    )

    private fun Enemy.toSkipResult(): EnemyTurnResult.SkipTurn = EnemyTurnResult.SkipTurn(
        enemyId = id,
        position = getComponent<PositionComponent>().toCoordinates(),
    )

}
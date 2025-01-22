package ru.meatgames.tomb.domain.enemy

import ru.meatgames.tomb.Direction
import ru.meatgames.tomb.domain.component.GoalComponent
import ru.meatgames.tomb.domain.component.PositionComponent
import ru.meatgames.tomb.domain.component.Vector
import ru.meatgames.tomb.domain.component.asDirections
import ru.meatgames.tomb.domain.component.calculateVectorTo
import ru.meatgames.tomb.domain.component.isNextTo
import ru.meatgames.tomb.domain.component.toCoordinates
import ru.meatgames.tomb.domain.map.MapController
import ru.meatgames.tomb.domain.minus
import ru.meatgames.tomb.domain.player.CharacterController
import ru.meatgames.tomb.domain.render.BufferHolder
import ru.meatgames.tomb.domain.render.BufferHolderFactory
import ru.meatgames.tomb.domain.turn.EnemyTurnResult
import ru.meatgames.tomb.model.theme.TilesController
import ru.meatgames.tomb.resolvedOffset
import javax.inject.Inject

class EnemiesManager @Inject constructor(
    private val mapController: MapController,
    private val tilesController: TilesController,
    private val characterController: CharacterController,
    private val enemiesController: EnemiesController,
    private val bufferHolderFactory: BufferHolderFactory,
) {

    fun takeTurn(
        enemy: Enemy,
    ): EnemyTurnResult = with(enemy) {
        val bufferHolder = bufferHolderFactory.cachedBufferHolder
        val player = characterController.characterStateFlow.value
        val vectorToPlayer = getComponent<PositionComponent>().calculateVectorTo(player.position)

        val result = when (getComponent<GoalComponent>().activeGoal) {
            is GoalComponent.Goal.Player -> targetPlayer(vectorToPlayer)
            is GoalComponent.Goal.Position -> move(vectorToPlayer)
            else -> observe(bufferHolder)
        }

        return@with result ?: toSkipResult()
    }

    private fun Enemy.targetPlayer(
        vectorToPlayer: Vector,
    ): EnemyTurnResult? {
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

    private fun Enemy.observe(
        bufferHolder: BufferHolder,
    ): EnemyTurnResult? {
        val isVisibleByPlayer = bufferHolder.areCoordinatesVisible(
            (getComponent<PositionComponent>().toCoordinates() - bufferHolder.offset)
        )

        if (!isVisibleByPlayer) return null

        updateComponent<GoalComponent> { _ ->
            GoalComponent(
                activeGoal = GoalComponent.Goal.Player,
            )
        }

        return toAlertResult()
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
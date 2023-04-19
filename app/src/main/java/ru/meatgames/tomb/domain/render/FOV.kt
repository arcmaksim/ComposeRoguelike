package ru.meatgames.tomb.domain.render

import ru.meatgames.tomb.Direction
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Direct port of https://www.albertford.com/shadowcasting/
 */
fun computeFov(
    originX: Int,
    originY: Int,
    maxDepth: Int,
    checkIfTileIsBlocking: (Int, Int) -> Boolean,
    revealTile: (Int, Int) -> Unit,
) {
    revealTile(originX, originY)

    setOf(
        Direction.Top,
        Direction.Right,
        Direction.Bottom,
        Direction.Left,
    ).map { direction ->
        val quadrant = Quadrant(
            direction = direction,
            originX = originX,
            originY = originY,
        )

        fun Tile.reveal() {
            val tile = quadrant.transform(this)
            revealTile(tile.rowDepth, tile.col)
        }

        fun Tile.isWall(): Boolean {
            val tile = quadrant.transform(this)
            return checkIfTileIsBlocking(tile.rowDepth, tile.col)
        }

        fun Tile.isFloor(): Boolean {
            val tile = quadrant.transform(this)
            return !checkIfTileIsBlocking(tile.rowDepth, tile.col)
        }

        fun Row.scanIterative() {
            val deque = ArrayDeque(listOf(this))

            while (deque.isNotEmpty()) {
                val row = deque.removeFirst()
                if (row.depth >= maxDepth) continue
                var previousTile: Tile? = null

                row.tiles().forEach { tile ->
                    if (tile.isWall() || tile.isSymmetric(row)) {
                        tile.reveal()
                    }
                    if (previousTile?.isWall() == true && tile.isFloor()) {
                        row.startSlope = tile.slope()
                    }
                    if (previousTile?.isFloor() == true && tile.isWall()) {
                        deque.addFirst(
                            row.next().apply {
                                endSlope = tile.slope()
                            }
                        )
                    }
                    previousTile = tile
                }
                if (previousTile?.isFloor() == true) {
                    deque.addFirst(row.next())
                }
            }
        }

        Row(
            depth = 1,
            startSlope = -1.0,
            endSlope = 1.0,
            maxDepth = maxDepth,
        ).scanIterative()
    }
}

data class Quadrant(
    val direction: Direction,
    val originX: Int,
    val originY: Int,
) {

    fun transform(
        tile: Tile,
    ): Tile = when (direction) {
        Direction.Top -> Tile(originX + tile.col, originY - tile.rowDepth)
        Direction.Bottom -> Tile(originX + tile.col, originY + tile.rowDepth)
        Direction.Right -> Tile(originX + tile.rowDepth, originY + tile.col)
        Direction.Left -> Tile(originX - tile.rowDepth, originY + tile.col)
    }

}

data class Row(
    val depth: Int,
    var startSlope: Double,
    var endSlope: Double,
    val maxDepth: Int,
) {

    fun tiles(): List<Tile> {
        val minCol = (depth * startSlope).roundTiesUp()
        val maxCol = (depth * endSlope).roundTiesDown()
        return ((minCol.toInt())..(maxCol.toInt())).map {
            Tile(
                rowDepth = depth,
                col = it,
            )
        }
    }

    fun next(): Row = copy(
        depth = depth + 1,
    )

}

data class Tile(
    val rowDepth: Int,
    val col: Int,
)

fun Tile.slope(): Double = (2 * col - 1).toDouble() / (2 * rowDepth)

fun Tile.isSymmetric(
    row: Row,
) = col >= row.depth * row.startSlope && col <= row.depth * row.endSlope

fun Double.roundTiesUp() = floor(this + 0.5)

fun Double.roundTiesDown() = ceil(this - 0.5)

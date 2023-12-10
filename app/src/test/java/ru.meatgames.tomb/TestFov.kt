package ru.meatgames.tomb

import org.junit.Test
import ru.meatgames.tomb.presentation.render.computeFov

class TestFov {

    @Test
    fun testFov() {
        val mapSize = 9
        val viewportSize = mapSize - 2

        val map =
                "#########" +
                "#########" +
                "####.####" +
                "###...###" +
                "###...###" +
                "###...###" +
                "#########" +
                "#########" +
                "#########"

        assert(map.length == mapSize * mapSize)

        val mask = BooleanArray(viewportSize * viewportSize) { false }

        computeFov(
            originX = viewportSize / 2,
            originY = viewportSize / 2,
            maxDepth = mapSize / 2,
            checkIfTileIsBlocking = { x: Int, y: Int ->
                map[(x + 1) + (y + 1) * mapSize] == '#'
            },
            revealTile = { x: Int, y: Int ->
                mask[x + y * viewportSize] = true
            },
        )

        val expectedMap =
                "xx###xx\n" +
                "x##.##x\n" +
                "x#...#x\n" +
                "x#...#x\n" +
                "x#...#x\n" +
                "x#####x\n" +
                "xxxxxxx\n"

        assert(expectedMap.length == (viewportSize + 1) * viewportSize)

        val result = map.filterIndexed { index, _ ->
            val x = index % mapSize
            if (x == 0 || x == mapSize - 1) return@filterIndexed false

            val y = index / mapSize
            if (y == 0 || y == mapSize - 1) return@filterIndexed false

            return@filterIndexed true
        }.mapIndexed { index, value ->
            val newLine = if (index % viewportSize == viewportSize - 1) "\n" else ""
            if (mask[index]) "$value$newLine" else "x$newLine"
        }.reduce { acc, s -> acc + s }

        assert(expectedMap == result)
    }

}

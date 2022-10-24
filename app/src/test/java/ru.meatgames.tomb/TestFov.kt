package ru.meatgames.tomb

import org.junit.Test
import ru.meatgames.tomb.domain.computeFov

class TestFov {

    @Test
    fun testFov() {
        val map =
                "#######" +
                "###.###" +
                "##...##" +
                "##...##" +
                "##...##" +
                "#######" +
                "#######"

        val mask = BooleanArray(map.length) { false }

        computeFov(
            originX = 3,
            originY = 3,
            maxDepth = 4,
            checkIfTileIsBlocking = { x: Int, y: Int ->
                map[x + y * 7] == '#'
            },
            revealTile = { x: Int, y: Int ->
                mask[x + y * 7] = true
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

        val result = map.mapIndexed { index, value ->
            val asd = if (index % 7 == 6) "\n" else ""
            if (mask[index]) "$value$asd" else "x$asd"
        }.reduce { acc, s -> acc + s }

        assert(expectedMap == result)
    }

}

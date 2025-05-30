package org.rsmod.routefinder

import kotlin.math.max
import kotlin.math.min
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.EnumSource
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.flag.CollisionFlag.LOC

class LineValidatorLineOfWalkTest {
    @Test
    fun `valid when on top of target coordinates`() {
        val map = CollisionFlagMap()
        map.allocateIfAbsent(3200, 3200, 0)
        with(LineValidator(map)) { assertTrue(hasLineOfWalk(0, 3200, 3200, 3200, 3200)) }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `valid when path clear of collision flags`(dir: Direction) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + (dir.offX * 3)
        val destZ = srcZ + (dir.offZ * 3)
        for (level in 0 until 4) {
            for (z in min(srcZ, destZ)..max(srcZ, destZ)) {
                for (x in min(srcX, destX)..max(srcX, destX)) {
                    map.allocateIfAbsent(x, z, level)
                }
            }
        }
        with(LineValidator(map)) {
            assertTrue(hasLineOfWalk(level = 0, srcX, srcZ, destX, destZ))
            assertTrue(hasLineOfWalk(level = 1, srcX, srcZ, destX, destZ))
            assertTrue(hasLineOfWalk(level = 2, srcX, srcZ, destX, destZ))
            assertTrue(hasLineOfWalk(level = 3, srcX, srcZ, destX, destZ))
        }
    }

    @ParameterizedTest
    @EnumSource(Direction::class)
    fun `fail when path blocked by loc`(dir: Direction) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + (dir.offX * 3)
        val destZ = srcZ + (dir.offZ * 3)
        for (level in 0 until 4) {
            map[srcX + dir.offX, srcZ + dir.offZ, level] = LOC
        }
        with(LineValidator(map)) {
            assertFalse(hasLineOfWalk(level = 0, srcX, srcZ, destX, destZ))
            assertFalse(hasLineOfWalk(level = 1, srcX, srcZ, destX, destZ))
            assertFalse(hasLineOfWalk(level = 2, srcX, srcZ, destX, destZ))
            assertFalse(hasLineOfWalk(level = 3, srcX, srcZ, destX, destZ))
        }
    }

    @ParameterizedTest
    @ArgumentsSource(DirectionalExtraFlagProvider::class)
    fun `fail when path blocked by extraFlag collision flag`(dir: Direction, extraFlag: Int) {
        val map = CollisionFlagMap()
        val srcX = 3200
        val srcZ = 3200
        val destX = srcX + (dir.offX * 3)
        val destZ = srcZ + (dir.offZ * 3)
        for (level in 0 until 4) {
            map[srcX + dir.offX, srcZ + dir.offZ, level] = extraFlag
        }
        with(LineValidator(map)) {
            assertFalse(hasLineOfWalk(level = 0, srcX, srcZ, destX, destZ, extraFlag = extraFlag))
            assertFalse(hasLineOfWalk(level = 1, srcX, srcZ, destX, destZ, extraFlag = extraFlag))
            assertFalse(hasLineOfWalk(level = 2, srcX, srcZ, destX, destZ, extraFlag = extraFlag))
            assertFalse(hasLineOfWalk(level = 3, srcX, srcZ, destX, destZ, extraFlag = extraFlag))
        }
    }
}

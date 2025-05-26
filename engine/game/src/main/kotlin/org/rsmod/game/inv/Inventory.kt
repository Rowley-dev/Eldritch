package org.rsmod.game.inv

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import java.util.BitSet
import org.rsmod.game.type.inv.InvStackType
import org.rsmod.game.type.inv.UnpackedInvType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.isAssociatedWith
import org.rsmod.game.type.util.UncheckedType

public class Inventory(public val type: UnpackedInvType, public val objs: Array<InvObj?>) :
    Iterable<InvObj?> {
    public val modifiedSlots: BitSet = BitSet()

    public val size: Int
        get() = objs.size

    public val indices: IntRange
        get() = objs.indices

    public fun isNotEmpty(): Boolean = !isEmpty()

    public fun isEmpty(): Boolean = objs.all { it == null }

    public fun isFull(): Boolean = objs.all { it != null }

    public fun freeSpace(): Int = objs.count { it == null }

    public fun occupiedSpace(): Int = objs.count { it != null }

    public fun hasFreeSpace(): Boolean = objs.any { it == null }

    public fun lastOccupiedSlot(): Int = indexOfLast { it != null } + 1

    /**
     * Maps and returns the indices (`slots`) of objs in the inventory that satisfy the given
     * [predicate].
     *
     * The [predicate] function is invoked for each slot with its index and the obj at that slot as
     * parameters.
     *
     * **Example Usage:** Find all slots with non-null objs
     *
     * ```
     * val occupiedSlots = inventory.mapSlots { _, obj -> obj != null }
     * ```
     *
     * @param predicate A lambda that takes the slot and the obj and returns `true` for slots to
     *   map.
     * @return A [Set] of slot indices where the objects satisfy the given [predicate].
     */
    public fun mapSlots(predicate: (Int, InvObj?) -> Boolean): Set<Int> =
        objs.mapSlotsTo(IntOpenHashSet(), predicate)

    public fun filterNotNull(predicate: (InvObj) -> Boolean): List<InvObj> =
        objs.mapNotNull { if (it != null && predicate(it)) it else null }

    public fun fillNulls() {
        for (i in objs.indices) {
            if (objs[i] == null) {
                continue
            }
            objs[i] = null
            modifiedSlots.set(i)
        }
    }

    public fun hasModifiedSlots(): Boolean = !modifiedSlots.isEmpty

    public fun clearModifiedSlots() {
        modifiedSlots.clear()
    }

    public fun getValue(slot: Int): InvObj =
        this[slot] ?: throw NoSuchElementException("Slot $slot is missing in the inv.")

    public operator fun get(slot: Int): InvObj? = objs.getOrNull(slot)

    public operator fun set(slot: Int, obj: InvObj?) {
        objs[slot] = obj
        modifiedSlots.set(slot)
    }

    public operator fun contains(type: ObjType): Boolean = objs.any { type.isAssociatedWith(it) }

    public fun count(objType: UnpackedObjType): Int {
        val obj = objs.firstOrNull { it?.id == objType.id } ?: return 0
        val singleStack = type.stack == InvStackType.Always || objType.isStackable
        if (singleStack) {
            return obj.count
        }
        return individualCount(obj)
    }

    public fun count(obj: InvObj, objType: UnpackedObjType): Int {
        val singleStack = type.stack == InvStackType.Always || objType.isStackable
        if (singleStack) {
            return obj.count
        }
        return individualCount(obj)
    }

    private fun individualCount(obj: InvObj): Int {
        var count = 0
        for (i in objs.indices) {
            val other = objs[i] ?: continue
            if (other.id == obj.id) {
                count += other.count
            }
        }
        return count
    }

    override fun iterator(): Iterator<InvObj?> = objs.iterator()

    override fun toString(): String = "Inventory(type=$type, objs=${(objs.mapNotNullEntries())})"

    public companion object {
        @OptIn(UncheckedType::class)
        public fun create(type: UnpackedInvType): Inventory {
            val objs = arrayOfNulls<InvObj>(type.size)
            if (type.stock != null) {
                for (i in type.stock.indices) {
                    val copy = type.stock[i] ?: continue
                    objs[i] = InvObj(copy.obj, copy.count)
                }
            }
            return Inventory(type, objs)
        }

        private inline fun <T : MutableCollection<Int>> Array<InvObj?>.mapSlotsTo(
            destination: T,
            predicate: (Int, InvObj?) -> Boolean,
        ): T {
            for (slot in indices) {
                val obj = this[slot]
                if (predicate(slot, obj)) {
                    destination.add(slot)
                }
            }
            return destination
        }

        private fun Array<InvObj?>.mapNotNullEntries(): List<Pair<Int, InvObj>> =
            mapIndexedNotNull { slot, obj ->
                obj?.let { slot to obj }
            }
    }
}

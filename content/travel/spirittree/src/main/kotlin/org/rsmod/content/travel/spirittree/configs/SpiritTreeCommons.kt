package org.rsmod.content.travel.spirittree.configs

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.map.CoordGrid

val spiritTreeTeleportLocations =
    listOf<CoordGrid>(
        CoordGrid(0, 39, 49, 46, 34), // Tree Gnome Village
        CoordGrid(0, 38, 53, 29, 52), // Gnome Stronghold
        CoordGrid(0, 39, 50, 59, 59), // Battlefield
        CoordGrid(0, 49, 54, 49, 52), // Grand Exchange
        CoordGrid(0, 38, 44, 56, 34), // Feldip
    )

fun ProtectedAccess.isAtSelectedSpiritTree(selectionIndex: Int): Boolean {
    return player.isWithinDistance(spiritTreeTeleportLocations[selectionIndex], 10, 10, 10)
}

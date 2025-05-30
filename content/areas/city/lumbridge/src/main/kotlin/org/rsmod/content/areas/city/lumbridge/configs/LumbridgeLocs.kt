package org.rsmod.content.areas.city.lumbridge.configs

import org.rsmod.api.type.refs.loc.LocReferences

internal typealias lumbridge_locs = LumbridgeLocs

object LumbridgeLocs : LocReferences() {
    val winch = find("winch", 3005742587901703282)
    val farmerfred_axe_logs = find("log_withaxe", 3945491204872604074)
    val farmerfred_logs = find("log_withoutaxe", 4989925034077424035)
}

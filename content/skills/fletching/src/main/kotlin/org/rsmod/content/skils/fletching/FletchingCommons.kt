package org.rsmod.content.skils.fletching

import org.rsmod.api.config.refs.params
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.fletchingLvl
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType


internal fun ProtectedAccess.hasFletchingLevel(
    type: UnpackedObjType,
    ): Boolean {
        val levelRequirement = type.param(params.levelrequire)
        if (player.fletchingLvl < levelRequirement) {
            val message =
                "You need a Fletching level of ${levelRequirement} to make ${ocName(type.param(params.skill_productitem)).lowercase()}."
        mes(message)
        return false
    }
    return true
}
class FletchingCommons {
}

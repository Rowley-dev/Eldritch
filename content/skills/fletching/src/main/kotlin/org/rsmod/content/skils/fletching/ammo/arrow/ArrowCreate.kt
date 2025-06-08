package org.rsmod.content.skils.fletching.ammo.arrow

import org.rsmod.api.config.Constants.dm_invspace
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.fletchingLvl
import org.rsmod.content.skils.fletching.hasFletchingLevel
import org.rsmod.game.inv.InvObj
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class ArrowCreate : PluginScript() {
    override fun ScriptContext.startup() {

    }

    private fun Dialogue.createArrowDialogue() {

    }
    private fun ProtectedAccess.createArrow(arrowhead: UnpackedObjType, item: InvObj) {
        if (!canFletch(arrowhead)) {
            return
        }

    }

    private fun ProtectedAccess.canFletch(
        arrowhead: UnpackedObjType
    ): Boolean {
        val headlessArrowCount = inv.count(arrows.headless_arrows as UnpackedObjType)
        val arrowheadCount = inv.count(arrowhead)
        val minimumRequired = 15

        if (!hasFletchingLevel(arrowhead)) {
            return false
        }
        if (headlessArrowCount < minimumRequired) {
            mes("You do not have enough ${ocName(arrows.headless_arrows)} to make that many.")
            return false
        }
        if (arrowheadCount < minimumRequired) {
            mes("You do not have enough ${ocName(arrowhead)} to make that many.")
            return false
        }
        if (arrowhead == arrowheads.broad_arrowheads) {
            mes("You need to unlock the ability to create broad arrows.")
            return false
        }
        if (!hasRoom(arrowhead)) {
            mes(dm_invspace)
            return false
        }
        return true
    }

    private fun ProtectedAccess.hasRoom(
        arrowhead: UnpackedObjType,
    ): Boolean {
        val finishedArrow = arrowhead.param(params.skill_productitem)
        if (player.inv.isFull() && !inv.contains(finishedArrow)) {
            return false
        }
        return true
    }
}

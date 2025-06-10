package org.rsmod.content.skils.fletching

import jakarta.inject.Inject
import org.rsmod.api.config.Constants.dm_invspace
import org.rsmod.api.config.locXpParam
import org.rsmod.api.config.objXpParam
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.statAdvance
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.script.onPlayerQueueWithArgs
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.content.skils.fletching.ammo.arrow.arrowheads
import org.rsmod.content.skils.fletching.ammo.arrow.arrows
import org.rsmod.content.skils.fletching.ammo.bolt.bolt_tips
import org.rsmod.content.skils.fletching.ammo.bolt.finished_bolts
import org.rsmod.content.skils.fletching.ammo.bolt.unfinished_bolts
import org.rsmod.game.entity.Player
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class AmmoCreate
    @Inject
constructor(
        private val xpMods: XpModifiers,)
    : PluginScript() {

    var Player.skillMultiPreviousSelection by intVarp(varps.skillmulti_previousselection)

    override fun ScriptContext.startup() {
        onOpHeldU(content.fletching_arrowhead, arrows.headless_arrows) {
            createAmmoDialogue(it.first, it.second)
        }

        onOpHeldU(content.fletching_bolt, objs.feather) {
            createAmmoDialogue(it.first, it.second)
        }

        onOpHeldU(bolt_tips.jade_bolt_tips, finished_bolts.blurite_bolts) {
            createAmmoDialogue(it.first, it.second)
        }
        onOpHeldU(bolt_tips.redtopaz_bolt_tips, finished_bolts.steel_bolts) {
            createAmmoDialogue(it.first, it.second)
        }
        onOpHeldU(bolt_tips.sapphire_bolt_tips, finished_bolts.mithril_bolts) {
            createAmmoDialogue(it.first, it.second)
        }
        onOpHeldU(bolt_tips.emerald_bolt_tips, finished_bolts.mithril_bolts) {
            createAmmoDialogue(it.first, it.second)
        }
        onOpHeldU(bolt_tips.ruby_bolt_tips, finished_bolts.adamant_bolts) {
            createAmmoDialogue(it.first, it.second)
        }
        onOpHeldU(bolt_tips.diamond_bolt_tips, finished_bolts.adamant_bolts) {
            createAmmoDialogue(it.first, it.second)
        }
        onOpHeldU(bolt_tips.dragonstone_bolt_tips, finished_bolts.rune_bolts) {
            createAmmoDialogue(it.first, it.second)
        }
        onOpHeldU(bolt_tips.onyx_bolt_tips, finished_bolts.rune_bolts) {
            createAmmoDialogue(it.first, it.second)
        }
        onOpHeldU(arrowheads.arrow_shaft, objs.feather) {
            createAmmoDialogue(it.first, it.second)
        }

        onPlayerQueueWithArgs<Triple<Int, UnpackedObjType, UnpackedObjType>>(queues.make_item) {
            createArrow(it.args)
        }
    }

    private suspend fun ProtectedAccess.createAmmoDialogue(
        ammo: UnpackedObjType,
        secondary: UnpackedObjType
    ) {
        val setsToMake = skillMultiSelect(
            title = if (ammo.isContentType(content.fletching_arrowhead)) {
                "How many sets of 15 do you wish to complete?"
            } else if (ammo.isContentType(content.fletching_bolt_tip)){
                "How many sets of 10 would you like to tip?"
            } else {
                "How many sets of 10 do you wish to feather?"
            },
            quantitySelectionOptions = 3,
            maximumQuantity = 10,
            choices = listOf(ammo.param(params.skill_productitem)),
            defaultQuantity = player.skillMultiPreviousSelection,
        )

        weakQueue(queues.make_item, 1, Triple(setsToMake, ammo, secondary))
    }

    private fun ProtectedAccess.createArrow(triple: Triple<Int, UnpackedObjType, UnpackedObjType>) {
        val (setsToMake, ammo, secondary) = triple
        clearQueue(queues.make_item)

        if (!canFletch(ammo, secondary)) return

        val secondaryCount = inv.count(secondary)
        val ammoCount = inv.count(ammo)
        val minimumRequired = getMinimumRequired(ammo)
        val sequence = getFletchingSeq(ammo)

        val amountToMake = minOf(secondaryCount, ammoCount, minimumRequired)

        invDel(inv, ammo, amountToMake, secondary, amountToMake)
        invAdd(inv, ammo.param(params.skill_productitem), amountToMake)

        anim(sequence)

        val xp = (ammo.fletchXp / amountToMake) * xpMods.get(player, stats.fletching)
        player.statAdvance(stats.fletching, xp)

        // Requeue next batch ONLY if more remain
        if (setsToMake > 1) {
            weakQueue(queues.make_item, 3, Triple(setsToMake - 1, ammo, secondary))
        }
    }


    private fun ProtectedAccess.canFletch(
        ammo: UnpackedObjType,
        secondary: UnpackedObjType
    ): Boolean {
        val secondaryCount = inv.count(secondary)
        val ammoCount = inv.count(ammo)
        val minimumRequired = 15

        if (!hasFletchingLevel(ammo)) return false

        if (secondaryCount < minimumRequired) {
            mes("You do not have enough ${ocName(secondary)} to make that many.")
            return false
        }

        if (ammoCount < minimumRequired) {
            mes("You do not have enough ${ocName(ammo)} to make that many.")
            return false
        }

        if (ammo == arrowheads.broad_arrowheads) {
            mes("You need to unlock the ability to create broad arrows.")
            return false
        }

        if (ammo == unfinished_bolts.broad_unfinished_bolts) {
            mes("You need to unlock the ability to create broad bolts.")
            return false
        }

        if (!hasRoom(ammo)) {
            mes(dm_invspace)
            return false
        }

        return true
    }

    private fun ProtectedAccess.hasRoom(ammo: UnpackedObjType): Boolean {
        val finishedArrow = ammo.param(params.skill_productitem)
        return !(player.inv.isFull() && !inv.contains(finishedArrow))
    }

    companion object {
        val UnpackedObjType.fletchXp: Double by objXpParam(params.skill_xp)
}
    }

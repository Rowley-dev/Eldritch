package org.rsmod.content.items.jewellery.amulet_of_glory.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.queues
import org.rsmod.api.player.output.ClientScripts.chatboxMultiInit
import org.rsmod.api.player.output.ClientScripts.topLevelChatboxResetBackground
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.world.WorldRepository
import org.rsmod.api.script.*
import org.rsmod.content.items.jewellery.amulet_of_glory.configs.*
import org.rsmod.game.inv.InvObj
import org.rsmod.game.inv.isType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.isType
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class AmuletOfGlory @Inject constructor(
    private val worldRepo: WorldRepository
) : PluginScript() {

    override fun ScriptContext.startup() {
        onOpHeld4(content.amulet_of_glory) { gloryDialogue(it.type, it.obj, it.slot) }
        onPlayerQueueWithArgs<Pair<CoordGrid, InvObj>>(queues.jewellery_teleport) {
            val (coords, item) = it.args
            gloryTeleport(coords, item)
        }
    }

    private suspend fun ProtectedAccess.gloryDialogue(
        type: UnpackedObjType,
        item: InvObj,
        invSlot: Int
    ) {
        mes("You rub the amulet...")
        topLevelChatboxResetBackground(player)
        val selection = choice5(
            "Edgeville", 1,
            "Karamja", 2,
            "Draynor Village", 3,
            "Al Kharid", 4,
            "Nowhere", 5,
            title = "Where would you like to teleport to?"
        )
        when (selection) {
            1 -> gloryPreTeleport(type, item, AMULET_OF_GLORY_LOCATIONS["Edgeville"]!!, invSlot)
            2 -> gloryPreTeleport(type, item, AMULET_OF_GLORY_LOCATIONS["Karamja"]!!, invSlot)
            3 -> gloryPreTeleport(type, item, AMULET_OF_GLORY_LOCATIONS["Draynor Village"]!!, invSlot)
            4 -> gloryPreTeleport(type, item, AMULET_OF_GLORY_LOCATIONS["Al Kharid"]!!, invSlot)
            5 -> return
        }
    }

    private fun ProtectedAccess.gloryPreTeleport(
        type: UnpackedObjType,
        item: InvObj,
        selection: CoordGrid,
        invSlot: Int
    ) {
        val replacement = type.param(params.replacement_item_id)

        anim(amulet_of_glory_seqs.amulet_of_glory_teleport)
        spotanim(amulet_of_glory_spotanims.amulet_of_glory_spotanim, 1, 92)
        soundArea(worldRepo, player.coords, amulet_of_glory_synths.amulet_of_glory_synth, 0, 1, 5)
        queue(queues.jewellery_teleport, 5, selection to item)

        if (!type.isType(amulets_of_glory.amulet_of_glory_eternal)) {
            invReplace(inv, invSlot, 1, replacement)
        }
    }

    private fun ProtectedAccess.gloryTeleport(selection: CoordGrid, item: InvObj) {
        teleport(selection)
        onArrival(item)
        resetAnim()
        clearQueue(queues.jewellery_teleport)
    }

    private fun ProtectedAccess.onArrival(item: InvObj) {
        getMessage(item)?.let { mes(it) }
    }

    private fun ProtectedAccess.getMessage(item: InvObj): String? =
        when {
            item.isType(amulets_of_glory.amulet_of_glory_6) ||
                item.isType(amulets_of_glory.trail_amulet_of_glory_6) ->
                "<col=7f007f>Your amulet has five charges left.</col>"

            item.isType(amulets_of_glory.amulet_of_glory_5) ||
                item.isType(amulets_of_glory.trail_amulet_of_glory_5) ->
                "<col=7f007f>Your amulet has four charges left.</col>"

            item.isType(amulets_of_glory.amulet_of_glory_4) ||
                item.isType(amulets_of_glory.trail_amulet_of_glory_4) ->
                "<col=7f007f>Your amulet has three charges left.</col>"

            item.isType(amulets_of_glory.amulet_of_glory_3) ||
                item.isType(amulets_of_glory.trail_amulet_of_glory_3) ->
                "<col=7f007f>Your amulet has two charges left.</col>"

            item.isType(amulets_of_glory.amulet_of_glory_2) ||
                item.isType(amulets_of_glory.trail_amulet_of_glory_2) ->
                "<col=7f007f>Your amulet has one charge left.</col>"

            item.isType(amulets_of_glory.amulet_of_glory_1) ||
                item.isType(amulets_of_glory.trail_amulet_of_glory_1) ->
                "<col=7f007f>Your amulet has run out of charges.</col>"

            else -> null
        }
}

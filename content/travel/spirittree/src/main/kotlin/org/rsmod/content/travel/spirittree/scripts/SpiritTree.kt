package org.rsmod.content.travel.spirittree.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.BaseMesAnims.happy
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.player.output.ClientScripts.chatDefaultRestoreInput
import org.rsmod.api.player.output.ClientScripts.objboxSetButtons
import org.rsmod.api.player.output.ClientScripts.topLevelChatboxResetBackground
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLoc2
import org.rsmod.api.script.onPlayerQueueWithArgs
import org.rsmod.content.travel.spirittree.configs.isAtSelectedSpiritTree
import org.rsmod.content.travel.spirittree.configs.spiritTreeTeleportLocations
import org.rsmod.content.travel.spirittree.configs.spirit_tree_locs
import org.rsmod.content.travel.spirittree.configs.spirit_tree_npcs
import org.rsmod.content.travel.spirittree.configs.spirit_tree_objs
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.interf.IfSubType
import org.rsmod.game.type.loc.LocType
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class SpiritTree @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        onOpLoc1(content.spirit_tree) { attemptDialogue(it.loc, it.type) }
        onOpLoc2(content.spirit_tree) { openSpiritTreeMenu(it.type) }
        onPlayerQueueWithArgs<CoordGrid>(queues.spirit_tree_teleport) {
            spiritTreeTeleport(it.args)
        }
    }

    private suspend fun ProtectedAccess.attemptDialogue(tree: BoundLocInfo, type: LocType) {
        startDialogue { spiritTreeDialogueGreeting(tree, type) }
    }

    private suspend fun ProtectedAccess.spiritTreeDialogueGreeting(
        tree: BoundLocInfo,
        type: LocType,
    ) {
        faceLoc(tree)

        if (type.id == spirit_tree_locs.spirittree_pog.id) {
            chatNpcSpecific(
                "Spirit Tree",
                spirit_tree_npcs.spirit_tree_chathead_pog,
                happy,
                NORMAL_GREETING,
            )
        } else if (
            type.id == spirit_tree_locs.spirittree_small_1op.id ||
                type.id == spirit_tree_locs.spirittree_small_2ops.id
        ) {
            chatNpcSpecific(
                "Spirit Tree",
                spirit_tree_npcs.spirit_tree_chathead_small,
                happy,
                NORMAL_GREETING,
            )
        } else {
            chatNpcSpecific(
                "Spirit Tree",
                spirit_tree_npcs.spirit_tree_chathead_big,
                happy,
                NORMAL_GREETING,
            )
        }

        openSpiritTreeMenu(type)
    }

    private suspend fun ProtectedAccess.spiritTreeDialogueAttemptTeleport(type: LocType) {
        if (type.id == spirit_tree_locs.spirittree_pog.id) {
            chatNpcSpecific(
                "Spirit Tree",
                spirit_tree_npcs.spirit_tree_chathead_pog,
                happy,
                CURRENT_LOCATION_DIALOGUE,
            )
        } else if (
            type.id == spirit_tree_locs.spirittree_small_1op.id ||
                type.id == spirit_tree_locs.spirittree_small_2ops.id
        ) {
            chatNpcSpecific(
                "Spirit Tree",
                spirit_tree_npcs.spirit_tree_chathead_small,
                happy,
                CURRENT_LOCATION_DIALOGUE,
            )
        } else {
            chatNpcSpecific(
                "Spirit Tree",
                spirit_tree_npcs.spirit_tree_chathead_big,
                happy,
                CURRENT_LOCATION_DIALOGUE,
            )
        }

        openSpiritTreeMenu(type)
    }

    private suspend fun ProtectedAccess.openSpiritTreeMenu(type: LocType) {
        ifOpenSub(interfaces.menu, components.mainmodal, IfSubType.Modal)

        val selectionIndex =
            menu(title = "Spirit Tree Locations", hotkeys = true, choices = selections)

        if (selectionIndex in spiritTreeTeleportLocations.indices) {
            if (isAtSelectedSpiritTree(selectionIndex)) {
                spiritTreeDialogueAttemptTeleport(type)
            } else {
                spiritTreePreTeleport(spiritTreeTeleportLocations[selectionIndex])
            }
        }
    }

    private fun ProtectedAccess.spiritTreePreTeleport(coord: CoordGrid) {
        anim(seqs.human_reachforladder, 15)
        queue(queues.spirit_tree_teleport, 3, coord)
        sendTreeChatBoxNoPause()
    }

    private suspend fun ProtectedAccess.spiritTreeTeleport(coord: CoordGrid) {
        ifClose()
        teleport(coord)
        sendTreeChatBoxPause()
    }

    // Sends objbox as an overlay with no "Click here to continue" so that teleport is called when
    // queue finishes.
    private fun ProtectedAccess.sendTreeChatBoxNoPause() {
        chatDefaultRestoreInput(player)
        topLevelChatboxResetBackground(player)
        ifOpenSub(interfaces.obj_dialogue, components.chatbox_chatmodal, IfSubType.Overlay)
        ifSetObj(components.objectbox_item, spirit_tree_objs.spirit_tree_dummy, 400)
        ifSetText(components.objectbox_text, TREE_CHATBOX_TELEPORT_TEXT)
    }

    // Objbox sent after teleport (suspends)
    private fun ProtectedAccess.sendTreeChatBoxPause() {
        chatDefaultRestoreInput(player)
        topLevelChatboxResetBackground(player)
        ifOpenSub(interfaces.obj_dialogue, components.chatbox_chatmodal, IfSubType.Modal)
        ifSetObj(components.objectbox_item, spirit_tree_objs.spirit_tree_dummy, 400)
        ifSetText(components.objectbox_text, TREE_CHATBOX_TELEPORT_TEXT)
        objboxSetButtons(player, "Click here to continue")
        ifSetEvents(components.objectbox_pbutton, 0..1, IfEvent.PauseButton)
    }

    private companion object {
        private const val TREE_CHATBOX_TELEPORT_TEXT =
            "You place your hands on the dry tough bark of the<br>spirit tree, and feel a surge of energy run through<br>your veins."

        private const val NORMAL_GREETING = "Hello gnome friend. Where would you like to go?"

        private const val CURRENT_LOCATION_DIALOGUE = "You're already here."

        private val selections =
            listOf(
                "Tree Gnome Village",
                "Gnome Stronghold",
                "Battlefield of Khazard",
                "Grand Exchange",
                "Feldip Hills",
                "Prifddinas",
                "<col=5f5f5f>Port Sarim</col>",
                "<col=5f5f5f>Etceteria</col>",
                "<col=5f5f5f>Brimhaven</col>",
                "<col=5f5f5f>Hosidius</col>",
                "<col=5f5f5f>Farming Guild</col>",
                "<col=5f5f5f>Your house (Yanille)",
                "<col=5f5f5f>Poison Waste</col>",
                "Cancel",
            )
    }
}

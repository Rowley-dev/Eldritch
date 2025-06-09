package org.rsmod.content.skils.fletching.ammo.arrow

import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.controllers
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.events.PlayerQueueEvents
import org.rsmod.api.player.output.ClientScripts.skillMultiInit
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.statAdvance
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.script.onPlayerQueueWithArgs
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.interf.IfSubType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

var Player.skillMultiPreviousSelection by intVarp(varps.skillmulti_previousselection)

internal typealias headless_arrow_objs = HeadlessArrowObjs
internal typealias headless_arrow_seqs = HeadlessArrowSeqs

internal object HeadlessArrowObjs : ObjReferences() {
    val headless_arrow = find("headless_arrow")
    val arrow_shaft = find("arrow_shaft")
}

internal object HeadlessArrowSeqs : SeqReferences() {
    val human_fletching_add_feather = find("human_fletching_add_feather")
}

internal object HeadlessArrowObjEdits : ObjEditor() {
    init {
        edit(headless_arrow_objs.headless_arrow) {
            param[params.skill_xp] = 15
        }
    }
}

class HeadlessArrow : PluginScript() {
    override fun ScriptContext.startup() {
        onOpHeldU(headless_arrow_objs.arrow_shaft, objs.feather) {
            attachFeather(it.first, it.second)
        }
        onPlayerQueueWithArgs(queues.make_item) { makeHeadlessArrow(it.args)}
    }

    private suspend fun ProtectedAccess.headlessArrowDialogue(
        first: ObjType, second: ObjType
    ): Int? {
        return skillMultiSelect(
            title = "How many sets of 15 do you wish to complete?",
            quantitySelectionOptions = 3,
            maximumQuantity = 10,
            choices = listOf(headless_arrow_objs.headless_arrow),
            defaultQuantity = player.skillMultiPreviousSelection,
        )
    }

    private suspend fun ProtectedAccess.attachFeather(
        first: ObjType, second: ObjType
    ) {
        val setsToMake = headlessArrowDialogue(first, second)
        if (setsToMake == null) return
        player.skillMultiPreviousSelection = setsToMake
        repeat(setsToMake) {
            if (!)
            weakQueue(queues.make_item, 2, first to second)
        }
    }

    private suspend fun ProtectedAccess.makeHeadlessArrow(pair: Pair<ObjType, ObjType>) {
        val (first, second) = pair
        invDel(inv, first, 15, second, 15)
        invAdd(inv, headless_arrow_objs.headless_arrow, 15)
        anim(headless_arrow_seqs.human_fletching_add_feather)
        player.statAdvance(stats.fletching, 15.0)


    }
}

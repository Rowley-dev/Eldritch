package org.rsmod.content.skils.fletching.ammo.arrow

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.events.PlayerQueueEvents
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.statAdvance
import org.rsmod.api.script.onOpHeldU
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

internal typealias headless_arrow_objs = HeadlessArrowObjs

internal object HeadlessArrowObjs : ObjReferences() {
    val headless_arrow = find("headless_arrow")
    val arrow_shaft = find("arrow_shaft")
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
    }

    private fun ProtectedAccess.attachFeather(first: ObjType, second: ObjType) {
        invDel(inv, first, 15, second, 15)
        invAdd(inv, headless_arrow_objs.headless_arrow, 15)
        player.statAdvance()
    }
}

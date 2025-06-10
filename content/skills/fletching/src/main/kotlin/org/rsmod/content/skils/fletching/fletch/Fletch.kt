package org.rsmod.content.skils.fletching.fletch

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpHeldU
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Fletch : PluginScript() {
    override fun ScriptContext.startup() {
        onOpHeldU(content.log, objs.knife) { createFletchDialogue() }
    }

    private fun ProtectedAccess.createFletchDialogue() {
       amountToMake = skillMultiSelect(
           title = "What would you like to make?",
           quantitySelectionOptions = 4,
           maximumQuantity = 28,
           choices = TODO(),
           defaultQuantity = player.skillMultiPreviousSelection,
       )
    }
}



package org.rsmod.content.skils.fletching.fletch

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.dbtables
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.vars.intVarp
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.script.onPlayerQueueWithArgs
import org.rsmod.game.dbtable.DbRow
import org.rsmod.game.dbtable.DbRowResolver
import org.rsmod.game.dbtable.DbTableResolver
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.isType

var Player.skillMultiPreviousSelection: Int by intVarp(varps.skillmulti_previousselection)

class Fletch
@Inject
constructor(private val rows: DbRowResolver, private val tables: DbTableResolver)
: PluginScript() {
    override fun ScriptContext.startup() {
        onOpHeldU(content.fletchable_log, objs.knife) { createFletchDialogue(it.first) }
        onPlayerQueueWithArgs<Pair<Int, ObjType>>(queues.fletch_whittle) { whittleItem(it.args)}
    }



    private suspend fun ProtectedAccess.createFletchDialogue(log: ObjType) {
        val choices = getChoices(log)
        val amountToWhittle = skillMultiSelect(
            title = "What would you like to make?",
            quantitySelectionOptions = 4,
            maximumQuantity = 28,
            choices = choices,
            defaultQuantity = player.skillMultiPreviousSelection,
        )
        if 
        weakQueue(queues.fletch_whittle, 1, amountToWhittle to item)
    }

    private suspend fun ProtectedAccess.whittleItem(pair: Pair<Int, ObjType>) {
        val (amountToWhittle, item) = pair

    }

    private fun ProtectedAccess.getChoices(log: ObjType) : List<ObjType> {
        if (log.isType(objs.logs)) {
            return LogData.LOGS.products

        if (log.isType(objs.oak_logs)) {
            return LogData.OAK.products
        }
        if (log.isType(objs.willow_logs)) {
            return LogData.WILLOW.products
        }
        if (log.isType(objs.maple_logs)) {
            return LogData.MAPLE.products
        }
        if (log.isType(objs.yew_logs)) {
            return LogData.YEW.products
        }
        if (log.isType(objs.magic_logs)) {
            return LogData.MAGIC.products
        }
        if (log.isType(objs.teak_logs)) {
            return LogData.TEAK.products
        }
        if (log.isType(objs.mahogany_logs)) {
            return LogData.MAHOGANY.products
        }
        return LogData.LOGS.products
    }
}



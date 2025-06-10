package org.rsmod.content.skils.fletching.fletch

import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.type.builders.dbrow.DbRowBuilder
import org.rsmod.api.type.builders.dbtable.DbTableBuilder
import org.rsmod.api.type.refs.dbcol.DbColumnReferences
import org.rsmod.api.type.refs.dbrow.DbRowReferences
import org.rsmod.api.type.refs.dbtable.DbTableReferences
import org.rsmod.game.type.obj.ObjType

internal typealias whittle_columns = WhittleItemDbColumns

internal typealias whittle_rows = WhittleItemDbRows

internal typealias whittle_tables = WhittleItemDbTables

internal object WhittleItemDbColumns : DbColumnReferences() {
    val raw = obj("fletching_whittle:raw_log")
    val product = obj("fletching_whittle:product")
    val product_amount = int("fletching_whittle:product_amount")
    val levelRequired = int("fletching_whittle:level_required")
    val xp = int("fletching_whittle:xp")
    val anim = seq("fletching_whittle:seq")
}

internal object WhittleItemDbRows : DbRowReferences() {
    val normal_log_arrow_shafts = find("normal_log_arrow_shafts")
    val normal_log_shortbow_u = find("normal_log_shortbow_u")
    val normal_log_longbow_u = find("normal_log_longbow_u")
    val oak_log_shortbow_u = find("oak_log_shortbow_u")
    val oak_log_longbow_u = find("oak_log_longbow_u")
    val willow_log_shortbow_u = find("willow_log_shortbow_u")
    val willow_log_longbow_u = find("willow_log_longbow_u")
    val maple_log_shortbow_u = find("maple_log_shortbow_u")
    val maple_log_longbow_u = find("maple_log_longbow_u")
    val yew_log_shortbow_u = find("yew_log_shortbow_u")
    val yew_log_longbow_u = find("yew_log_longbow_u")
    val magic_log_shortbow_u = find("magic_log_shortbow_u")
    val magic_log_longbow_u = find("magic_log_longbow_u")
    val crossbow_stock = find("crossbow_stock")
    val willow_crossbow_stock = find("willow_crossbow_stock")
    val teak_crossbow_stock = find("teak_crossbow_stock")
    val mahogany_crossbow_stock = find("mahogany_crossbow_stock")
    val maple_crossbow_stock = find("maple_crossbow_stock")
    val yew_crossbow_stock = find("yew_crossbow_stock")
}

internal object WhittleItemDbTables : DbTableReferences() {
    val whittle_items_table = find("fletching_whittle_item_table")
}

internal object WhittleItemDbTableBuilder : DbTableBuilder() {
    init {
        build("whittle_items_table") {
            column(WhittleItemDbColumns.raw)
            column(WhittleItemDbColumns.product)
            column(WhittleItemDbColumns.product_amount)
            column(WhittleItemDbColumns.levelRequired)
            column(WhittleItemDbColumns.xp)
            column(WhittleItemDbColumns.anim)
        }
    }
}

internal object WhitteItemDbRowBuilder : DbRowBuilder() {
    init {
        build("normal_log_arrow_shafts") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.arrow_shaft }
            column(WhittleItemDbColumns.product_amount) { value = 15 }
            column(WhittleItemDbColumns.levelRequired) { value = 1}
            column(WhittleItemDbColumns.xp) { value = 1 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("normal_log_shortbow_u") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.shortbow_u }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 1}
            column(WhittleItemDbColumns.xp) { value = 1 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("normal_log_longbow_u") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.longbow_u }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 1}
            column(WhittleItemDbColumns.xp) { value = 1 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("oak_log_shortbow_u") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.oak_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.oak_shortbow_u }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 20}
            column(WhittleItemDbColumns.xp) { value = 16 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("oak_log_longbow_u") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.oak_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.oak_longbow_u }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 25}
            column(WhittleItemDbColumns.xp) { value = 20 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("willow_log_shortbow_u") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.willow_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.willow_shortbow_u }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 35}
            column(WhittleItemDbColumns.xp) { value = 33 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("willow_log_longbow_u") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.willow_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.willow_longbow_u }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 40}
            column(WhittleItemDbColumns.xp) { value = 39 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("maple_log_shortbow_u") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.maple_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.maple_shortbow_u }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 50}
            column(WhittleItemDbColumns.xp) { value = 58 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("maple_log_longbow_u") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.maple_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.maple_longbow_u }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 55}
            column(WhittleItemDbColumns.xp) { value = 65 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("yew_log_shortbow_u") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.yew_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.yew_shortbow_u }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 65}
            column(WhittleItemDbColumns.xp) { value = 83 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("yew_log_longbow_u") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.yew_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.yew_longbow_u }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 70}
            column(WhittleItemDbColumns.xp) { value = 91 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("magic_log_shortbow_u") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.magic_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.magic_shortbow_u }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 80}
            column(WhittleItemDbColumns.xp) { value = 125 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("magic_log_longbow_u") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.magic_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.magic_longbow_u }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 85}
            column(WhittleItemDbColumns.xp) { value = 140 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("crossbow_stock") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.crossbow_stock }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 1}
            column(WhittleItemDbColumns.xp) { value = 1}
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("willow_crossbow_stock") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.willow_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.willow_crossbow_stock }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 35}
            column(WhittleItemDbColumns.xp) { value = 33 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("teak_crossbow_stock") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.teak_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.teak_crossbow_stock }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 50}
            column(WhittleItemDbColumns.xp) { value = 58 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("mahogany_crossbow_stock") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.mahogany_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.mahogany_crossbow_stock }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 65}
            column(WhittleItemDbColumns.xp) { value = 83 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("maple_crossbow_stock") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.maple_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.maple_crossbow_stock }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 70}
            column(WhittleItemDbColumns.xp) { value = 91 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }
        build("yew_crossbow_stock") {
            table = WhittleItemDbTables.whittle_items_table
            column(WhittleItemDbColumns.raw) { value = objs.yew_logs }
            column(WhittleItemDbColumns.product) { value = whittle_objs.yew_crossbow_stock }
            column(WhittleItemDbColumns.product_amount) { value = 1 }
            column(WhittleItemDbColumns.levelRequired) { value = 80}
            column(WhittleItemDbColumns.xp) { value = 125 }
            column(WhittleItemDbColumns.anim) { value = seqs.human_fletching }
        }

    }
}

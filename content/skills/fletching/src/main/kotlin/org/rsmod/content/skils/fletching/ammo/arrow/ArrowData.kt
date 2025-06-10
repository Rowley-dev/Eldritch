package org.rsmod.content.skils.fletching.ammo.arrow

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.obj.ObjType

internal typealias arrowheads = ArrowheadObjs
internal typealias arrows = ArrowObjs


internal object ArrowObjs : ObjReferences() {
    val bronze_arrows = find("bronze_arrow")
    val iron_arrows = find("iron_arrow")
    val steel_arrows = find("steel_arrow")
    val mithril_arrows = find("mithril_arrow")
    val adamant_arrows = find("adamant_arrow")
    val rune_arrows = find("rune_arrow")
    val dragon_arrows = find("dragon_arrow")
    val broad_arrows = find("slayer_broad_arrows")
    val amethyst_arrows = find("amethyst_arrow")

    val headless_arrows = find("headless_arrow")
}

internal object ArrowheadObjs : ObjReferences() {

    val bronze_arrowheads = find("bronze_arrowheads")
    val iron_arrowheads = find("iron_arrowheads")
    val steel_arrowheads = find("steel_arrowheads")
    val mithril_arrowheads = find("mithril_arrowheads")
    val adamant_arrowheads = find("adamant_arrowheads")
    val rune_arrowheads = find("rune_arrowheads")
    val dragon_arrowheads = find("dragon_arrowheads")
    val broad_arrowheads = find("slayer_broad_arrowhead")
    val amethyst_arrowheads = find("amethyst_arrowheads")
    val arrow_shaft = find("arrow_shaft")
}

internal object ArrowheadEdits : ObjEditor() {
    init {
        edit (arrowheads.arrow_shaft) {
            contentGroup = content.fletching_arrowhead
            param[params.levelrequire] = 1
            param[params.skill_xp] = PlayerStatMap.toFineXP(1.5).toInt()
            param[params.skill_productitem] = arrows.headless_arrows
        }

        edit (arrowheads.bronze_arrowheads) {
            contentGroup = content.fletching_arrowhead
            param[params.levelrequire] = 1
            param[params.skill_xp] = PlayerStatMap.toFineXP(1.95).toInt()
            param[params.skill_productitem] = arrows.bronze_arrows
        }

        edit (arrowheads.iron_arrowheads) {
            contentGroup = content.fletching_arrowhead
            param[params.levelrequire] = 15
            param[params.skill_xp] = PlayerStatMap.toFineXP(3.75).toInt()
            param[params.skill_productitem] = arrows.iron_arrows
        }

        edit (arrowheads.steel_arrowheads) {
            contentGroup = content.fletching_arrowhead
            param[params.levelrequire] = 30
            param[params.skill_xp] = PlayerStatMap.toFineXP(7.5).toInt()
            param[params.skill_productitem] = arrows.steel_arrows
        }

        edit (arrowheads.mithril_arrowheads) {
            contentGroup = content.fletching_arrowhead
            param[params.levelrequire] = 45
            param[params.skill_xp] = PlayerStatMap.toFineXP(11.25).toInt()
            param[params.skill_productitem] = arrows.mithril_arrows
        }

        edit (arrowheads.adamant_arrowheads) {
            contentGroup = content.fletching_arrowhead
            param[params.levelrequire] = 60
            param[params.skill_xp] = PlayerStatMap.toFineXP(15.0).toInt()
            param[params.skill_productitem] = arrows.adamant_arrows
        }

        edit (arrowheads.rune_arrowheads) {
            contentGroup = content.fletching_arrowhead
            param[params.levelrequire] = 75
            param[params.skill_xp] = PlayerStatMap.toFineXP(18.75).toInt()
            param[params.skill_productitem] = arrows.rune_arrows
        }

        edit (arrowheads.broad_arrowheads) {
            contentGroup = content.fletching_arrowhead
            param[params.levelrequire] = 52
            param[params.skill_xp] = PlayerStatMap.toFineXP(15.0).toInt()
            param[params.skill_productitem] = arrows.broad_arrows
        }

        edit (arrowheads.dragon_arrowheads) {
            contentGroup = content.fletching_arrowhead
            param[params.levelrequire] = 90
            param[params.skill_xp] = PlayerStatMap.toFineXP(22.5).toInt()
            param[params.skill_productitem] = arrows.dragon_arrows
        }

        edit (arrowheads.amethyst_arrowheads) {
            contentGroup = content.fletching_arrowhead
            param[params.levelrequire] = 82
            param[params.skill_xp] = PlayerStatMap.toFineXP(20.25).toInt()
            param[params.skill_productitem] = arrows.amethyst_arrows
        }

    }

}

package org.rsmod.content.skils.fletching.ammo.javelin

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.stat.PlayerStatMap

internal typealias finished_javelins = FinishedJavelinObjs
internal typealias javelin_heads = JavelinHeadObjs
internal typealias javelin_shafts = JavelinShaftObjs

internal object FinishedJavelinObjs : ObjReferences() {
    val bronze_javelins = find("bronze_javelin")
    val iron_javelins = find("iron_javelin")
    val steel_javelins = find("steel_javelin")
    val mithril_javelins = find("mithril_javelin")
    val adamant_javelins = find("adamant_javelin")
    val rune_javelins = find("rune_javelin")
    val dragon_javelins = find("dragon_javelin")
    val amethyst_javelins = find("amethyst_javelin")
}
internal object JavelinHeadObjs : ObjReferences() {
    val bronze_javelin_heads = find("bronze_javelin_head")
    val iron_javelin_heads = find("iron_javelin_head")
    val steel_javelin_heads = find("steel_javelin_head")
    val mithril_javelin_heads = find("mithril_javelin_head")
    val adamant_javelin_heads = find("adamant_javelin_head")
    val rune_javelin_heads = find("rune_javelin_head")
    val dragon_javelin_heads = find("dragon_javelin_head")
    val amethyst_javelin_heads = find("amethyst_javelin_head")
}
internal object JavelinShaftObjs : ObjReferences() {
    val javelin_shaft = find("javelin_shaft")
}

internal object JavelinHeadEdits : ObjEditor() {
    init {
        edit(javelin_heads.bronze_javelin_heads) {
            contentGroup = content.fletching_javelin
            param[params.levelrequire] = 10
            param[params.skill_xp] = PlayerStatMap.toFineXP(1.0).toInt()
            param[params.skill_productitem] = finished_javelins.bronze_javelins
        }
        edit(javelin_heads.iron_javelin_heads) {
            contentGroup = content.fletching_javelin
            param[params.levelrequire] = 17
            param[params.skill_xp] = PlayerStatMap.toFineXP(2.0).toInt()
            param[params.skill_productitem] = finished_javelins.iron_javelins
        }
        edit(javelin_heads.steel_javelin_heads) {
            contentGroup = content.fletching_javelin
            param[params.levelrequire] = 32
            param[params.skill_xp] = PlayerStatMap.toFineXP(5.0).toInt()
            param[params.skill_productitem] = finished_javelins.steel_javelins
        }
        edit(javelin_heads.mithril_javelin_heads) {
            contentGroup = content.fletching_javelin
            param[params.levelrequire] = 47
            param[params.skill_xp] = PlayerStatMap.toFineXP(8.0).toInt()
            param[params.skill_productitem] = finished_javelins.mithril_javelins
        }
        edit(javelin_heads.adamant_javelin_heads) {
            contentGroup = content.fletching_javelin
            param[params.levelrequire] = 62
            param[params.skill_xp] = PlayerStatMap.toFineXP(10.0).toInt()
            param[params.skill_productitem] = finished_javelins.adamant_javelins
        }
        edit(javelin_heads.rune_javelin_heads) {
            contentGroup = content.fletching_javelin
            param[params.levelrequire] = 77
            param[params.skill_xp] = PlayerStatMap.toFineXP(12.4).toInt()
            param[params.skill_productitem] = finished_javelins.rune_javelins
        }
        edit(javelin_heads.dragon_javelin_heads) {
            contentGroup = content.fletching_javelin
            param[params.levelrequire] = 92
            param[params.skill_xp] = PlayerStatMap.toFineXP(15.0).toInt()
            param[params.skill_productitem] = finished_javelins.dragon_javelins
        }
        edit(javelin_heads.amethyst_javelin_heads) {
            contentGroup = content.fletching_javelin
            param[params.levelrequire] = 84
            param[params.skill_xp] = PlayerStatMap.toFineXP(13.5).toInt()
            param[params.skill_productitem] = finished_javelins.amethyst_javelins
        }

    }
}

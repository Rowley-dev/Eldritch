package org.rsmod.content.skils.fletching.ammo.dart

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.stat.PlayerStatMap

internal typealias dart_tips = DartTipsObjs
internal typealias finished_darts = FinishedDartObjs

internal object FinishedDartObjs : ObjReferences() {
    val bronze_darts = find("bronze_dart")
    val iron_darts = find("iron_dart")
    val steel_darts = find("steel_dart")
    val black_darts = find("black_dart")
    val mithril_darts = find("mithril_dart")
    val adamant_darts = find("adamant_dart")
    val rune_darts = find("rune_dart")
    val dragon_darts = find("dragon_dart")
    val amethyst_darts = find("amethyst_dart")
}

internal object DartTipsObjs : ObjReferences() {
    val bronze_dart_tips = find("bronze_dart_tip")
    val iron_dart_tips = find("iron_dart_tip")
    val steel_dart_tips = find("steel_dart_tip")
    val mithril_dart_tips = find("mithril_dart_tip")
    val adamant_dart_tips = find("adamant_dart_tip")
    val rune_dart_tips = find("rune_dart_tip")
    val dragon_dart_tips = find("dragon_dart_tip")
    val amethyst_dart_tips = find("amethyst_dart_tip")
}

internal object DartTipEdits : ObjEditor () {
    init {
        edit(dart_tips.bronze_dart_tips) {
            contentGroup = content.fletching_dart
            param[params.levelrequire] = 10
            param[params.skill_xp] = PlayerStatMap.toFineXP(1.8).toInt()
            param[params.skill_productitem] = finished_darts.bronze_darts
        }
        edit(dart_tips.iron_dart_tips) {
            contentGroup = content.fletching_dart
            param[params.levelrequire] = 22
            param[params.skill_xp] = PlayerStatMap.toFineXP(3.8).toInt()
            param[params.skill_productitem] = finished_darts.iron_darts
        }
        edit(dart_tips.steel_dart_tips) {
            contentGroup = content.fletching_dart
            param[params.levelrequire] = 37
            param[params.skill_xp] = PlayerStatMap.toFineXP(7.5).toInt()
            param[params.skill_productitem] = finished_darts.steel_darts
        }
        edit(dart_tips.mithril_dart_tips) {
            contentGroup = content.fletching_dart
            param[params.levelrequire] = 52
            param[params.skill_xp] = PlayerStatMap.toFineXP(11.2).toInt()
            param[params.skill_productitem] = finished_darts.mithril_darts
        }
        edit(dart_tips.adamant_dart_tips) {
            contentGroup = content.fletching_dart
            param[params.levelrequire] = 67
            param[params.skill_xp] = PlayerStatMap.toFineXP(15.0).toInt()
            param[params.skill_productitem] = finished_darts.adamant_darts
        }
        edit(dart_tips.rune_dart_tips) {
            contentGroup = content.fletching_dart
            param[params.levelrequire] = 81
            param[params.skill_xp] = PlayerStatMap.toFineXP(18.8).toInt()
            param[params.skill_productitem] = finished_darts.rune_darts
        }
        edit(dart_tips.dragon_dart_tips) {
            contentGroup = content.fletching_dart
            param[params.levelrequire] = 95
            param[params.skill_xp] = PlayerStatMap.toFineXP(25.0).toInt()
            param[params.skill_productitem] = finished_darts.dragon_darts
        }
        edit(dart_tips.amethyst_dart_tips) {
            contentGroup = content.fletching_dart
            param[params.levelrequire] = 90
            param[params.skill_xp] = PlayerStatMap.toFineXP(21.0).toInt()
            param[params.skill_productitem] = finished_darts.amethyst_darts
        }
    }

}

package org.rsmod.content.skils.fletching.ammo.bolt

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.stat.PlayerStatMap

internal typealias finished_bolts = FinishedBoltObjs
internal typealias bolt_tips = BoltTipsObjs
internal typealias unfinished_bolts = UnfinishedBoltObjs

internal object FinishedBoltObjs : ObjReferences() {
    val bronze_bolts = find("bolt")
    val iron_bolts = find("xbows_crossbow_bolts_iron")
    val steel_bolts = find("xbows_crossbow_bolts_steel")
    val mithril_bolts = find("xbows_crossbow_bolts_mithril")
    val adamant_bolts = find("xbows_crossbow_bolts_adamantite")
    val rune_bolts = find("xbows_crossbow_bolts_runite")
    val dragon_bolts = find("dragon_bolts")
    val broad_bolts = find("slayer_broad_bolt")

    val silver_bolts = find("xbows_crossbow_bolts_silver")
    val blurite_bolts = find("xbows_crossbow_bolts_blurite")

    val blurite_tipped_jade_bolts = find("xbows_crossbow_bolts_blurite_tipped_jade")
    val steel_tipped_redtopaz_bolts = find("xbows_crossbow_bolts_steel_tipped_redtopaz")
    val mithril_tipped_sapphire_bolts = find("xbows_crossbow_bolts_mithril_tipped_sapphire")
    val mithril_tipped_emerald_bolts = find("xbows_crossbow_bolts_mithril_tipped_emerald")
    val adamant_tipped_ruby_bolts = find("xbows_crossbow_bolts_adamantite_tipped_ruby")
    val adamant_tipped_diamond_bolts = find("xbows_crossbow_bolts_adamantite_tipped_diamond")
    val rune_tipped_dragonstone_bolts = find("xbows_crossbow_bolts_runite_tipped_dragonstone")
    val rune_tipped_onyx_bolts = find("xbows_crossbow_bolts_runite_tipped_onyx")
    val broad_tipped_amethyst_bolts = find("slayer_broad_bolt_amethyst")

}

internal object BoltTipsObjs : ObjReferences() {
    val jade_bolt_tips = find("xbows_bolt_tips_jade")
    val redtopaz_bolt_tips = find("xbows_bolt_tips_redtopaz")
    val sapphire_bolt_tips = find("xbows_bolt_tips_sapphire")
    val emerald_bolt_tips = find("xbows_bolt_tips_emerald")
    val ruby_bolt_tips = find("xbows_bolt_tips_ruby")
    val diamond_bolt_tips = find("xbows_bolt_tips_diamond")
    val dragonstone_bolt_tips = find("xbows_bolt_tips_dragonstone")
    val onyx_bolt_tips = find("xbows_bolt_tips_onyx")
    val amethyst_bolt_tips = find("xbows_bolt_tips_amethyst")
    val opal_bolt_tips = find("opal_bolttips")
    val pearl_bolt_tips = find("pearl_bolttips")
    val barbed_bolt_tips = find("barbed_bolttips")
}

internal object BoltTipsEdits : ObjEditor() {
    init {
        edit(bolt_tips.jade_bolt_tips) {
            contentGroup = content.fletching_bolt_tip
            param[params.levelrequire] = 26
            param[params.skill_xp] = PlayerStatMap.toFineXP(2.4).toInt()
            param[params.skill_productitem] = finished_bolts.blurite_tipped_jade_bolts
        }
        edit(bolt_tips.redtopaz_bolt_tips) {
            contentGroup = content.fletching_bolt_tip
            param[params.levelrequire] = 48
            param[params.skill_xp] = PlayerStatMap.toFineXP(3.9).toInt()
            param[params.skill_productitem] = finished_bolts.steel_tipped_redtopaz_bolts
        }
        edit(bolt_tips.sapphire_bolt_tips) {
            contentGroup = content.fletching_bolt_tip
            param[params.levelrequire] = 56
            param[params.skill_xp] = PlayerStatMap.toFineXP(4.7).toInt()
            param[params.skill_productitem] = finished_bolts.mithril_tipped_sapphire_bolts
        }
        edit(bolt_tips.emerald_bolt_tips) {
            contentGroup = content.fletching_bolt_tip
            param[params.levelrequire] = 55
            param[params.skill_xp] = PlayerStatMap.toFineXP(5.5).toInt()
            param[params.skill_productitem] = finished_bolts.mithril_tipped_emerald_bolts
        }
        edit(bolt_tips.ruby_bolt_tips) {
            contentGroup = content.fletching_bolt_tip
            param[params.levelrequire] = 63
            param[params.skill_xp] = PlayerStatMap.toFineXP(6.3).toInt()
            param[params.skill_productitem] = finished_bolts.adamant_tipped_ruby_bolts
        }
        edit(bolt_tips.diamond_bolt_tips) {
            contentGroup = content.fletching_bolt_tip
            param[params.levelrequire] = 65
            param[params.skill_xp] = PlayerStatMap.toFineXP(7.0).toInt()
            param[params.skill_productitem] = finished_bolts.adamant_tipped_diamond_bolts
        }
        edit(bolt_tips.dragonstone_bolt_tips) {
            contentGroup = content.fletching_bolt_tip
            param[params.levelrequire] = 71
            param[params.skill_xp] = PlayerStatMap.toFineXP(8.2).toInt()
            param[params.skill_productitem] = finished_bolts.rune_tipped_dragonstone_bolts
        }
        edit(bolt_tips.onyx_bolt_tips) {
            contentGroup = content.fletching_bolt_tip
            param[params.levelrequire] = 73
            param[params.skill_xp] = PlayerStatMap.toFineXP(9.4).toInt()
            param[params.skill_productitem] = finished_bolts.rune_tipped_onyx_bolts
        }
        edit(bolt_tips.amethyst_bolt_tips) {
            contentGroup = content.fletching_bolt_tip
            param[params.levelrequire] = 76
            param[params.skill_xp] = PlayerStatMap.toFineXP(10.6).toInt()
            param[params.skill_productitem] = finished_bolts.broad_tipped_amethyst_bolts
        }
    }
}

internal object UnfinishedBoltObjs : ObjReferences() {
    val bronze_unfinished_bolts = find("xbows_crossbow_bolts_bronze_unfeathered")
    val iron_unfinished_bolts = find("xbows_crossbow_bolts_iron_unfeathered")
    val steel_unfinished_bolts = find("xbows_crossbow_bolts_steel_unfeathered")
    val mithril_unfinished_bolts = find("xbows_crossbow_bolts_mithril_unfeathered")
    val adamant_unfinished_bolts = find("xbows_crossbow_bolts_adamantite_unfeathered")
    val rune_unfinished_bolts = find("xbows_crossbow_bolts_runite_unfeathered")
    val dragon_unfinished_bolts = find("dragon_bolts_unfeathered")
    val blurite_unfinished_bolts = find("xbows_crossbow_bolts_blurite_unfeathered")
    val silver_unfinished_bolts = find("xbows_crossbow_bolts_silver_unfeathered")
    val broad_unfinished_bolts = find("slayer_broad_bolt_unfinished")
}

internal object UnfinishedBoltEdits : ObjEditor() {
    init {
        edit(unfinished_bolts.iron_unfinished_bolts) {
            contentGroup = content.fletching_bolt
            param[params.levelrequire] = 39
            param[params.skill_xp] = PlayerStatMap.toFineXP(1.5).toInt()
            param[params.skill_productitem] = finished_bolts.iron_bolts
        }
        edit(unfinished_bolts.steel_unfinished_bolts) {
            contentGroup = content.fletching_bolt
            param[params.levelrequire] = 46
            param[params.skill_xp] = PlayerStatMap.toFineXP(3.5).toInt()
            param[params.skill_productitem] = finished_bolts.steel_bolts
        }
        edit(unfinished_bolts.mithril_unfinished_bolts) {
            contentGroup = content.fletching_bolt
            param[params.levelrequire] = 54
            param[params.skill_xp] = PlayerStatMap.toFineXP(5.0).toInt()
            param[params.skill_productitem] = finished_bolts.mithril_bolts
        }
        edit(unfinished_bolts.adamant_unfinished_bolts) {
            contentGroup = content.fletching_bolt
            param[params.levelrequire] = 61
            param[params.skill_xp] = PlayerStatMap.toFineXP(7.0).toInt()
            param[params.skill_productitem] = finished_bolts.adamant_bolts
        }
        edit(unfinished_bolts.rune_unfinished_bolts) {
            contentGroup = content.fletching_bolt
            param[params.levelrequire] = 69
            param[params.skill_xp] = PlayerStatMap.toFineXP(10.0).toInt()
            param[params.skill_productitem] = finished_bolts.rune_bolts
        }
        edit(unfinished_bolts.dragon_unfinished_bolts) {
            contentGroup = content.fletching_bolt
            param[params.levelrequire] = 84
            param[params.skill_xp] = PlayerStatMap.toFineXP(12.0).toInt()
            param[params.skill_productitem] = finished_bolts.dragon_bolts
        }
        edit(unfinished_bolts.blurite_unfinished_bolts) {
            contentGroup = content.fletching_bolt
            param[params.levelrequire] = 24
            param[params.skill_xp] = PlayerStatMap.toFineXP(1.0).toInt()
            param[params.skill_productitem] = finished_bolts.blurite_bolts
        }
        edit(unfinished_bolts.silver_unfinished_bolts) {
            contentGroup = content.fletching_bolt
            param[params.levelrequire] = 43
            param[params.skill_xp] = PlayerStatMap.toFineXP(2.5).toInt()
            param[params.skill_productitem] = finished_bolts.silver_bolts
        }
        edit(unfinished_bolts.bronze_unfinished_bolts) {
            contentGroup = content.fletching_bolt
            param[params.levelrequire] = 9
            param[params.skill_xp] = PlayerStatMap.toFineXP(0.5).toInt()
            param[params.skill_productitem] = finished_bolts.bronze_bolts
        }
        edit(unfinished_bolts.broad_unfinished_bolts) {
            contentGroup = content.fletching_bolt
            param[params.levelrequire] = 55
            param[params.skill_xp] = PlayerStatMap.toFineXP(3.0).toInt()
            param[params.skill_productitem] = finished_bolts.broad_bolts
        }
    }

}

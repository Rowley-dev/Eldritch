package org.rsmod.content.skils.fletching

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.fletchingLvl
import org.rsmod.api.type.refs.content.ContentReferences
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.content.skils.fletching.ammo.bolt.finished_bolts
import org.rsmod.content.skils.fletching.ammo.bolt.unfinished_bolts
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.isType
import org.rsmod.game.type.seq.SeqType

internal typealias fletching_seqs = FletchingSeqs


internal object FletchingSeqs : SeqReferences() {
    val human_fletching_add_feather = find("human_fletching_add_feather")
    val human_fletching_add_arrowhead = find("human_fletching_add_arrow_tips")
    val human_fletching_add_bolt_feathers_bronze = find("human_fletching_add_bolt_feathers_bronze")
    val human_fletching_add_bolt_feathers_iron = find("human_fletching_add_bolt_feathers_iron")
    val human_fletching_add_bolt_feathers_steel = find("human_fletching_add_bolt_feathers_steel")
    val human_fletching_add_bolt_feathers_mithril = find("human_fletching_add_bolt_feathers_mithril")
    val human_fletching_add_bolt_feathers_adamant = find("human_fletching_add_bolt_feathers_adamant")
    val human_fletching_add_bolt_feathers_rune = find("human_fletching_add_bolt_feathers_rune")
    val human_fletching_add_bolt_feathers_dragon = find("human_fletching_add_bolt_feathers_dragon")
    val human_fletching_add_bolt_feathers_blurite = find("human_fletching_add_bolt_feathers_blurite")
    val human_fletching_add_bolt_tips_bronze = find("human_fletching_add_bolt_tips_bronze")
    val human_fletching_add_bolt_tips_iron = find("human_fletching_add_bolt_tips_iron")
    val human_fletching_add_bolt_tips_steel = find("human_fletching_add_bolt_tips_steel")
    val human_fletching_add_bolt_tips_mithril = find("human_fletching_add_bolt_tips_mithril")
    val human_fletching_add_bolt_tips_adamant = find("human_fletching_add_bolt_tips_adamant")
    val human_fletching_add_bolt_tips_rune = find("human_fletching_add_bolt_tips_rune")
    val human_fletching_add_bolt_tips_dragon = find("human_fletching_add_bolt_tips_dragon")
    val human_fletching_add_bolt_tips_blurite = find("human_fletching_add_bolt_tips_blurite")
    val human_fletching_add_dart_feathers_bronze = find("human_fletching_add_dart_feathers_bronze")
    val human_fletching_add_dart_feathers_iron = find("human_fletching_add_dart_feathers_iron")
    val human_fletching_add_dart_feathers_steel = find("human_fletching_add_dart_feathers_steel")
    val human_fletching_add_dart_feathers_mithril = find("human_fletching_add_dart_feathers_mithril")
    val human_fletching_add_dart_feathers_adamant = find("human_fletching_add_dart_feathers_adamant")
    val human_fletching_add_dart_feathers_rune = find("human_fletching_add_dart_feathers_rune")
    val human_fletching_add_dart_feathers_dragon = find("human_fletching_add_dart_feathers_dragon")
}

internal fun ProtectedAccess.getFletchingSeq(
    type: UnpackedObjType,
): SeqType {
    return when {
        type.isContentType(content.fletching_arrowhead) -> fletching_seqs.human_fletching_add_arrowhead
        type.isType(unfinished_bolts.iron_unfinished_bolts) -> fletching_seqs.human_fletching_add_bolt_feathers_iron
        type.isType(unfinished_bolts.bronze_unfinished_bolts) -> fletching_seqs.human_fletching_add_bolt_feathers_bronze
        type.isType(unfinished_bolts.steel_unfinished_bolts) -> fletching_seqs.human_fletching_add_bolt_feathers_steel
        type.isType(unfinished_bolts.mithril_unfinished_bolts) -> fletching_seqs.human_fletching_add_bolt_feathers_mithril
        type.isType(unfinished_bolts.adamant_unfinished_bolts) -> fletching_seqs.human_fletching_add_bolt_feathers_adamant
        type.isType(unfinished_bolts.rune_unfinished_bolts) -> fletching_seqs.human_fletching_add_bolt_feathers_rune
        type.isType(unfinished_bolts.dragon_unfinished_bolts) -> fletching_seqs.human_fletching_add_bolt_feathers_dragon
        type.isType(unfinished_bolts.blurite_unfinished_bolts) -> fletching_seqs.human_fletching_add_bolt_feathers_blurite
        type.isType(finished_bolts.bronze_bolts) -> fletching_seqs.human_fletching_add_bolt_tips_bronze
        type.isType(finished_bolts.iron_bolts) -> fletching_seqs.human_fletching_add_bolt_tips_iron
        type.isType(finished_bolts.steel_bolts) -> fletching_seqs.human_fletching_add_bolt_tips_steel
        type.isType(finished_bolts.mithril_bolts) -> fletching_seqs.human_fletching_add_bolt_tips_mithril
        type.isType(finished_bolts.adamant_bolts) -> fletching_seqs.human_fletching_add_bolt_tips_adamant
        type.isType(finished_bolts.rune_bolts) -> fletching_seqs.human_fletching_add_bolt_tips_rune
        type.isType(finished_bolts.dragon_bolts) -> fletching_seqs.human_fletching_add_bolt_tips_dragon
        type.isType(finished_bolts.blurite_bolts) -> fletching_seqs.human_fletching_add_bolt_tips_blurite
        else -> fletching_seqs.human_fletching_add_arrowhead
    }
}

/* Returns the amount of ammo to make on each action in ammoCreate() */
internal fun ProtectedAccess.getMinimumRequired(ammo: UnpackedObjType): Int {
    return when {
        ammo.isContentType(content.fletching_arrowhead) -> 15
        else -> 10
    }
}

internal fun ProtectedAccess.hasFletchingLevel(
    type: UnpackedObjType,
    ): Boolean {
        val levelRequirement = type.param(params.levelrequire)
        if (player.fletchingLvl < levelRequirement) {
            val message =
                "You need a Fletching level of ${levelRequirement} to make ${ocName(type.param(params.skill_productitem)).lowercase()}."
        mes(message)
        return false
    }
    return true
}
//TODO(): What are the specific messages for each type of fletching?
internal fun ProtectedAccess.getMessageForFletchingLevel(
    type: UnpackedObjType,
): String {
    if (type.isContentType(content.fletching_arrowhead)) {
        return "You need a Fletching level of ${type.param(params.levelrequire)} to make ${ocName(type.param(params.skill_productitem)).lowercase()}s."
    }
    if (type.isContentType(content.fletching_bolt_tip)) {
        return "You need a Fletching level of ${type.param(params.levelrequire)} to make ${ocName(type.param(params.skill_productitem)).lowercase()}s."
    }
    return "You need a Fletching level of ${type.param(params.levelrequire)} to make ${ocName(type.param(params.skill_productitem)).lowercase()}."
}
class FletchingCommons {
}

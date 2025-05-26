package org.rsmod.api.combat.formulas.attributes.collector

import jakarta.inject.Inject
import java.util.EnumSet
import kotlin.collections.plusAssign
import org.rsmod.api.combat.commons.magic.MagicSpellChecks
import org.rsmod.api.combat.commons.magic.Spellbook
import org.rsmod.api.combat.formulas.attributes.CombatSpellAttributes
import org.rsmod.api.combat.formulas.attributes.CombatStaffAttributes
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.front
import org.rsmod.api.player.hands
import org.rsmod.api.player.hat
import org.rsmod.api.player.lefthand
import org.rsmod.api.player.legs
import org.rsmod.api.player.righthand
import org.rsmod.api.player.ring
import org.rsmod.api.player.torso
import org.rsmod.api.player.worn.EquipmentChecks
import org.rsmod.api.random.GameRandom
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.isAnyType
import org.rsmod.game.inv.isType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.isType

public class CombatMagicAttributeCollector @Inject constructor(private val objTypes: ObjTypeList) {
    // `random` is an explicit parameter to indicate that this function relies on randomness
    // for certain effects, such as the Brimstone ring proc.
    public fun spellCollect(
        player: Player,
        spell: ObjType,
        spellbook: Spellbook?,
        usedSunfireRune: Boolean,
        random: GameRandom,
    ): EnumSet<CombatSpellAttributes> {
        val attributes = EnumSet.noneOf(CombatSpellAttributes::class.java)

        if (spellbook == Spellbook.Standard) {
            attributes += CombatSpellAttributes.StandardBook
        }

        val spellAttribute =
            when {
                MagicSpellChecks.isWindSpell(spell) -> CombatSpellAttributes.WindSpell
                MagicSpellChecks.isWaterSpell(spell) -> CombatSpellAttributes.WaterSpell
                MagicSpellChecks.isEarthSpell(spell) -> CombatSpellAttributes.EarthSpell
                MagicSpellChecks.isFireSpell(spell) -> CombatSpellAttributes.FireSpell
                MagicSpellChecks.isBindSpell(spell) -> CombatSpellAttributes.BindSpell
                spell.isType(objs.spell_magic_dart) -> CombatSpellAttributes.MagicDart
                else -> null
            }

        if (spellAttribute != null) {
            attributes += spellAttribute
        }

        val helm = player.hat
        val body = player.torso
        val legs = player.legs
        val weapon = player.righthand
        val amulet = player.front

        val hasImprovedAhrimPassive =
            EquipmentChecks.isAhrimSet(helm, body, legs, weapon) &&
                amulet.isType(objs.amulet_of_the_damned_full)

        if (hasImprovedAhrimPassive && random.randomBoolean(4)) {
            attributes += CombatSpellAttributes.AhrimPassive
        }

        val ring = player.ring
        if (ring.isType(objs.brimstone_ring) && random.randomBoolean(4)) {
            attributes += CombatSpellAttributes.BrimstonePassive
        }

        if (player.vars[varbits.buff_mark_of_darkness_disabled] == 1) {
            attributes += CombatSpellAttributes.MarkOfDarkness
        }

        if (weapon.isType(objs.slayers_staff_e)) {
            attributes += CombatSpellAttributes.SlayerStaffE
        }

        val gloves = player.hands
        if (gloves.isType(objs.chaos_gauntlets)) {
            attributes += CombatSpellAttributes.ChaosGauntlets
        }

        if (MagicSpellChecks.isBoltSpell(spell)) {
            attributes += CombatSpellAttributes.BoltSpell
        }

        val chargeBuffCooldown = player.vars[varbits.buff_charge_spell_disabled]
        if (MagicSpellChecks.isGodSpell(spell) && chargeBuffCooldown > 0) {
            attributes += CombatSpellAttributes.ChargeSpell
        }

        if (MagicSpellChecks.isDemonbaneSpell(spell)) {
            attributes += CombatSpellAttributes.Demonbane
        }

        if (EquipmentChecks.isSmokeStaff(weapon)) {
            attributes += CombatSpellAttributes.SmokeStaff
        }

        if (player.skullIcon == constants.skullicon_forinthry_surge) {
            attributes += CombatSpellAttributes.ForinthrySurge
        }

        if (amulet.isType(objs.amulet_of_avarice)) {
            attributes += CombatSpellAttributes.AmuletOfAvarice
        } else if (amulet.isType(objs.salve_amulet_ei)) {
            attributes += CombatSpellAttributes.SalveAmuletEi
        } else if (amulet.isType(objs.salve_amulet_i)) {
            attributes += CombatSpellAttributes.SalveAmuletI
        }

        val helmType = objTypes.getOrNull(player.hat)
        if (helmType != null && helmType.hasImbuedBlackMaskAttribute()) {
            attributes += CombatSpellAttributes.BlackMaskI
        }

        val weaponAttribute =
            when {
                weapon.isType(objs.dragon_hunter_wand) -> {
                    CombatSpellAttributes.DragonHunterWand
                }

                weapon.isType(objs.dragon_hunter_lance) -> {
                    CombatSpellAttributes.DragonHunterLance
                }

                EquipmentChecks.isDragonHunterCrossbow(weapon) -> {
                    CombatSpellAttributes.DragonHunterCrossbow
                }

                weapon.isAnyType(
                    objs.accursed_sceptre,
                    objs.accursed_sceptre_a,
                    objs.thammarons_sceptre,
                    objs.thammarons_sceptre_a,
                ) -> {
                    CombatSpellAttributes.RevenantWeapon
                }

                weapon.isType(objs.purging_staff) -> {
                    CombatSpellAttributes.PurgingStaff
                }

                else -> null
            }

        if (weaponAttribute != null) {
            attributes += weaponAttribute
        }

        val shield = player.lefthand

        val shieldAttribute =
            when {
                shield.isType(objs.tome_of_water) -> CombatSpellAttributes.WaterTome
                shield.isType(objs.tome_of_earth) -> CombatSpellAttributes.EarthTome
                shield.isType(objs.tome_of_fire) -> CombatSpellAttributes.FireTome
                else -> null
            }

        if (shieldAttribute != null) {
            attributes += shieldAttribute
        }

        if (MagicSpellChecks.isFireSpell(spell) && usedSunfireRune) {
            attributes += CombatSpellAttributes.SunfireRunePassive
        }

        if (ring.isType(objs.efaritays_aid)) {
            attributes += CombatSpellAttributes.EfaritaysAid
        }

        return attributes
    }

    // `random` is an explicit parameter to indicate that this function relies on randomness
    // for certain effects, such as the Brimstone ring proc.
    public fun staffCollect(player: Player, random: GameRandom): EnumSet<CombatStaffAttributes> {
        val attributes = EnumSet.noneOf(CombatStaffAttributes::class.java)

        val ring = player.ring
        if (ring.isType(objs.brimstone_ring) && random.randomBoolean(4)) {
            attributes += CombatStaffAttributes.BrimstonePassive
        }

        if (player.skullIcon == constants.skullicon_forinthry_surge) {
            attributes += CombatStaffAttributes.ForinthrySurge
        }

        val amulet = player.front
        if (amulet.isType(objs.amulet_of_avarice)) {
            attributes += CombatStaffAttributes.AmuletOfAvarice
        } else if (amulet.isType(objs.salve_amulet_ei)) {
            attributes += CombatStaffAttributes.SalveAmuletEi
        } else if (amulet.isType(objs.salve_amulet_i)) {
            attributes += CombatStaffAttributes.SalveAmuletI
        }

        val helmType = objTypes.getOrNull(player.hat)
        if (helmType != null && helmType.hasImbuedBlackMaskAttribute()) {
            attributes += CombatStaffAttributes.BlackMaskI
        }

        val weapon = player.righthand

        val weaponAttribute =
            when {
                EquipmentChecks.isTumekensShadow(weapon) -> {
                    CombatStaffAttributes.TumekensShadow
                }

                weapon.isAnyType(
                    objs.accursed_sceptre,
                    objs.accursed_sceptre_a,
                    objs.thammarons_sceptre,
                    objs.thammarons_sceptre_a,
                ) -> {
                    CombatStaffAttributes.RevenantWeapon
                }

                else -> null
            }

        if (weaponAttribute != null) {
            attributes += weaponAttribute
        }

        return attributes
    }

    private fun UnpackedObjType.hasImbuedBlackMaskAttribute(): Boolean {
        return param(params.blackmask_imbued) != 0 || param(params.slayer_helm_imbued) != 0
    }
}

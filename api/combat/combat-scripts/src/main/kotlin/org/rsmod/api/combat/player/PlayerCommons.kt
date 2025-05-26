package org.rsmod.api.combat.player

import kotlin.math.min
import org.rsmod.api.combat.MAGIC_ATTACK_RANGE
import org.rsmod.api.combat.MAX_ATTACK_RANGE
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.magic.MagicSpell
import org.rsmod.api.combat.commons.styles.AttackStyle
import org.rsmod.api.combat.commons.styles.MagicAttackStyle
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.styles.RangedAttackStyle
import org.rsmod.api.combat.commons.types.AttackType
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.commons.types.RangedAttackType
import org.rsmod.api.combat.manager.MagicRuneManager
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.righthand
import org.rsmod.api.specials.SpecialAttack
import org.rsmod.api.specials.SpecialAttackRegistry
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.api.spells.MagicSpellRegistry
import org.rsmod.api.spells.autocast.AutocastWeapons
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.InvObj
import org.rsmod.game.type.obj.ObjTypeList

internal fun ProtectedAccess.attackRange(style: AttackStyle?): Int =
    if (autocastSpell > 0) {
        MAGIC_ATTACK_RANGE
    } else {
        weaponAttackRange(style)
    }

internal fun ProtectedAccess.weaponAttackRange(style: AttackStyle?): Int {
    var attackRange = 1

    val weapon = player.righthand
    if (weapon != null) {
        val weaponRange = ocParam(weapon, params.attackrange)
        val increase = if (style == AttackStyle.LongrangeRanged) 2 else 0
        attackRange = weaponRange + increase
    }

    check(attackRange > 0) {
        "Attack range must be greater than 0: range=$attackRange, weapon=$weapon, style=$style"
    }

    return min(MAX_ATTACK_RANGE, attackRange)
}

internal fun ProtectedAccess.resolveCombatAttack(
    weapon: InvObj?,
    type: AttackType?,
    style: AttackStyle?,
    spell: MagicSpell?,
): CombatAttack.PlayerAttack =
    when {
        spell != null -> {
            CombatAttack.Spell(weapon, spell, defensiveCasting)
        }

        type?.isMagic == true -> {
            check(style == null || style.isMagic) {
                "Expected attack style to be magic-based: style=$style, type=$type, weapon=$weapon"
            }
            checkNotNull(weapon) {
                "Expected valid weapon for magic-staff-based attack: style=$style, type=$type"
            }
            val magicStyle = MagicAttackStyle.from(style)
            CombatAttack.Staff(weapon, magicStyle)
        }

        type?.isRanged == true -> {
            check(style == null || style.isRanged) {
                "Expected attack style to be ranged-based: style=$style, type=$type, weapon=$weapon"
            }
            checkNotNull(weapon) {
                "Expected valid weapon for ranged-based attack: style=$style, type=$type"
            }
            val rangedType = RangedAttackType.from(type)
            val rangedStyle = RangedAttackStyle.from(style)
            CombatAttack.Ranged(weapon, rangedType, rangedStyle)
        }

        else -> {
            val meleeType = MeleeAttackType.from(type)
            val meleeStyle = MeleeAttackStyle.from(style)
            CombatAttack.Melee(weapon, meleeType, meleeStyle, combatStance)
        }
    }

internal fun ProtectedAccess.resolveAutocastSpell(
    objTypes: ObjTypeList,
    spells: MagicSpellRegistry,
    runes: MagicRuneManager,
    autocast: AutocastWeapons,
): MagicSpell? {
    if (!autocastEnabled) {
        return null
    }
    val weapon = player.righthand ?: return null
    val spell = spells.getAutocastSpell(autocastSpell) ?: return null

    val weaponType = objTypes[weapon]

    if (spell.spellbook != spellbook) {
        mes("You can't autocast that spell with your current active spellbook.")

        autocastEnabled = false
        autocast.reset(player, weaponType)
        return null
    }

    val isValidStaff = autocast.canStaffAutocast(player, weaponType, autocastSpell)
    if (!isValidStaff) {
        return null
    }

    val canCastSpell = runes.canCastSpell(player, spell)
    if (!canCastSpell) {
        autocastEnabled = false
        autocast.reset(player, weaponType)
        return null
    }

    return spell
}

internal suspend fun ProtectedAccess.activateMeleeSpecial(
    target: PathingEntity,
    attack: CombatAttack.Melee,
    specials: SpecialAttackRegistry,
    energy: SpecialAttackEnergy,
): Boolean {
    val weapon = attack.weapon ?: return false
    val special = specials[weapon] ?: return false
    if (special !is SpecialAttack.Melee) {
        return false
    }

    val specializedEnergy = energy.isSpecializedRequirement(special.energyInHundreds)
    if (!specializedEnergy) {
        val hasRequiredEnergy = energy.hasSpecialEnergy(player, special.energyInHundreds)
        if (!hasRequiredEnergy) {
            mes("You don't have enough power left.")
            return false
        }
    }

    // Official behavior: If a special attack reaches this point, the attack delay is always
    // applied. That is, even if the special fails (e.g., due to an unmet condition), the
    // interaction will either be canceled entirely or proceed with the delay.

    val reduceEnergy = special.attack(this, target, attack)
    if (reduceEnergy) {
        energy.takeSpecialEnergy(player, special.energyInHundreds)
    }
    return true
}

internal suspend fun ProtectedAccess.activateRangedSpecial(
    target: PathingEntity,
    attack: CombatAttack.Ranged,
    specials: SpecialAttackRegistry,
    energy: SpecialAttackEnergy,
): Boolean {
    val special = specials[attack.weapon] ?: return false
    if (special !is SpecialAttack.Ranged) {
        return false
    }

    val specializedEnergyReq = energy.isSpecializedRequirement(special.energyInHundreds)
    if (!specializedEnergyReq) {
        val hasRequiredEnergy = energy.hasSpecialEnergy(player, special.energyInHundreds)
        if (!hasRequiredEnergy) {
            mes("You don't have enough power left.")
            return false
        }
    }

    // Official behavior: If a special attack reaches this point, the attack delay is always
    // applied. That is, even if the special fails (e.g., due to an unmet condition), the
    // interaction will either be canceled entirely or proceed with the delay.

    val reduceEnergy = special.attack(this, target, attack)
    if (reduceEnergy) {
        energy.takeSpecialEnergy(player, special.energyInHundreds)
    }
    return true
}

internal suspend fun ProtectedAccess.activateMagicSpecial(
    target: PathingEntity,
    attack: CombatAttack.Staff,
    specials: SpecialAttackRegistry,
    energy: SpecialAttackEnergy,
): Boolean {
    val special = specials[attack.weapon] ?: return false
    if (special !is SpecialAttack.Magic) {
        return false
    }

    val specializedEnergyReq = energy.isSpecializedRequirement(special.energyInHundreds)
    if (!specializedEnergyReq) {
        val hasRequiredEnergy = energy.hasSpecialEnergy(player, special.energyInHundreds)
        if (!hasRequiredEnergy) {
            mes("You don't have enough power left.")
            return false
        }
    }

    // Official behavior: If a special attack reaches this point, the attack delay is always
    // applied. That is, even if the special fails (e.g., due to an unmet condition), the
    // interaction will either be canceled entirely or proceed with the delay.

    val reduceEnergy = special.attack(this, target, attack)
    if (reduceEnergy) {
        energy.takeSpecialEnergy(player, special.energyInHundreds)
    }
    return true
}

internal suspend fun ProtectedAccess.activateShieldSpecial(
    target: PathingEntity,
    shield: InvObj?,
    specials: SpecialAttackRegistry,
): Boolean = TODO()

internal fun ProtectedAccess.setPkVars(target: Player) {
    // TODO(combat): Set pk skull when applicable.
    pkPrey2 = pkPrey1
    pkPrey1 = target.uid

    target.pkPredator3 = target.pkPredator2
    target.pkPredator2 = target.pkPredator1
    target.pkPredator1 = player.uid

    target.lastCombat = mapClock
    target.lastCombatPvp = mapClock
    target.aggressiveNpc = null
}

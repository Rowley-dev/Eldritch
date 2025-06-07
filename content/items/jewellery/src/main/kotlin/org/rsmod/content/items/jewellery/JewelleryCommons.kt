package org.rsmod.content.items.jewellery

import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.queues
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.world.WorldRepository
import org.rsmod.content.items.jewellery.amulet_of_glory.configs.amulet_of_glory_seqs
import org.rsmod.content.items.jewellery.amulet_of_glory.configs.amulet_of_glory_spotanims
import org.rsmod.content.items.jewellery.amulet_of_glory.configs.amulet_of_glory_synths
import org.rsmod.content.items.jewellery.amulet_of_glory.configs.amulets_of_glory
import org.rsmod.game.inv.InvObj
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.isType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.map.CoordGrid

fun ProtectedAccess.jewelleryTeleportEffects(
    playerAnim: SeqType,
    teleSpotanim: SpotanimType,
    spotanimDelay: Int,
    spotanimHeight: Int,
    synth: SynthType,
    repo: WorldRepository,
) {
    anim(playerAnim)
    spotanim(teleSpotanim, spotanimDelay, spotanimHeight)
    soundArea(repo, player.coords, synth)
}

fun ProtectedAccess.queueJewelleryTeleport(
    delay: Int,
    destination: CoordGrid,
    item: InvObj
) {
    queue(queues.jewellery_teleport, delay, destination to item)
}

fun ProtectedAccess.afterJewelleryTeleport() {
   clearQueue(queues.jewellery_teleport)
    resetAnim()
}

fun ProtectedAccess.replaceJewelleryInventory(
    type: UnpackedObjType,
    invSlot: Int
) {
        val replacement = type.param(params.replacement_item_id)
        invReplace(inv, invSlot, 1, replacement)
}


package org.rsmod.api.player.output

import net.rsprot.protocol.game.outgoing.sound.SynthSound
import org.rsmod.game.entity.Player
import org.rsmod.game.type.synth.SynthType

/** @see [SynthSound] */
public fun Player.soundSynth(synth: SynthType, loops: Int = 1, delay: Int = 0) {
    client.write(SynthSound(synth.id, loops, delay))
}

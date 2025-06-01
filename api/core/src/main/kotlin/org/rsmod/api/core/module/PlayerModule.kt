package org.rsmod.api.core.module

import org.rsmod.api.player.music.MusicPlayer
import org.rsmod.api.player.protect.ProtectedAccessContextFactory
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.module.ExtendedModule

public object PlayerModule : ExtendedModule() {
    override fun bind() {
        bindInstance<MusicPlayer>()
        bindInstance<ProtectedAccessContextFactory>()
        bindInstance<ProtectedAccessLauncher>()
    }
}

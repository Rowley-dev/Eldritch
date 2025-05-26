package org.rsmod.api.spells.runes.compact

import jakarta.inject.Inject
import org.rsmod.game.enums.EnumTypeMapResolver
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class CompactRuneScript
@Inject
constructor(
    private val repo: CompactRuneRepository,
    private val enumResolver: EnumTypeMapResolver,
) : PluginScript() {
    override fun ScriptContext.startup() {
        repo.init(enumResolver)
    }
}

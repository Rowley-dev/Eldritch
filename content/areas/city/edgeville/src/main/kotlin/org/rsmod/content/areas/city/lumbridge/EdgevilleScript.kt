package org.rsmod.content.areas.city.lumbridge

import jakarta.inject.Inject
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class EdgevilleScript @Inject
constructor(private val locRepo: LocRepository, private val objRepo: ObjRepository) :
    PluginScript() {
    override fun ScriptContext.startup() {

    }

}

package org.rsmod.content.areas.city.lumbridge.map

import org.rsmod.api.type.builders.map.npc.MapNpcSpawnBuilder
import org.rsmod.content.areas.city.lumbridge.EdgevilleScript

object EdgevilleNpcSpawns : MapNpcSpawnBuilder() {
    override fun onPackMapTask() {
        resourceFile<EdgevilleScript>("npcs.toml")
    }
}

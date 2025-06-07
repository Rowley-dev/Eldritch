package org.rsmod.content.areas.city.lumbridge.map

import org.rsmod.api.config.refs.areas
import org.rsmod.api.type.builders.map.area.MapAreaBuilder
import org.rsmod.game.area.polygon.VertexCoord

object EdgevilleMapAreas : MapAreaBuilder() {
    override fun onPackMapTask() {
        area(areas.edgeville) {
            polygon(levels = 0..3) {
                vertex(VertexCoord(47, 54, 31, 63))
                vertex(VertexCoord(47, 54, 31, 0))
                vertex(VertexCoord(48, 54, 63, 0))
                vertex(VertexCoord(48, 54, 63, 63))
            }
        }
    }
}

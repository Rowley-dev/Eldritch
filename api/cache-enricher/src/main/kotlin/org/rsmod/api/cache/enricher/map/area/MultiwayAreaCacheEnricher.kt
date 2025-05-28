package org.rsmod.api.cache.enricher.map.area

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import org.rsmod.api.cache.map.area.MapAreaDefinition
import org.rsmod.api.config.refs.areas
import org.rsmod.api.parsers.json.Json
import org.rsmod.api.utils.io.InputStreams
import org.rsmod.game.area.polygon.PolygonArea
import org.rsmod.game.area.polygon.PolygonMapSquareBuilder
import org.rsmod.game.area.util.PolygonMapSquareClipper
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey

public class MultiwayAreaCacheEnricher @Inject constructor(@Json private val mapper: ObjectMapper) :
    AreaCacheEnricher {
    override fun generate(): List<EnrichedAreaConfig> {
        val multiway = loadMultiwayAreas()
        val areas = collectAreas(multiway)
        return areas.toAreaConfigList()
    }

    private fun loadMultiwayAreas(): Array<MultiwayArea> {
        val input = InputStreams.readAllBytes<MultiwayAreaCacheEnricher>("multiway.json")
        return mapper.readValue(input, Array<MultiwayArea>::class.java)
    }

    private fun PolygonArea.toAreaConfigList(): List<EnrichedAreaConfig> {
        return mapSquares.map { (square, polygon) ->
            val areaDef = MapAreaDefinition.from(polygon)
            EnrichedAreaConfig(square, areaDef)
        }
    }

    private fun collectAreas(multiwayAreas: Array<MultiwayArea>): PolygonArea {
        // TODO: Yet another place we have to hardcode this value. I think we should heavily
        //  consider moving this to an `AreaType` flag.
        val multiwayArea = areas.multiway.id.toShort()

        val builderLists = mutableMapOf<MapSquareKey, PolygonMapSquareBuilder>()
        for (multiway in multiwayAreas) {
            val levels = multiway.levels.toSet()

            for (polygon in multiway.polygons) {
                val clipped = PolygonMapSquareClipper.closeAndClip(polygon.coords)
                for ((mapSquare, polygonVertices) in clipped) {
                    val builder = builderLists.getOrPut(mapSquare) { PolygonMapSquareBuilder() }
                    builder.polygon(multiwayArea, levels) {
                        for (vertex in polygonVertices) {
                            val localX = vertex.x % MapSquareGrid.LENGTH
                            val localZ = vertex.z % MapSquareGrid.LENGTH
                            vertex(localX, localZ)
                        }
                    }
                }
            }
        }

        val groupedSquares = builderLists.mapValues { it.value.build() }
        return PolygonArea(groupedSquares)
    }

    private data class MultiwayArea(
        val name: String,
        val levels: List<Int>,
        val polygons: List<MultiwayPolygon>,
    )

    private data class MultiwayPolygon(val vertices: List<Point>) {
        val coords = vertices.map { CoordGrid(it.x, it.z) }
    }

    private data class Point(val x: Int, val z: Int)
}

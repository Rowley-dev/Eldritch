package org.rsmod.content.skils.fletching.fletch

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.content.skils.fletching.ammo.arrow.arrowheads
import org.rsmod.game.type.obj.ObjType

internal typealias whittle_objs = WhittleObjs
internal object WhittleObjs : ObjReferences() {
    val shortbow_u = find("unstrung_shortbow")
    val longbow_u = find("unstrung_longbow")
    val oak_shortbow_u = find("unstrung_oak_shortbow")
    val oak_longbow_u = find("unstrung_oak_longbow")
    val willow_shortbow_u = find("unstrung_willow_shortbow")
    val willow_longbow_u = find("unstrung_willow_longbow")
    val maple_shortbow_u = find("unstrung_maple_shortbow")
    val maple_longbow_u = find("unstrung_maple_longbow")
    val yew_shortbow_u = find("unstrung_yew_shortbow")
    val yew_longbow_u = find("unstrung_yew_longbow")
    val magic_shortbow_u = find("unstrung_magic_shortbow")
    val magic_longbow_u = find("unstrung_magic_longbow")

    val crossbow_stock = find("xbows_crossbow_stock_wood")
    val oak_crossbow_stock = find("xbows_crossbow_stock_oak")
    val willow_crossbow_stock = find("xbows_crossbow_stock_willow")
    val teak_crossbow_stock = find("xbows_crossbow_stock_teak")
    val mahogany_crossbow_stock = find("xbows_crossbow_stock_mahogany")
    val maple_crossbow_stock = find("xbows_crossbow_stock_maple")
    val yew_crossbow_stock = find("xbows_crossbow_stock_yew")
    val magic_crossbow_stock = find("xbows_crossbow_stock_magic")

    val arrow_shaft = find("arrow_shaft")
}


enum class WhittleItem(
    val product: ObjType,
    val amount: Int = 1,
    val levelRequirement: Int,
    val experience: Double,
) {
    ARROW_SHAFT_15(arrowheads.arrow_shaft, 15, 1, 1.5),
    ARROW_SHAFT_30(arrowheads.arrow_shaft, 30, 1, 1.5),
    ARROW_SHAFT_45(arrowheads.arrow_shaft, 45, 1, 1.5),
    ARROW_SHAFT_60(arrowheads.arrow_shaft, 60, 1, 1.5),
    SHORTBOW_U(whittle_objs.shortbow_u, 1, 5, 6.0),
    LONGBOW_U(whittle_objs.longbow_u, 1, 10, 7.5),
    OAK_SHORTBOW_U(whittle_objs.oak_shortbow_u, 1, 20, 16.0),
    OAK_LONGBOW_U(whittle_objs.oak_longbow_u, 1, 25, 20.0),
    WILLOW_SHORTBOW_U(whittle_objs.willow_shortbow_u, 1, 35, 33.0),
    WILLOW_LONGBOW_U(whittle_objs.willow_longbow_u, 1, 40, 39.0),
    MAPLE_SHORTBOW_U(whittle_objs.maple_shortbow_u, 1, 50, 58.0),
    MAPLE_LONGBOW_U(whittle_objs.maple_longbow_u, 1, 55, 65.0),
    YEW_SHORTBOW_U(whittle_objs.yew_shortbow_u, 1, 65, 83.0),
    YEW_LONGBOW_U(whittle_objs.yew_longbow_u, 1, 70, 91.0),
    MAGIC_SHORTBOW_U(whittle_objs.magic_shortbow_u, 1, 80, 125.0),
    MAGIC_LONGBOW_U(whittle_objs.magic_longbow_u, 1, 85, 140.0),
    CROSSBOW_STOCK(whittle_objs.crossbow_stock, 1, 1, 1.5),
    OAK_CROSSBOW_STOCK(whittle_objs.oak_crossbow_stock, 1, 20, 16.0),
    WILLOW_CROSSBOW_STOCK(whittle_objs.willow_crossbow_stock, 1, 35, 33.0),
    TEAK_CROSSBOW_STOCK(whittle_objs.teak_crossbow_stock, 1, 50, 58.0),
    MAHOGANY_CROSSBOW_STOCK(whittle_objs.mahogany_crossbow_stock, 1, 65, 83.0),
    MAPLE_CROSSBOW_STOCK(whittle_objs.maple_crossbow_stock, 1, 70, 91.0),
    YEW_CROSSBOW_STOCK(whittle_objs.yew_crossbow_stock, 1, 80, 125.0),
    MAGIC_CROSSBOW_STOCK(whittle_objs.magic_crossbow_stock, 1, 85, 140.0),
    }
}

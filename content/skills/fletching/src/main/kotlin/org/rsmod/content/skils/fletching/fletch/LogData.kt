package org.rsmod.content.skils.fletching.fletch

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.obj.ObjType

internal object LogObjEdits : ObjEditor() {
    init {
        edit (objs.logs) {
            contentGroup = content.fletchable_log
            param[params.levelrequire] = 1
            param[params.skill_xp] = PlayerStatMap.toFineXP(6.0).toInt()
            param[params.skill_productitem_list] =
        }}
    }
}

enum class LogData(
    val raw: ObjType,
    val products: List<ObjType>,
) {
    LOGS(
        objs.logs, listOf(
        whittle_objs.shortbow_u,
        whittle_objs.longbow_u,
        whittle_objs.crossbow_stock,
        whittle_objs.arrow_shaft,
            )
    ),
    OAK(
        objs.oak_logs, listOf(
        whittle_objs.oak_shortbow_u,
        whittle_objs.oak_longbow_u,
        whittle_objs.oak_crossbow_stock,
            )
    ),
    WILLOW(
        objs.willow_logs, listOf(
        whittle_objs.willow_shortbow_u,
        whittle_objs.willow_longbow_u,
        whittle_objs.willow_crossbow_stock,
            )
    ),
    MAPLE(
        objs.maple_logs, listOf(
        whittle_objs.maple_shortbow_u,
        whittle_objs.maple_longbow_u,
        whittle_objs.maple_crossbow_stock,
            )
    ),
    YEW(
        objs.yew_logs, listOf(
        whittle_objs.yew_shortbow_u,
        whittle_objs.yew_longbow_u,
        whittle_objs.yew_crossbow_stock,
            )
    ),
    MAGIC(
        objs.magic_logs, listOf(
        whittle_objs.magic_shortbow_u,
        whittle_objs.magic_longbow_u,
        whittle_objs.magic_crossbow_stock,
            )
    ),
    TEAK(
        objs.teak_logs, listOf(
        whittle_objs.teak_crossbow_stock,
            )
    ),
    MAHOGANY(
        objs.mahogany_logs, listOf(
        whittle_objs.mahogany_crossbow_stock,
            )
    );
    companion object {
        fun from(raw: ObjType): LogData? = values().find { it.raw == raw }
    }
}

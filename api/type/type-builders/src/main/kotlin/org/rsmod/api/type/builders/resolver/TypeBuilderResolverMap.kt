package org.rsmod.api.type.builders.resolver

import jakarta.inject.Inject
import org.rsmod.api.type.builders.TypeBuilder
import org.rsmod.api.type.builders.area.AreaBuilder
import org.rsmod.api.type.builders.area.AreaBuilderResolver
import org.rsmod.api.type.builders.bas.BasBuilder
import org.rsmod.api.type.builders.bas.BasBuilderResolver
import org.rsmod.api.type.builders.controller.ControllerBuilder
import org.rsmod.api.type.builders.controller.ControllerBuilderResolver
import org.rsmod.api.type.builders.dbrow.DbRowBuilder
import org.rsmod.api.type.builders.dbrow.DbRowBuilderResolver
import org.rsmod.api.type.builders.dbtable.DbTableBuilder
import org.rsmod.api.type.builders.dbtable.DbTableBuilderResolver
import org.rsmod.api.type.builders.droptrig.DropTriggerBuilder
import org.rsmod.api.type.builders.droptrig.DropTriggerBuilderResolver
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.builders.enums.EnumBuilderResolver
import org.rsmod.api.type.builders.headbar.HeadbarBuilder
import org.rsmod.api.type.builders.headbar.HeadbarBuilderResolver
import org.rsmod.api.type.builders.hitmark.HitmarkBuilder
import org.rsmod.api.type.builders.hitmark.HitmarkBuilderResolver
import org.rsmod.api.type.builders.hunt.HuntModeBuilder
import org.rsmod.api.type.builders.hunt.HuntModeBuilderResolver
import org.rsmod.api.type.builders.inv.InvBuilder
import org.rsmod.api.type.builders.inv.InvBuilderResolver
import org.rsmod.api.type.builders.loc.LocBuilder
import org.rsmod.api.type.builders.loc.LocBuilderResolver
import org.rsmod.api.type.builders.mesanim.MesAnimBuilder
import org.rsmod.api.type.builders.mesanim.MesAnimBuilderResolver
import org.rsmod.api.type.builders.mod.ModGroupBuilder
import org.rsmod.api.type.builders.mod.ModGroupBuilderResolver
import org.rsmod.api.type.builders.npc.NpcBuilder
import org.rsmod.api.type.builders.npc.NpcBuilderResolver
import org.rsmod.api.type.builders.obj.ObjBuilder
import org.rsmod.api.type.builders.obj.ObjBuilderResolver
import org.rsmod.api.type.builders.param.ParamBuilder
import org.rsmod.api.type.builders.param.ParamBuilderResolver
import org.rsmod.api.type.builders.proj.ProjAnimBuilder
import org.rsmod.api.type.builders.proj.ProjAnimBuilderResolver
import org.rsmod.api.type.builders.stat.StatBuilder
import org.rsmod.api.type.builders.stat.StatBuilderResolver
import org.rsmod.api.type.builders.struct.StructBuilder
import org.rsmod.api.type.builders.struct.StructBuilderResolver
import org.rsmod.api.type.builders.varbit.VarBitBuilder
import org.rsmod.api.type.builders.varbit.VarBitBuilderResolver
import org.rsmod.api.type.builders.varcon.VarConBuilder
import org.rsmod.api.type.builders.varcon.VarConBuilderResolver
import org.rsmod.api.type.builders.varconbit.VarConBitBuilder
import org.rsmod.api.type.builders.varconbit.VarConBitBuilderResolver
import org.rsmod.api.type.builders.varn.VarnBuilder
import org.rsmod.api.type.builders.varn.VarnBuilderResolver
import org.rsmod.api.type.builders.varnbit.VarnBitBuilder
import org.rsmod.api.type.builders.varnbit.VarnBitBuilderResolver
import org.rsmod.api.type.builders.varobjbit.VarObjBitBuilder
import org.rsmod.api.type.builders.varobjbit.VarObjBitBuilderResolver
import org.rsmod.api.type.builders.varp.VarpBuilder
import org.rsmod.api.type.builders.varp.VarpBuilderResolver
import org.rsmod.api.type.builders.walktrig.WalkTriggerBuilder
import org.rsmod.api.type.builders.walktrig.WalkTriggerBuilderResolver

public class TypeBuilderResolverMap
@Inject
constructor(
    private val areaResolver: AreaBuilderResolver,
    private val basResolver: BasBuilderResolver,
    private val conResolver: ControllerBuilderResolver,
    private val dbRowResolver: DbRowBuilderResolver,
    private val dbTableResolver: DbTableBuilderResolver,
    private val dropTriggerResolver: DropTriggerBuilderResolver,
    private val enumResolver: EnumBuilderResolver,
    private val headbarResolver: HeadbarBuilderResolver,
    private val hitmarkResolver: HitmarkBuilderResolver,
    private val huntResolver: HuntModeBuilderResolver,
    private val invResolver: InvBuilderResolver,
    private val locResolver: LocBuilderResolver,
    private val mesAnimResolver: MesAnimBuilderResolver,
    private val modGroupResolver: ModGroupBuilderResolver,
    private val npcResolver: NpcBuilderResolver,
    private val objResolver: ObjBuilderResolver,
    private val paramResolver: ParamBuilderResolver,
    private val projAnimResolver: ProjAnimBuilderResolver,
    private val statResolver: StatBuilderResolver,
    private val structResolver: StructBuilderResolver,
    private val varBitResolver: VarBitBuilderResolver,
    private val varConResolver: VarConBuilderResolver,
    private val varConBitResolver: VarConBitBuilderResolver,
    private val varnResolver: VarnBuilderResolver,
    private val varnBitResolver: VarnBitBuilderResolver,
    private val varObjBitResolver: VarObjBitBuilderResolver,
    private val varpResolver: VarpBuilderResolver,
    private val walkTriggerResolver: WalkTriggerBuilderResolver,
) {
    private val builders = mutableListOf<TypeBuilder<*, *>>()

    private val _resultValues = mutableListOf<Any>()
    private val _updates = mutableListOf<TypeBuilderResult.Update<*>>()
    private val _errors = mutableListOf<TypeBuilderResult.Error<*>>()
    private val _symbolErrors = mutableListOf<TypeBuilderResult.Error<*>>()

    public val size: Int
        get() = builders.size

    public val resultValues: List<Any>
        get() = _resultValues

    public val updates: List<TypeBuilderResult.Update<*>>
        get() = _updates

    public val errors: List<TypeBuilderResult.Error<*>>
        get() = _errors

    public val symbolErrors: List<TypeBuilderResult.Error<*>>
        get() = _symbolErrors

    public operator fun plusAssign(builders: Collection<TypeBuilder<*, *>>) {
        this.builders += builders
    }

    public fun resolveAll() {
        for (builders in builders) {
            resolve(builders)
        }
    }

    public fun <B, T> resolve(
        builders: TypeBuilder<B, T>,
        res: TypeBuilderResolver<B, T> = builders.resolver(),
    ) {
        val resolved = res.resolve(builders)

        val updates = resolved.filterIsInstance<TypeBuilderResult.Update<Any>>()
        _updates += updates

        val errors = resolved.filterIsInstance<TypeBuilderResult.Error<Any>>()
        _errors += errors

        val symbolErrors = errors.filter { it.status is TypeBuilderResult.NameNotFound }
        _symbolErrors += symbolErrors

        val success = resolved.filterIsInstance<TypeBuilderResult.Success<Any>>()
        val results = updates.map { it.value } + errors.map { it.value } + success.map { it.value }
        _resultValues.addAll(results)
    }

    /**
     * This function can be optionally called to clear stored references after this system is no
     * longer in use.
     */
    public fun clear() {
        builders.clear()
        _updates.clear()
        _errors.clear()
        _symbolErrors.clear()
        _resultValues.clear()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <B, T> TypeBuilder<B, T>.resolver(): TypeBuilderResolver<B, T> {
        val resolver =
            when (this) {
                is AreaBuilder -> areaResolver
                is BasBuilder -> basResolver
                is ControllerBuilder -> conResolver
                is DbRowBuilder -> dbRowResolver
                is DbTableBuilder -> dbTableResolver
                is DropTriggerBuilder -> dropTriggerResolver
                is EnumBuilder -> enumResolver
                is HeadbarBuilder -> headbarResolver
                is HitmarkBuilder -> hitmarkResolver
                is HuntModeBuilder -> huntResolver
                is InvBuilder -> invResolver
                is LocBuilder -> locResolver
                is MesAnimBuilder -> mesAnimResolver
                is ModGroupBuilder -> modGroupResolver
                is NpcBuilder -> npcResolver
                is ObjBuilder -> objResolver
                is ParamBuilder -> paramResolver
                is ProjAnimBuilder -> projAnimResolver
                is StatBuilder -> statResolver
                is StructBuilder -> structResolver
                is VarBitBuilder -> varBitResolver
                is VarConBuilder -> varConResolver
                is VarConBitBuilder -> varConBitResolver
                is VarnBuilder -> varnResolver
                is VarnBitBuilder -> varnBitResolver
                is VarObjBitBuilder -> varObjBitResolver
                is VarpBuilder -> varpResolver
                is WalkTriggerBuilder -> walkTriggerResolver
                else -> throw NotImplementedError("Resolver not defined for type-builder: $this")
            }
        return resolver as TypeBuilderResolver<B, T>
    }
}

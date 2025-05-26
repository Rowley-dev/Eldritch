package org.rsmod.api.type.refs.resolver

import jakarta.inject.Inject
import org.rsmod.api.type.refs.TypeReferences
import org.rsmod.api.type.refs.area.AreaReferenceResolver
import org.rsmod.api.type.refs.area.AreaReferences
import org.rsmod.api.type.refs.category.CategoryReferenceResolver
import org.rsmod.api.type.refs.category.CategoryReferences
import org.rsmod.api.type.refs.comp.ComponentReferenceResolver
import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.content.ContentReferenceResolver
import org.rsmod.api.type.refs.content.ContentReferences
import org.rsmod.api.type.refs.currency.CurrencyReferenceResolver
import org.rsmod.api.type.refs.currency.CurrencyReferences
import org.rsmod.api.type.refs.dbcol.DbColumnReferenceResolver
import org.rsmod.api.type.refs.dbcol.DbColumnReferences
import org.rsmod.api.type.refs.dbrow.DbRowReferenceResolver
import org.rsmod.api.type.refs.dbrow.DbRowReferences
import org.rsmod.api.type.refs.dbtable.DbTableReferenceResolver
import org.rsmod.api.type.refs.dbtable.DbTableReferences
import org.rsmod.api.type.refs.enums.EnumReferenceResolver
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.api.type.refs.font.FontMetricsReferenceResolver
import org.rsmod.api.type.refs.font.FontMetricsReferences
import org.rsmod.api.type.refs.headbar.HeadbarReferenceResolver
import org.rsmod.api.type.refs.headbar.HeadbarReferences
import org.rsmod.api.type.refs.hitmark.HitmarkReferenceResolver
import org.rsmod.api.type.refs.hitmark.HitmarkReferences
import org.rsmod.api.type.refs.hunt.HuntModeReferenceResolver
import org.rsmod.api.type.refs.hunt.HuntModeReferences
import org.rsmod.api.type.refs.interf.InterfaceReferenceResolver
import org.rsmod.api.type.refs.interf.InterfaceReferences
import org.rsmod.api.type.refs.inv.InvReferenceResolver
import org.rsmod.api.type.refs.inv.InvReferences
import org.rsmod.api.type.refs.jingle.JingleReferenceResolver
import org.rsmod.api.type.refs.jingle.JingleReferences
import org.rsmod.api.type.refs.loc.LocReferenceResolver
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.api.type.refs.midi.MidiReferenceResolver
import org.rsmod.api.type.refs.midi.MidiReferences
import org.rsmod.api.type.refs.mod.ModLevelReferenceResolver
import org.rsmod.api.type.refs.mod.ModLevelReferences
import org.rsmod.api.type.refs.npc.NpcReferenceResolver
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferenceResolver
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.param.ParamReferenceResolver
import org.rsmod.api.type.refs.param.ParamReferences
import org.rsmod.api.type.refs.proj.ProjAnimReferenceResolver
import org.rsmod.api.type.refs.proj.ProjAnimReferences
import org.rsmod.api.type.refs.queue.QueueReferenceResolver
import org.rsmod.api.type.refs.queue.QueueReferences
import org.rsmod.api.type.refs.seq.SeqReferenceResolver
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.api.type.refs.spot.SpotanimReferenceResolver
import org.rsmod.api.type.refs.spot.SpotanimReferences
import org.rsmod.api.type.refs.stat.StatReferenceResolver
import org.rsmod.api.type.refs.stat.StatReferences
import org.rsmod.api.type.refs.struct.StructReferenceResolver
import org.rsmod.api.type.refs.struct.StructReferences
import org.rsmod.api.type.refs.synth.SynthReferenceResolver
import org.rsmod.api.type.refs.synth.SynthReferences
import org.rsmod.api.type.refs.timer.TimerReferenceResolver
import org.rsmod.api.type.refs.timer.TimerReferences
import org.rsmod.api.type.refs.varbit.VarBitReferenceResolver
import org.rsmod.api.type.refs.varbit.VarBitReferences
import org.rsmod.api.type.refs.varn.VarnReferenceResolver
import org.rsmod.api.type.refs.varn.VarnReferences
import org.rsmod.api.type.refs.varnbit.VarnBitReferenceResolver
import org.rsmod.api.type.refs.varnbit.VarnBitReferences
import org.rsmod.api.type.refs.varp.VarpReferenceResolver
import org.rsmod.api.type.refs.varp.VarpReferences
import org.rsmod.api.type.refs.walktrig.WalkTriggerReferenceResolver
import org.rsmod.api.type.refs.walktrig.WalkTriggerReferences

public class TypeReferenceResolverMap
@Inject
constructor(
    private val areaResolver: AreaReferenceResolver,
    private val categoryResolver: CategoryReferenceResolver,
    private val componentResolver: ComponentReferenceResolver,
    private val contentResolver: ContentReferenceResolver,
    private val currencyResolver: CurrencyReferenceResolver,
    private val dbColumnResolver: DbColumnReferenceResolver,
    private val dbRowResolver: DbRowReferenceResolver,
    private val dbTableResolver: DbTableReferenceResolver,
    private val enumResolver: EnumReferenceResolver,
    private val fontMetricsResolver: FontMetricsReferenceResolver,
    private val headbarResolver: HeadbarReferenceResolver,
    private val hitmarkResolver: HitmarkReferenceResolver,
    private val huntResolver: HuntModeReferenceResolver,
    private val interfaceResolver: InterfaceReferenceResolver,
    private val invResolver: InvReferenceResolver,
    private val jingleResolver: JingleReferenceResolver,
    private val locResolver: LocReferenceResolver,
    private val midiResolver: MidiReferenceResolver,
    private val modLevelResolver: ModLevelReferenceResolver,
    private val npcResolver: NpcReferenceResolver,
    private val objResolver: ObjReferenceResolver,
    private val paramResolver: ParamReferenceResolver,
    private val projAnimResolver: ProjAnimReferenceResolver,
    private val queueResolver: QueueReferenceResolver,
    private val seqResolver: SeqReferenceResolver,
    private val spotResolver: SpotanimReferenceResolver,
    private val statResolver: StatReferenceResolver,
    private val structResolver: StructReferenceResolver,
    private val synthResolver: SynthReferenceResolver,
    private val timerResolver: TimerReferenceResolver,
    private val varBitResolver: VarBitReferenceResolver,
    private val varnResolver: VarnReferenceResolver,
    private val varnBitResolver: VarnBitReferenceResolver,
    private val varpResolver: VarpReferenceResolver,
    private val walkTriggerResolver: WalkTriggerReferenceResolver,
) {
    private val references = mutableListOf<TypeReferences<*, *>>()
    private val _errors = mutableListOf<TypeReferenceResult.Error<*>>()
    private val _issues = mutableListOf<TypeReferenceResult.Issue<*>>()
    private val _updates = mutableListOf<TypeReferenceResult.Update<*>>()

    public val size: Int
        get() = references.size

    public val errors: List<TypeReferenceResult.Error<*>>
        get() = _errors

    public val issues: List<TypeReferenceResult.Issue<*>>
        get() = _issues

    public val updates: List<TypeReferenceResult.Update<*>>
        get() = _updates

    public operator fun plusAssign(refs: Collection<TypeReferences<*, *>>) {
        this.references += refs
    }

    public fun resolveAll() {
        for (references in references) {
            resolve(references)
        }
    }

    public fun <T, I> resolve(
        ref: TypeReferences<T, I>,
        res: TypeReferenceResolver<T, I> = ref.resolver(),
    ) {
        val resolved = res.resolve(ref)

        val updates = resolved.filterIsInstance<TypeReferenceResult.Update<*>>()
        _updates += updates

        val issues = resolved.filterIsInstance<TypeReferenceResult.Issue<*>>()
        _issues += issues

        val errors = resolved.filterIsInstance<TypeReferenceResult.Error<*>>()
        _errors += errors
    }

    /**
     * This function can be optionally called to clear stored references after this system is no
     * longer in use.
     */
    public fun clear() {
        references.clear()
        _errors.clear()
        _issues.clear()
        _updates.clear()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T, I> TypeReferences<T, I>.resolver(): TypeReferenceResolver<T, I> {
        val resolver =
            when (this) {
                is AreaReferences -> areaResolver
                is CategoryReferences -> categoryResolver
                is ComponentReferences -> componentResolver
                is ContentReferences -> contentResolver
                is CurrencyReferences -> currencyResolver
                is DbColumnReferences -> dbColumnResolver
                is DbRowReferences -> dbRowResolver
                is DbTableReferences -> dbTableResolver
                is EnumReferences -> enumResolver
                is FontMetricsReferences -> fontMetricsResolver
                is HeadbarReferences -> headbarResolver
                is HitmarkReferences -> hitmarkResolver
                is HuntModeReferences -> huntResolver
                is InterfaceReferences -> interfaceResolver
                is InvReferences -> invResolver
                is JingleReferences -> jingleResolver
                is LocReferences -> locResolver
                is MidiReferences -> midiResolver
                is ModLevelReferences -> modLevelResolver
                is NpcReferences -> npcResolver
                is ObjReferences -> objResolver
                is ParamReferences -> paramResolver
                is ProjAnimReferences -> projAnimResolver
                is QueueReferences -> queueResolver
                is SeqReferences -> seqResolver
                is SpotanimReferences -> spotResolver
                is StatReferences -> statResolver
                is StructReferences -> structResolver
                is SynthReferences -> synthResolver
                is TimerReferences -> timerResolver
                is VarBitReferences -> varBitResolver
                is VarnReferences -> varnResolver
                is VarnBitReferences -> varnBitResolver
                is VarpReferences -> varpResolver
                is WalkTriggerReferences -> walkTriggerResolver
                else -> throw NotImplementedError("Resolver not defined for type-reference: $this")
            }
        return resolver as TypeReferenceResolver<T, I>
    }
}

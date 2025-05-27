package org.rsmod.game.type.literal

import kotlin.reflect.KClass
import org.rsmod.game.type.area.AreaType
import org.rsmod.game.type.area.HashedAreaType
import org.rsmod.game.type.category.CategoryType
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.comp.HashedComponentType
import org.rsmod.game.type.dbrow.DbRowType
import org.rsmod.game.type.dbrow.HashedDbRowType
import org.rsmod.game.type.dbtable.DbTableType
import org.rsmod.game.type.dbtable.HashedDbTableType
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.enums.HashedEnumType
import org.rsmod.game.type.headbar.HashedHeadbarType
import org.rsmod.game.type.headbar.HeadbarType
import org.rsmod.game.type.hitmark.HashedHitmarkType
import org.rsmod.game.type.hitmark.HitmarkType
import org.rsmod.game.type.interf.HashedInterfaceType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.type.loc.HashedLocType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.npc.HashedNpcType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.HashedObjType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.proj.HashedProjAnimType
import org.rsmod.game.type.proj.ProjAnimType
import org.rsmod.game.type.seq.HashedSeqType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.HashedSpotanimType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.stat.HashedStatType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.varbit.HashedVarBitType
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.game.type.varp.HashedVarpType
import org.rsmod.game.type.varp.VarpType
import org.rsmod.map.CoordGrid

public object CacheVarTypeMap {
    public val classedLiterals: Map<KClass<*>, CacheVarLiteral> =
        hashMapOf(
            AreaType::class to CacheVarLiteral.AREA,
            HashedAreaType::class to CacheVarLiteral.AREA,
            Boolean::class to CacheVarLiteral.BOOL,
            ComponentType::class to CacheVarLiteral.COMPONENT,
            HashedComponentType::class to CacheVarLiteral.COMPONENT,
            Int::class to CacheVarLiteral.INT,
            CategoryType::class to CacheVarLiteral.CATEGORY,
            CoordGrid::class to CacheVarLiteral.COORDGRID,
            DbRowType::class to CacheVarLiteral.DBROW,
            HashedDbRowType::class to CacheVarLiteral.DBROW,
            DbTableType::class to CacheVarLiteral.DBTABLE,
            HashedDbTableType::class to CacheVarLiteral.DBTABLE,
            EnumType::class to CacheVarLiteral.ENUM,
            HashedEnumType::class to CacheVarLiteral.ENUM,
            HeadbarType::class to CacheVarLiteral.HEADBAR,
            HashedHeadbarType::class to CacheVarLiteral.HEADBAR,
            HitmarkType::class to CacheVarLiteral.HITMARK,
            HashedHitmarkType::class to CacheVarLiteral.HITMARK,
            InterfaceType::class to CacheVarLiteral.INTERFACE,
            HashedInterfaceType::class to CacheVarLiteral.INTERFACE,
            LocType::class to CacheVarLiteral.LOC,
            HashedLocType::class to CacheVarLiteral.LOC,
            NpcType::class to CacheVarLiteral.NPC,
            HashedNpcType::class to CacheVarLiteral.NPC,
            ObjType::class to CacheVarLiteral.OBJ,
            HashedObjType::class to CacheVarLiteral.OBJ,
            ProjAnimType::class to CacheVarLiteral.PROJANIM,
            HashedProjAnimType::class to CacheVarLiteral.PROJANIM,
            SeqType::class to CacheVarLiteral.SEQ,
            HashedSeqType::class to CacheVarLiteral.SEQ,
            SpotanimType::class to CacheVarLiteral.SPOTANIM,
            HashedSpotanimType::class to CacheVarLiteral.SPOTANIM,
            UnpackedObjType::class to CacheVarLiteral.NAMEDOBJ,
            String::class to CacheVarLiteral.STRING,
            StatType::class to CacheVarLiteral.STAT,
            HashedStatType::class to CacheVarLiteral.STAT,
            SynthType::class to CacheVarLiteral.SYNTH,
            VarBitType::class to CacheVarLiteral.VARBIT,
            HashedVarBitType::class to CacheVarLiteral.VARBIT,
            VarpType::class to CacheVarLiteral.VARP,
            HashedVarpType::class to CacheVarLiteral.VARP,
        )

    public val codecMap: Map<KClass<*>, CacheVarCodec<*, *>> =
        hashMapOf(
            AreaType::class to CacheVarAreaCodec,
            HashedAreaType::class to CacheVarAreaCodec,
            Boolean::class to CacheVarBoolCodec,
            CategoryType::class to CacheVarCategoryCodec,
            ComponentType::class to CacheVarComponentCodec,
            HashedComponentType::class to CacheVarComponentCodec,
            DbRowType::class to CacheVarDbRowCodec,
            HashedDbRowType::class to CacheVarDbRowCodec,
            DbTableType::class to CacheVarDbTableCodec,
            HashedDbTableType::class to CacheVarDbTableCodec,
            Int::class to CacheVarIntCodec,
            CoordGrid::class to CacheVarCoordGridCodec,
            EnumType::class to CacheVarEnumCodec,
            HashedEnumType::class to CacheVarEnumCodec,
            HeadbarType::class to CacheVarHeadbarCodec,
            HashedHeadbarType::class to CacheVarHeadbarCodec,
            HitmarkType::class to CacheVarHitmarkCodec,
            HashedHitmarkType::class to CacheVarHitmarkCodec,
            InterfaceType::class to CacheVarInterfaceCodec,
            HashedInterfaceType::class to CacheVarInterfaceCodec,
            LocType::class to CacheVarLocCodec,
            HashedLocType::class to CacheVarLocCodec,
            NpcType::class to CacheVarNpcCodec,
            HashedNpcType::class to CacheVarNpcCodec,
            ObjType::class to CacheVarObjCodec,
            HashedObjType::class to CacheVarObjCodec,
            ProjAnimType::class to CacheVarProjAnimCodec,
            HashedProjAnimType::class to CacheVarProjAnimCodec,
            SeqType::class to CacheVarSeqCodec,
            HashedSeqType::class to CacheVarSeqCodec,
            SpotanimType::class to CacheVarSpotanimCodec,
            HashedSpotanimType::class to CacheVarSpotanimCodec,
            UnpackedObjType::class to CacheVarNamedObjCodec,
            String::class to CacheVarStringCodec,
            StatType::class to CacheVarStatCodec,
            HashedStatType::class to CacheVarStatCodec,
            SynthType::class to CacheVarSynthCodec,
            VarBitType::class to CacheVarVarBitCodec,
            HashedVarBitType::class to CacheVarVarBitCodec,
            VarpType::class to CacheVarVarpCodec,
            HashedVarpType::class to CacheVarVarpCodec,
        )

    public val CacheVarLiteral.codecOut: KClass<*>
        get() =
            when (this) {
                CacheVarLiteral.BOOL -> Boolean::class
                CacheVarLiteral.ENTITY_OVERLAY -> Int::class
                CacheVarLiteral.SEQ -> SeqType::class
                CacheVarLiteral.COLOUR -> Int::class
                CacheVarLiteral.TOPLEVEL_INTERFACE -> Int::class
                CacheVarLiteral.LOC_SHAPE -> Int::class
                CacheVarLiteral.COMPONENT -> ComponentType::class
                CacheVarLiteral.STRUCT -> Int::class
                CacheVarLiteral.IDKIT -> Int::class
                CacheVarLiteral.OVERLAY_INTERFACE -> Int::class
                CacheVarLiteral.MIDI -> Int::class
                CacheVarLiteral.NPCMODE -> Int::class
                CacheVarLiteral.NAMEDOBJ -> UnpackedObjType::class
                CacheVarLiteral.SYNTH -> SynthType::class
                CacheVarLiteral.AREA -> AreaType::class
                CacheVarLiteral.STAT -> StatType::class
                CacheVarLiteral.NPCSTAT -> Int::class
                CacheVarLiteral.MAPAREA -> Int::class
                CacheVarLiteral.INTERFACE -> InterfaceType::class
                CacheVarLiteral.COORDGRID -> CoordGrid::class
                CacheVarLiteral.GRAPHIC -> Int::class
                CacheVarLiteral.FONTMETRICS -> Int::class
                CacheVarLiteral.ENUM -> EnumType::class
                CacheVarLiteral.JINGLE -> Int::class
                CacheVarLiteral.INT -> Int::class
                CacheVarLiteral.LOC -> LocType::class
                CacheVarLiteral.MODEL -> Int::class
                CacheVarLiteral.NPC -> NpcType::class
                CacheVarLiteral.OBJ -> ObjType::class
                CacheVarLiteral.PLAYERUID -> Int::class
                CacheVarLiteral.STRING -> String::class
                CacheVarLiteral.SPOTANIM -> SpotanimType::class
                CacheVarLiteral.NPCUID -> Int::class
                CacheVarLiteral.INV -> Int::class
                CacheVarLiteral.TEXTURE -> Int::class
                CacheVarLiteral.CATEGORY -> CategoryType::class
                CacheVarLiteral.CHAR -> Int::class
                CacheVarLiteral.MAPELEMENT -> Int::class
                CacheVarLiteral.HITMARK -> HitmarkType::class
                CacheVarLiteral.HEADBAR -> HeadbarType::class
                CacheVarLiteral.STRINGVECTOR -> Int::class
                CacheVarLiteral.DBTABLE -> DbTableType::class
                CacheVarLiteral.DBROW -> DbRowType::class
                CacheVarLiteral.MOVESPEED -> Int::class
                CacheVarLiteral.VARBIT -> VarBitType::class
                CacheVarLiteral.VARP -> VarpType::class
                CacheVarLiteral.PROJANIM -> ProjAnimType::class
            }

    public fun <K, V : Any> findCodec(literal: CacheVarLiteral): CacheVarCodec<K, V> =
        findCodec(literal.codecOut)

    @Suppress("UNCHECKED_CAST")
    public fun <K, V : Any> findCodec(type: KClass<*>): CacheVarCodec<K, V> =
        codecMap[type] as? CacheVarCodec<K, V>
            ?: error("CacheVarCodec for type is not implemented in `codecMap`: $type")
}

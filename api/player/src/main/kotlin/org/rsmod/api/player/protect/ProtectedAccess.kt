package org.rsmod.api.player.protect

import com.github.michaelbull.logging.InlineLogger
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.reflect.KClass
import org.rsmod.annotations.InternalApi
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.BaseHitmarkGroups
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.hitmark_groups
import org.rsmod.api.config.refs.invs
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.varps
import org.rsmod.api.hunt.NpcSearch
import org.rsmod.api.hunt.PlayerSearch
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.invtx.invAddAll
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.invtx.invClear
import org.rsmod.api.invtx.invCompress
import org.rsmod.api.invtx.invDel
import org.rsmod.api.invtx.invMoveAll
import org.rsmod.api.invtx.invSwap
import org.rsmod.api.invtx.invTakeFee
import org.rsmod.api.invtx.invTransaction
import org.rsmod.api.invtx.invTransfer
import org.rsmod.api.invtx.select
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.cinematic.CameraMode
import org.rsmod.api.player.cinematic.Cinematic
import org.rsmod.api.player.cinematic.MinimapState
import org.rsmod.api.player.combatClearQueue
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.hit.modifier.NoopPlayerHitModifier
import org.rsmod.api.player.hit.modifier.PlayerHitModifier
import org.rsmod.api.player.hit.modifier.StandardPlayerHitModifier
import org.rsmod.api.player.hit.processQueuedHit
import org.rsmod.api.player.hit.processor.DamageOnlyPlayerHitProcessor
import org.rsmod.api.player.hit.processor.InstantPlayerHitProcessor
import org.rsmod.api.player.hit.processor.StandardPlayerHitProcessor
import org.rsmod.api.player.hit.queueHit
import org.rsmod.api.player.hit.queueImpactHit
import org.rsmod.api.player.hit.takeInstantHit
import org.rsmod.api.player.input.ResumePCountDialogInput
import org.rsmod.api.player.input.ResumePObjDialogInput
import org.rsmod.api.player.input.ResumePStringDialogInput
import org.rsmod.api.player.input.ResumePauseButtonInput
import org.rsmod.api.player.interact.HeldInteractions
import org.rsmod.api.player.interact.WornInteractions
import org.rsmod.api.player.isInCombat
import org.rsmod.api.player.isInPvnCombat
import org.rsmod.api.player.isInPvpCombat
import org.rsmod.api.player.isOutOfCombat
import org.rsmod.api.player.mapMultiway
import org.rsmod.api.player.midiJingle
import org.rsmod.api.player.midiSong
import org.rsmod.api.player.output.Camera
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.ClientScripts
import org.rsmod.api.player.output.ClientScripts.chatDefaultRestoreInput
import org.rsmod.api.player.output.ClientScripts.mesLayerMode14
import org.rsmod.api.player.output.ClientScripts.mesLayerMode7
import org.rsmod.api.player.output.ClientScripts.mesLayerMode9
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.objExamine
import org.rsmod.api.player.output.runClientScript
import org.rsmod.api.player.output.soundSynth
import org.rsmod.api.player.output.spam
import org.rsmod.api.player.queueDeath
import org.rsmod.api.player.startInvTransmit
import org.rsmod.api.player.stat.stat
import org.rsmod.api.player.stat.statAdd
import org.rsmod.api.player.stat.statAdvance
import org.rsmod.api.player.stat.statBase
import org.rsmod.api.player.stat.statBoost
import org.rsmod.api.player.stat.statDrain
import org.rsmod.api.player.stat.statHeal
import org.rsmod.api.player.stat.statRandom
import org.rsmod.api.player.stat.statRestore
import org.rsmod.api.player.stat.statRestoreAll
import org.rsmod.api.player.stat.statSub
import org.rsmod.api.player.stopInvTransmit
import org.rsmod.api.player.ui.ifChatNpcSpecific
import org.rsmod.api.player.ui.ifChatPlayer
import org.rsmod.api.player.ui.ifChoice
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.ui.ifCloseSub
import org.rsmod.api.player.ui.ifConfirmDestroy
import org.rsmod.api.player.ui.ifConfirmOverlay
import org.rsmod.api.player.ui.ifConfirmOverlayClose
import org.rsmod.api.player.ui.ifDoubleobjbox
import org.rsmod.api.player.ui.ifMenu
import org.rsmod.api.player.ui.ifMesbox
import org.rsmod.api.player.ui.ifObjbox
import org.rsmod.api.player.ui.ifOpenFullOverlay
import org.rsmod.api.player.ui.ifOpenMain
import org.rsmod.api.player.ui.ifOpenMainModal
import org.rsmod.api.player.ui.ifOpenMainSidePair
import org.rsmod.api.player.ui.ifOpenOverlay
import org.rsmod.api.player.ui.ifOpenSide
import org.rsmod.api.player.ui.ifOpenSub
import org.rsmod.api.player.ui.ifSetAnim
import org.rsmod.api.player.ui.ifSetEvents
import org.rsmod.api.player.ui.ifSetHide
import org.rsmod.api.player.ui.ifSetNpcHead
import org.rsmod.api.player.ui.ifSetObj
import org.rsmod.api.player.ui.ifSetPlayerHead
import org.rsmod.api.player.ui.ifSetText
import org.rsmod.api.player.vars.VarPlayerIntMapDelegate
import org.rsmod.api.player.vars.resyncVar
import org.rsmod.api.player.vars.setActiveMoveSpeed
import org.rsmod.api.player.vars.varMoveSpeed
import org.rsmod.api.player.worn.HeldEquipResult
import org.rsmod.api.player.worn.WornUnequipResult
import org.rsmod.api.random.GameRandom
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.repo.world.WorldRepository
import org.rsmod.api.route.RayCastValidator
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.api.utils.map.BuildAreaUtils
import org.rsmod.coroutine.GameCoroutine
import org.rsmod.events.EventBus
import org.rsmod.events.KeyedEvent
import org.rsmod.events.SuspendEvent
import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.entity.npc.NpcUid
import org.rsmod.game.entity.player.PlayerUid
import org.rsmod.game.entity.player.ProtectedAccessLostException
import org.rsmod.game.entity.util.PathingEntityCommon
import org.rsmod.game.hit.Hit
import org.rsmod.game.hit.HitBuilder
import org.rsmod.game.hit.HitType
import org.rsmod.game.interact.HeldOp
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.inv.InvObj
import org.rsmod.game.inv.Inventory
import org.rsmod.game.inv.isType
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.map.Direction
import org.rsmod.game.map.collision.get
import org.rsmod.game.map.collision.isWalkBlocked
import org.rsmod.game.map.collision.isZoneValid
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.type.area.AreaType
import org.rsmod.game.type.category.CategoryType
import org.rsmod.game.type.category.CategoryTypeList
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.dbrow.DbRowType
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.hitmark.HitmarkTypeGroup
import org.rsmod.game.type.hunt.HuntVis
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.interf.IfSubType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.type.inv.InvType
import org.rsmod.game.type.jingle.JingleType
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.mesanim.UnpackedMesAnimType
import org.rsmod.game.type.midi.MidiType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.seq.UnpackedSeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.synth.SynthType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.game.type.walktrig.WalkTriggerPriority
import org.rsmod.game.type.walktrig.WalkTriggerType
import org.rsmod.game.ui.Component
import org.rsmod.game.vars.VarPlayerStrMap
import org.rsmod.map.CoordGrid
import org.rsmod.map.util.Bounds
import org.rsmod.objtx.TransactionResultList
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.flag.CollisionFlag

private val logger = InlineLogger()

public class ProtectedAccess(
    public val player: Player,
    private val coroutine: GameCoroutine,
    private val context: ProtectedAccessContext,
) {
    public val random: GameRandom by context::random

    public val coords: CoordGrid by player::coords
    public val mapClock: Int by player::currentMapClock
    public val isBusy2: Boolean by player::isBusy2

    public val inv: Inventory by player::inv
    public val worn: Inventory by player::worn
    public val bank: Inventory by lazy { inv(invs.bank) }
    public val tempInv: Inventory by lazy { inv(invs.tradeoffer) }

    public val vars: VarPlayerIntMapDelegate by lazy { VarPlayerIntMapDelegate.from(player) }
    public val strVars: VarPlayerStrMap by player::strVars

    public var actionDelay: Int by player::actionDelay
    public var skillAnimDelay: Int by player::skillAnimDelay
    public var refaceDelay: Int by player::refaceDelay

    private var opHeldCallCount = 0

    public fun walk(dest: CoordGrid) {
        player.walk(dest)
    }

    /**
     * Queues a route towards [dest] and delays the player (suspending this call-site) based on the
     * distance to [dest] and the "step rate" of [MoveSpeed.Walk].
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     */
    public suspend fun playerWalk(dest: CoordGrid) {
        playerMove(dest, MoveSpeed.Walk)
    }

    /**
     * Queues a route towards [dest] and delays the player (suspending this call-site) based on the
     * distance to [dest] and the "step rate" of [MoveSpeed.Run].
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     */
    public suspend fun playerRun(dest: CoordGrid) {
        playerMove(dest, MoveSpeed.Run)
    }

    /**
     * Queues a route to [dest] and delays the player (suspending this call-site) based on the
     * distance to [dest] and the "step rate" of [moveSpeed].
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     */
    public suspend fun playerMove(dest: CoordGrid, moveSpeed: MoveSpeed = player.varMoveSpeed) {
        walk(dest)
        player.moveSpeed = moveSpeed

        val distance = coords.chebyshevDistance(dest)
        if (moveSpeed == MoveSpeed.Run && distance <= 3) {
            return
        }

        val distanceDelay = (distance - 1) / max(1, moveSpeed.steps)
        if (distanceDelay > 0) {
            delay(distanceDelay)
        }
    }

    /**
     * Similar to [playerWalk], but ensures a minimum delay of 1 cycle, regardless of distance.
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [playerWalk]
     */
    public suspend fun playerWalkWithMinDelay(dest: CoordGrid) {
        playerMoveWithMinDelay(dest, MoveSpeed.Walk)
    }

    /**
     * Similar to [playerRun], but ensures a minimum delay of 1 cycle, regardless of distance.
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [playerRun]
     */
    public suspend fun playerRunWithMinDelay(dest: CoordGrid) {
        playerMoveWithMinDelay(dest, MoveSpeed.Run)
    }

    /**
     * Similar to [playerMove], but ensures a minimum delay of 1 cycle, regardless of distance.
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [playerMove]
     */
    public suspend fun playerMoveWithMinDelay(
        dest: CoordGrid,
        moveSpeed: MoveSpeed = player.varMoveSpeed,
    ) {
        walk(dest)
        player.moveSpeed = moveSpeed

        val distanceDelay = (coords.chebyshevDistance(dest) - 1) / max(1, moveSpeed.steps)
        delay(max(1, distanceDelay))
    }

    public fun toggleRun() {
        if (player.runEnergy < 100) {
            spam("You don't have enough energy left to run!")
            player.resyncVar(varps.option_run)
            return
        }
        val speed = if (player.varMoveSpeed == MoveSpeed.Run) MoveSpeed.Walk else MoveSpeed.Run
        player.setActiveMoveSpeed(speed)
    }

    public fun telejump(dest: CoordGrid, collision: CollisionFlagMap) {
        if (!collision.isZoneValid(dest)) {
            player.clearMapFlag()
            mes("Invalid teleport!", ChatType.Engine)
            return
        }
        PathingEntityCommon.telejump(player, collision, dest)
    }

    public fun telejump(dest: CoordGrid) {
        telejump(dest, context.collision)
    }

    public fun teleport(dest: CoordGrid, collision: CollisionFlagMap) {
        if (!collision.isZoneValid(dest)) {
            player.clearMapFlag()
            mes("Invalid teleport!", ChatType.Engine)
            return
        }
        PathingEntityCommon.teleport(player, collision, dest)
    }

    public fun teleport(dest: CoordGrid) {
        teleport(dest, context.collision)
    }

    /**
     * Starts an `exactmove` sequence from [start] to [end].
     *
     * @param start The starting coordinates, typically the current [Player.coords].
     * @param end The destination coordinates where the player will appear after [delay2] client
     *   cycles.
     * @param delay1 The number of client cycles before the player visually appears at [start].
     * @param delay2 The number of client cycles before the player visually appears at [end].
     * @param dir A value in the range `[0..2047]` representing the direction the player will face
     *   upon reaching [end]. Common values can be found in the `constants` file, prefixed with
     *   "em_face_" (e.g. `em_face_north`).
     */
    public fun exactMove(start: CoordGrid, end: CoordGrid, delay1: Int, delay2: Int, dir: Int) {
        val collision = context.collision
        if (!collision.isZoneValid(end)) {
            player.clearMapFlag()
            mes("Invalid teleport!", ChatType.Engine)
            return
        }
        PathingEntityCommon.exactMove(player, start, end, delay1, delay2, dir, collision)
    }

    public fun anim(seq: SeqType, delay: Int = 0) {
        player.anim(seq, delay)
    }

    public fun resetAnim() {
        player.resetAnim()
    }

    public fun animProtect(animProtect: Boolean) {
        PathingEntityCommon.setAnimProtect(player, animProtect)
    }

    public fun resetSpotanim() {
        player.resetSpotanim()
    }

    public fun spotanim(spot: SpotanimType?, delay: Int = 0, height: Int = 0, slot: Int = 0) {
        if (spot == null) {
            player.resetSpotanim(height = height, slot = slot)
            return
        }
        player.spotanim(spot, delay, height, slot)
    }

    public fun say(text: String) {
        player.say(text)
    }

    public fun transmog(npcType: NpcType) {
        player.transmog = context.npcTypes[npcType]
    }

    public fun resetTransmog() {
        player.transmog = null
    }

    public fun rebuildAppearance() {
        player.rebuildAppearance()
    }

    public fun isBodyType(type: Int): Boolean {
        return player.appearance.bodyType == type
    }

    public fun isBodyTypeA(): Boolean {
        return isBodyType(constants.bodytype_a)
    }

    public fun isBodyTypeB(): Boolean {
        return isBodyType(constants.bodytype_b)
    }

    public fun isWithinDistance(
        target: CoordGrid,
        distance: Int,
        width: Int = 1,
        length: Int = 1,
    ): Boolean {
        return player.isWithinDistance(target, distance, width, length)
    }

    public fun isWithinDistance(other: PathingEntity, distance: Int): Boolean {
        return player.isWithinDistance(other, distance)
    }

    public fun isWithinDistance(loc: BoundLocInfo, distance: Int): Boolean {
        return player.isWithinDistance(loc, distance)
    }

    public fun isWithinArea(southWest: CoordGrid, northEast: CoordGrid): Boolean {
        return player.isWithinArea(southWest, northEast)
    }

    public fun distanceTo(target: CoordGrid, width: Int = 1, length: Int = 1): Int {
        return player.distanceTo(target, width, length)
    }

    public fun distanceTo(other: PathingEntity): Int {
        return player.distanceTo(other)
    }

    public fun distanceTo(loc: BoundLocInfo): Int {
        return player.distanceTo(loc)
    }

    public fun apRange(distance: Int) {
        val interaction = player.interaction ?: return
        interaction.apRange = distance
        interaction.apRangeCalled = true
    }

    public fun isWithinApRange(loc: BoundLocInfo, distance: Int): Boolean {
        if (!isWithinDistance(loc, distance)) {
            apRange(distance)
            return false
        }
        return true
    }

    public fun isWithinApRange(target: PathingEntity, distance: Int): Boolean {
        if (!isWithinDistance(target, distance)) {
            apRange(distance)
            return false
        }
        return true
    }

    /**
     * Returns a [Player] from the active player list whose [Player.uid] matches [uid], or `null` if
     * no match is found.
     */
    public fun findUid(uid: PlayerUid): Player? {
        return uid.resolve(context.playerList)
    }

    /**
     * Returns an [Npc] from the active npc list whose [Npc.uid] matches [uid], or `null` if no
     * match is found.
     */
    public fun findUid(uid: NpcUid): Npc? {
        return uid.resolve(context.npcList)
    }

    /**
     * Searches for and returns a validated coordinate within a [minRadius] to [maxRadius] tile
     * radius of [centre].
     *
     * A coordinate is considered valid if:
     * - It does **not** have the [CollisionFlag.BLOCK_WALK] collision flags set.
     *
     * This function calls [validatedSquares], which **randomizes** the order of candidate
     * coordinates before validation. The first valid coordinate from the shuffled list is returned.
     *
     * @return A randomly selected, **validated** coordinate within range, or `null` if none are
     *   found.
     */
    public fun mapFindSquareNone(centre: CoordGrid, minRadius: Int, maxRadius: Int): CoordGrid? {
        val squares = validatedSquares(centre, minRadius, maxRadius)
        return squares.firstOrNull()
    }

    /**
     * Searches for and returns a validated coordinate within a [minRadius] to [maxRadius] tile
     * radius of [centre].
     *
     * A coordinate is considered valid if:
     * - It has a valid line-of-walk from [centre].
     * - It does **not** have the [CollisionFlag.BLOCK_PLAYERS] or [CollisionFlag.BLOCK_WALK]
     *   collision flags set.
     *
     * This function calls [validatedLineOfWalkSquares], which **randomizes** the order of candidate
     * coordinates before validation. The first valid coordinate from the shuffled list is returned.
     *
     * @return A randomly selected, **validated** coordinate within range, or `null` if none are
     *   found.
     */
    public fun mapFindSquareLineOfWalk(
        centre: CoordGrid,
        minRadius: Int,
        maxRadius: Int,
    ): CoordGrid? {
        val squares = validatedLineOfWalkSquares(centre, minRadius, maxRadius)
        return squares.firstOrNull()
    }

    /**
     * Searches for and returns a validated coordinate within a [minRadius] to [maxRadius] tile
     * radius of [centre].
     *
     * A coordinate is considered valid if:
     * - It has a valid line-of-sight from [centre].
     * - It does **not** have the [CollisionFlag.BLOCK_PLAYERS] or [CollisionFlag.BLOCK_WALK]
     *   collision flags set.
     *
     * This function calls [validatedLineOfWalkSquares], which **randomizes** the order of candidate
     * coordinates before validation. The first valid coordinate from the shuffled list is returned.
     *
     * @return A randomly selected, **validated** coordinate within range, or `null` if none are
     *   found.
     */
    public fun mapFindSquareLineOfSight(
        centre: CoordGrid,
        minRadius: Int,
        maxRadius: Int,
    ): CoordGrid? {
        val squares = validatedLineOfSightSquares(centre, minRadius, maxRadius)
        return squares.firstOrNull()
    }

    /**
     * Returns a sequence of **shuffled** and **validated** coordinates centered around [centre],
     * within a radius of [minRadius] to [maxRadius] tiles.
     *
     * A coordinate is considered valid if:
     * - It has a valid line-of-walk from [centre].
     * - It does **not** have the [CollisionFlag.BLOCK_PLAYERS] or [CollisionFlag.BLOCK_WALK]
     *   collision flags set.
     *
     * @return A **shuffled** and **validated** [Sequence] of coordinates within range.
     */
    public fun validatedLineOfWalkSquares(
        centre: CoordGrid,
        minRadius: Int,
        maxRadius: Int,
    ): Sequence<CoordGrid> {
        val validator = RayCastValidator(context.collision)
        val squares = shuffledSquares(centre, minRadius, maxRadius)
        return squares.filter {
            validator.hasLineOfWalk(centre, it, extraFlag = CollisionFlag.BLOCK_PLAYERS)
        }
    }

    /**
     * Returns a sequence of **shuffled** and **validated** coordinates centered around [centre],
     * within a radius of [minRadius] to [maxRadius] tiles.
     *
     * A coordinate is considered valid if:
     * - It has a valid line-of-sight from [centre].
     * - It does **not** have the [CollisionFlag.BLOCK_PLAYERS] or [CollisionFlag.BLOCK_WALK]
     *   collision flags set.
     *
     * @return A **shuffled** and **validated** [Sequence] of coordinates within range.
     */
    public fun validatedLineOfSightSquares(
        centre: CoordGrid,
        minRadius: Int,
        maxRadius: Int,
    ): Sequence<CoordGrid> {
        val validator = RayCastValidator(context.collision)
        val squares = shuffledSquares(centre, minRadius, maxRadius)
        return squares.filter {
            validator.hasLineOfSight(centre, it, extraFlag = CollisionFlag.BLOCK_PLAYERS)
        }
    }

    /**
     * Returns a sequence of **shuffled** and **validated** coordinates centered around [centre],
     * within a radius of [minRadius] to [maxRadius] tiles.
     *
     * A coordinate is considered valid if it does **not** have the [CollisionFlag.BLOCK_WALK]
     * collision flag set.
     *
     * @return A **shuffled** and **validated** [Sequence] of coordinates within range.
     */
    public fun validatedSquares(
        centre: CoordGrid,
        minRadius: Int,
        maxRadius: Int,
    ): Sequence<CoordGrid> {
        val collision = context.collision
        val squares = shuffledSquares(centre, minRadius, maxRadius)
        return squares.filter {
            val flag = collision[it]
            flag and CollisionFlag.BLOCK_WALK == 0
        }
    }

    private fun shuffledSquares(
        centre: CoordGrid,
        minRadius: Int,
        maxRadius: Int,
    ): Sequence<CoordGrid> {
        require(minRadius <= maxRadius) {
            "`minRadius` must be less than or equal to `maxRadius`. " +
                "(centre=$centre, minRadius=$minRadius, maxRadius=$maxRadius)"
        }
        val base = centre.translate(-maxRadius, -maxRadius)
        val bounds = Bounds(base, 2 * maxRadius + 1, 2 * maxRadius + 1)
        return bounds.shuffled().filter { centre.chebyshevDistance(it) >= minRadius }
    }

    /** Returns `true` if there is a valid line-of-walk from [from] to [to] */
    public fun lineOfWalk(from: CoordGrid, to: CoordGrid): Boolean {
        val validator = RayCastValidator(context.collision)
        return validator.hasLineOfWalk(from, to, extraFlag = CollisionFlag.BLOCK_PLAYERS)
    }

    /** Returns `true` if there is a valid line-of-sight from [from] to [to] */
    public fun lineOfSight(from: CoordGrid, to: CoordGrid): Boolean {
        val validator = RayCastValidator(context.collision)
        return validator.hasLineOfSight(from, to, extraFlag = CollisionFlag.BLOCK_PLAYERS)
    }

    /**
     * Returns `true` if there is a valid line-of-walk from [from] to **every** coordinate occupied
     * by [bounds].
     */
    public fun lineOfWalk(from: CoordGrid, bounds: Bounds): Boolean {
        val validator = RayCastValidator(context.collision)
        val squares = bounds.asSequence()
        return squares.all {
            validator.hasLineOfWalk(from, it, extraFlag = CollisionFlag.BLOCK_PLAYERS)
        }
    }

    /**
     * Returns `true` if there is a valid line-of-sight from [from] to **every** coordinate occupied
     * by [bounds].
     */
    public fun lineOfSight(from: CoordGrid, bounds: Bounds): Boolean {
        val validator = RayCastValidator(context.collision)
        val squares = bounds.asSequence()
        return squares.all {
            validator.hasLineOfSight(from, it, extraFlag = CollisionFlag.BLOCK_PLAYERS)
        }
    }

    /** Returns `true` if [coord] has the [CollisionFlag.BLOCK_WALK] collision flag set. */
    public fun mapBlocked(coord: CoordGrid): Boolean {
        return context.collision.isWalkBlocked(coord)
    }

    /** @see [NpcSearch.find] */
    public fun npcFind(
        coord: CoordGrid,
        npc: NpcType,
        distance: Int,
        checkVis: HuntVis,
        search: NpcSearch,
    ): Npc? {
        return search.find(coord, npc, distance, checkVis)
    }

    /** @see [NpcSearch.findCat] */
    public fun npcFindCat(
        coord: CoordGrid,
        category: CategoryType,
        distance: Int,
        checkVis: HuntVis,
        search: NpcSearch,
    ): Npc? {
        return search.findCat(coord, category, distance, checkVis)
    }

    /** @see [NpcSearch.findAllAny] */
    public fun npcFindAllAny(
        coord: CoordGrid,
        distance: Int,
        checkVis: HuntVis,
        search: NpcSearch,
    ): Sequence<Npc> {
        return search.findAllAny(coord, distance, checkVis)
    }

    /** @see [NpcSearch.findAll] */
    public fun npcFindAll(
        coord: CoordGrid,
        npc: NpcType,
        distance: Int,
        checkVis: HuntVis,
        search: NpcSearch,
    ): Sequence<Npc> {
        return search.findAll(coord, npc, distance, checkVis)
    }

    /** @see [NpcSearch.findAllZone] */
    public fun npcFindAllZone(
        coord: CoordGrid,
        distance: Int,
        checkVis: HuntVis,
        search: NpcSearch,
    ): Sequence<Npc> {
        return search.findAllZone(coord, distance, checkVis)
    }

    /** @see [PlayerSearch.findAll] */
    public fun huntAll(
        coord: CoordGrid,
        distance: Int,
        checkVis: HuntVis,
        search: PlayerSearch,
    ): Sequence<Player> {
        return search.findAll(coord, distance, checkVis)
    }

    /** @see [NpcSearch.hunt] */
    public fun npcHunt(
        coord: CoordGrid,
        distance: Int,
        checkVis: HuntVis,
        search: NpcSearch,
    ): Npc? {
        return search.hunt(coord, distance, checkVis)
    }

    /** @see [NpcSearch.huntAll] */
    public fun npcHuntAll(
        coord: CoordGrid,
        npc: NpcType,
        distance: Int,
        checkVis: HuntVis,
        search: NpcSearch,
    ): Sequence<Npc> {
        return search.huntAll(coord, npc, distance, checkVis)
    }

    public fun opLoc1(loc: BoundLocInfo) {
        context.locInteractions.interact(player, loc, InteractionOp.Op1)
    }

    public fun opLoc2(loc: BoundLocInfo) {
        context.locInteractions.interact(player, loc, InteractionOp.Op2)
    }

    public fun opLoc3(loc: BoundLocInfo) {
        context.locInteractions.interact(player, loc, InteractionOp.Op3)
    }

    public fun opLoc4(loc: BoundLocInfo) {
        context.locInteractions.interact(player, loc, InteractionOp.Op4)
    }

    public fun opNpc1(npc: Npc) {
        context.npcInteractions.interact(player, npc, InteractionOp.Op1)
    }

    public fun opNpc2(npc: Npc) {
        context.npcInteractions.interact(player, npc, InteractionOp.Op2)
    }

    public fun opNpc3(npc: Npc) {
        context.npcInteractions.interact(player, npc, InteractionOp.Op3)
    }

    public fun opNpc4(npc: Npc) {
        context.npcInteractions.interact(player, npc, InteractionOp.Op4)
    }

    public fun opPlayer2(target: Player) {
        context.playerInteractions.interact(player, target, InteractionOp.Op2)
    }

    /**
     * @throws IllegalStateException if [checkOpHeldCallLimit] exceeds the safety net threshold.
     * @see [checkOpHeldCallLimit]
     */
    public suspend fun opHeld1(invSlot: Int, inv: Inventory = player.inv) {
        checkOpHeldCallLimit()
        context.heldInteractions.interact(this, inv, invSlot, HeldOp.Op1)
    }

    /**
     * Note: If you wish to directly equip an obj bypassing `onOpHeld2` scripts you will need to
     * call [invEquip] instead.
     *
     * @throws IllegalStateException if [checkOpHeldCallLimit] exceeds the safety net threshold.
     * @see [checkOpHeldCallLimit]
     * @see [HeldOp.Op2]
     */
    // TODO: Add specialized `HeldInteractions.opHeld2` function that returns a result type.
    public suspend fun opHeld2(invSlot: Int, inv: Inventory = player.inv) {
        checkOpHeldCallLimit()
        context.heldInteractions.interact(this, inv, invSlot, HeldOp.Op2)
    }

    /**
     * @throws IllegalStateException if [checkOpHeldCallLimit] exceeds the safety net threshold.
     * @see [checkOpHeldCallLimit]
     */
    public suspend fun opHeld3(invSlot: Int, inv: Inventory = player.inv) {
        checkOpHeldCallLimit()
        context.heldInteractions.interact(this, inv, invSlot, HeldOp.Op3)
    }

    /**
     * @throws IllegalStateException if [checkOpHeldCallLimit] exceeds the safety net threshold.
     * @see [checkOpHeldCallLimit]
     */
    public suspend fun opHeld4(invSlot: Int, inv: Inventory = player.inv) {
        checkOpHeldCallLimit()
        context.heldInteractions.interact(this, inv, invSlot, HeldOp.Op4)
    }

    /**
     * Note: If you wish to directly drop an obj bypassing any custom scripts you will need to call
     * [invDrop] instead.
     *
     * @throws IllegalStateException if [checkOpHeldCallLimit] exceeds the safety net threshold.
     * @see [HeldOp.Op5]
     * @see [checkOpHeldCallLimit]
     */
    public suspend fun opHeld5(invSlot: Int, inv: Inventory = player.inv) {
        checkOpHeldCallLimit()
        context.heldInteractions.interact(this, inv, invSlot, HeldOp.Op5)
    }

    /**
     * Note: This function will bypass any `onOpHeld2` scripts attached to the respective obj and
     * will attempt to directly equip it instead. Use [opHeld2] if you wish to avoid this behavior.
     *
     * @return [HeldEquipResult] with result of the attempt to equip respective obj.
     * @see [HeldInteractions.equip]
     */
    public fun invEquip(invSlot: Int, inv: Inventory = player.inv): HeldEquipResult {
        return context.heldInteractions.equip(this, inv, invSlot)
    }

    /**
     * Note: This function will bypass any `onOpHeld5` scripts attached to the respective obj and
     * will attempt to directly drop it instead. Use [opHeld5] if you wish to avoid this behavior.
     *
     * @see [HeldInteractions.drop]
     */
    public suspend fun invDrop(invSlot: Int, inv: Inventory = player.inv) {
        checkOpHeldCallLimit()
        context.heldInteractions.drop(this, inv, invSlot)
    }

    /**
     * Note: This function will bypass any `onOpWorn1` scripts attached to the respective obj and
     * will attempt to directly unequip it instead.
     *
     * @return [WornUnequipResult] with result of the attempt to unequip respective obj.
     * @see [WornInteractions.unequip]
     */
    // TODO: This function needs to be removed and any calls should be replaced with a new
    //  `opWorn1`.
    public fun wornUnequip(
        wornSlot: Int,
        into: Inventory = player.inv,
        from: Inventory = player.worn,
    ): WornUnequipResult {
        return context.wornInteractions.unequip(this, from, wornSlot, into)
    }

    public fun faceSquare(target: CoordGrid) {
        player.faceSquare(target)
    }

    public fun faceDirection(direction: Direction) {
        player.faceDirection(direction)
    }

    public fun faceLoc(loc: BoundLocInfo) {
        player.faceLoc(loc)
    }

    public fun faceEntitySquare(target: PathingEntity) {
        player.facePathingEntitySquare(target)
    }

    public fun stopAction() {
        player.clearPendingAction(context.eventBus)
        player.resetFaceEntity()
        player.clearMapFlag()
        player.abortRoute()
    }

    public fun invTransmit(inv: Inventory) {
        player.startInvTransmit(inv)
    }

    public fun invStopTransmit(inv: Inventory) {
        player.stopInvTransmit(inv)
    }

    public fun invAdd(
        inv: Inventory,
        obj: InvObj,
        slot: Int? = null,
        strict: Boolean = true,
        cert: Boolean = false,
        uncert: Boolean = false,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> {
        return player.invAdd(
            inv = inv,
            obj = obj.id,
            count = obj.count,
            vars = obj.vars,
            slot = slot,
            strict = strict,
            cert = cert,
            uncert = uncert,
            autoCommit = autoCommit,
        )
    }

    public fun invAdd(
        inv: Inventory,
        type: ObjType,
        count: Int = 1,
        vars: Int = 0,
        slot: Int? = null,
        strict: Boolean = true,
        cert: Boolean = false,
        uncert: Boolean = false,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> {
        return player.invAdd(
            inv = inv,
            type = type,
            count = count,
            vars = vars,
            slot = slot,
            strict = strict,
            cert = cert,
            uncert = uncert,
            autoCommit = autoCommit,
        )
    }

    /**
     * Attempts to add exactly [count] of [obj] into [inv]. If the inventory cannot fit the items,
     * they will instead be dropped on the floor, with [player] as the "owner," and this function
     * will return `false`. If the items are successfully placed in [inv], it returns `true`.
     */
    public fun invAddOrDrop(
        repo: ObjRepository,
        obj: ObjType,
        count: Int = 1,
        coords: CoordGrid = this.coords,
        inv: Inventory = this.inv,
    ): Boolean {
        return player.invAddOrDrop(repo, obj, count, coords = coords, inv = inv)
    }

    public fun invDel(
        inv: Inventory,
        type: ObjType,
        count: Int = 1,
        slot: Int? = null,
        strict: Boolean = true,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> {
        return player.invDel(
            inv = inv,
            type = type,
            count = count,
            slot = slot,
            strict = strict,
            autoCommit = autoCommit,
        )
    }

    public fun invDel(
        inv: Inventory,
        type1: ObjType,
        count1: Int,
        type2: ObjType,
        count2: Int,
        strict: Boolean = true,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> {
        return player.invDel(
            inv = inv,
            type1 = type1,
            count1 = count1,
            type2 = type2,
            count2 = count2,
            strict = strict,
            autoCommit = autoCommit,
        )
    }

    public fun invDel(
        inv: Inventory,
        type1: ObjType,
        count1: Int,
        type2: ObjType,
        count2: Int,
        type3: ObjType,
        count3: Int,
        strict: Boolean = true,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> {
        return player.invDel(
            inv = inv,
            type1 = type1,
            count1 = count1,
            type2 = type2,
            count2 = count2,
            type3 = type3,
            count3 = count3,
            strict = strict,
            autoCommit = autoCommit,
        )
    }

    /**
     * This transaction will remove the first found inv obj associated with [replace] based on their
     * slot.
     *
     * If [count] amount of the inv obj could not be deleted, this transaction will fail.
     *
     * _Note: This function will add the [replacement] obj in the first empty and valid slot._
     */
    public fun invReplace(
        inv: Inventory,
        replace: ObjType,
        count: Int,
        replacement: ObjType,
        vars: Int = 0,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> {
        return player.invTransaction(inv, autoCommit) {
            val fromInv = select(inv)
            delete {
                this.from = fromInv
                this.obj = replace.id
                this.strictCount = count
            }
            insert {
                this.into = fromInv
                this.obj = replacement.id
                this.strictCount = count
                this.vars = vars
            }
        }
    }

    /**
     * This transaction will remove the inv obj occupying slot [slot], resulting in failure if there
     * is no obj in said `slot`, or if there are any other implicit transaction errors.
     *
     * _Note: This function will add the new [replacement] obj in the first empty and valid slot. If
     * you wish to add the item into [slot] instead, use [invReplaceSlot]._
     *
     * @see [invReplaceSlot]
     */
    public fun invReplace(
        inv: Inventory,
        slot: Int,
        count: Int,
        replacement: ObjType,
        vars: Int = 0,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> {
        // The transaction will implicitly fail if the obj is null - no verification is required
        // at this level.
        val deleteObj = inv[slot]
        return player.invTransaction(inv, autoCommit) {
            val fromInv = select(inv)
            delete {
                this.from = fromInv
                this.obj = deleteObj?.id
                this.strictCount = count
                this.strictSlot = slot
            }
            insert {
                this.into = fromInv
                this.obj = replacement.id
                this.strictCount = count
                this.vars = vars
            }
        }
    }

    public fun invReplace(
        inv: Inventory,
        slot: Int,
        count: Int,
        replacement: Int,
        vars: Int = 0,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> {
        // The transaction will implicitly fail if the obj is null - no verification is required
        // at this level.
        val deleteObj = inv[slot]
        return player.invTransaction(inv, autoCommit) {
            val fromInv = select(inv)
            delete {
                this.from = fromInv
                this.obj = deleteObj?.id
                this.strictCount = count
                this.strictSlot = slot
            }
            insert {
                this.into = fromInv
                this.obj = replacement
                this.strictCount = count
                this.vars = vars
            }
        }
    }

    /**
     * This transaction will remove the inv obj occupying slot [slot], resulting in failure if there
     * is no obj in said `slot`, or if there are any other implicit transaction errors.
     *
     * _Note: This function will strictly add the new [replacement] obj in the [slot] slot. If you
     * wish for the obj to take the first available slot instead, use [invReplace]._
     *
     * @see [invReplace]
     */
    public fun invReplaceSlot(
        inv: Inventory,
        slot: Int,
        count: Int,
        replacement: ObjType,
        vars: Int = 0,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> {
        // The transaction will implicitly fail if the obj is null - no verification is required
        // at this level.
        val deleteObj = inv[slot]
        return player.invTransaction(inv, autoCommit) {
            val fromInv = select(inv)
            delete {
                this.from = fromInv
                this.obj = deleteObj?.id
                this.strictCount = count
                this.strictSlot = slot
            }
            insert {
                this.into = fromInv
                this.obj = replacement.id
                this.strictSlot = slot
                this.strictCount = count
                this.vars = vars
            }
        }
    }

    public fun invMoveToSlot(
        from: Inventory,
        into: Inventory,
        fromSlot: Int,
        intoSlot: Int,
        strict: Boolean = true,
    ): TransactionResultList<InvObj> {
        val resolvedInto = if (from === into) null else into
        return player.invSwap(
            from = from,
            into = resolvedInto,
            fromSlot = fromSlot,
            intoSlot = intoSlot,
            strict = strict,
        )
    }

    public fun invMoveFromSlot(
        from: Inventory,
        into: Inventory,
        fromSlot: Int,
        count: Int = 1,
        intoSlot: Int? = null,
        strict: Boolean = true,
        cert: Boolean = false,
        uncert: Boolean = false,
        placehold: Boolean = false,
    ): TransactionResultList<InvObj> {
        return player.invTransfer(
            from = from,
            into = into,
            count = count,
            fromSlot = fromSlot,
            intoSlot = intoSlot,
            strict = strict,
            cert = cert,
            uncert = uncert,
            placehold = placehold,
        )
    }

    public fun invMoveInv(
        from: Inventory,
        into: Inventory,
        untransform: Boolean = false,
        intoStartSlot: Int = 0,
        intoCapacity: Int? = null,
        keepSlots: Set<Int>? = null,
    ): TransactionResultList<InvObj> {
        return player.invMoveAll(
            from = from,
            into = into,
            untransform = untransform,
            intoStartSlot = intoStartSlot,
            intoCapacity = intoCapacity,
            keepSlots = keepSlots,
        )
    }

    public fun invMoveAll(
        into: Inventory,
        objs: Iterable<InvObj>,
        startSlot: Int? = null,
        strict: Boolean = true,
        cert: Boolean = false,
        uncert: Boolean = false,
        autoCommit: Boolean = true,
    ): TransactionResultList<InvObj> {
        return player.invAddAll(
            inv = into,
            objs = objs,
            startSlot = startSlot,
            strict = strict,
            cert = cert,
            uncert = uncert,
            autoCommit = autoCommit,
        )
    }

    public fun invCompress(inventory: Inventory): TransactionResultList<InvObj> {
        return player.invCompress(inventory)
    }

    public fun invClear(inventory: Inventory) {
        player.invClear(inventory)
    }

    public fun objExamine(
        inventory: Inventory,
        slot: Int,
        marketPrices: MarketPrices = context.marketPrices,
    ) {
        val obj = inventory[slot] ?: return resendSlot(inventory, 0)
        val normalized = context.objTypes.normalize(context.objTypes[obj])
        player.objExamine(normalized, obj.count, marketPrices[normalized] ?: 0)
    }

    /** @see [org.rsmod.api.player.stat.stat] */
    public fun stat(stat: StatType): Int {
        return player.stat(stat)
    }

    /** @see [org.rsmod.api.player.stat.statBase] */
    public fun statBase(stat: StatType): Int {
        return player.statBase(stat)
    }

    /** @see [org.rsmod.api.player.stat.statRestore] */
    public fun statRestore(stat: StatType) {
        player.statRestore(stat)
    }

    /** @see [org.rsmod.api.player.stat.statRestoreAll] */
    public fun statRestoreAll(stats: Iterable<StatType>) {
        player.statRestoreAll(stats)
    }

    /** @see [org.rsmod.api.player.stat.statAdvance] */
    public fun statAdvance(
        stat: StatType,
        xp: Double,
        rate: Double = player.xpRate,
        globalRate: Double = player.globalXpRate,
    ): Int {
        return player.statAdvance(stat, xp, rate, globalRate)
    }

    /** @see [org.rsmod.api.player.stat.statAdd] */
    public fun statAdd(stat: StatType, constant: Int, percent: Int) {
        player.statAdd(stat, constant, percent)
    }

    /** @see [org.rsmod.api.player.stat.statBoost] */
    public fun statBoost(stat: StatType, constant: Int, percent: Int) {
        player.statBoost(stat, constant, percent)
    }

    /** @see [org.rsmod.api.player.stat.statSub] */
    public fun statSub(stat: StatType, constant: Int, percent: Int) {
        player.statSub(stat, constant, percent)
    }

    /** @see [org.rsmod.api.player.stat.statDrain] */
    public fun statDrain(stat: StatType, constant: Int, percent: Int) {
        player.statDrain(stat, constant, percent)
    }

    /** @see [org.rsmod.api.player.stat.statHeal] */
    public fun statHeal(stat: StatType, constant: Int, percent: Int) {
        player.statHeal(stat, constant, percent)
    }

    /** @see [org.rsmod.api.player.stat.statRandom] */
    public fun statRandom(stat: StatType, low: Int, high: Int, invisibleBoost: Int): Boolean {
        return player.statRandom(random, stat, low, high, invisibleBoost)
    }

    /** @see [org.rsmod.api.player.stat.statRandom] */
    public fun statRandom(
        stat: StatType,
        low: Int,
        high: Int,
        invisibleLevels: InvisibleLevels,
    ): Boolean {
        return player.statRandom(random, stat, low, high, invisibleLevels)
    }

    public fun isInCombat(): Boolean {
        return player.isInCombat()
    }

    public fun isInPvpCombat(): Boolean {
        return player.isInPvpCombat()
    }

    public fun isInPvnCombat(): Boolean {
        return player.isInPvnCombat()
    }

    public fun isOutOfCombat(): Boolean {
        return player.isOutOfCombat()
    }

    public fun queueDeath() {
        player.queueDeath()
    }

    /**
     * Queues a hit dealt by [source] with an impact cycle delay of [delay] before the hit is
     * displayed and health is deducted from the player.
     *
     * _[modifier] is applied immediately when this function is called (via
     * [PlayerHitModifier.modify]). This means that effects like prayer protection reducing damage
     * are handled at this point and **not** on impact._
     *
     * If you want the modifier to be applied on impact, use [queueImpactHit] instead.
     *
     * **Notes:**
     * - [damage] is capped to the [player]'s current health at the time this function is called.
     *   This ensures that the "tick-eating" mechanic is possible.
     * - [StandardPlayerHitProcessor] is invoked when the cycle [delay] completes and the hit takes
     *   effect. It is responsible for reducing the [player]'s health, handling armour degradation,
     *   recoil damage, displaying the hitsplat, and other related mechanics. This behavior **cannot
     *   be bypassed** when using this function; however, it can be changed when using
     *   [takeInstantHit].
     * - As the hit is immediately modified, this function **returns an accurate** [Hit]
     *   representation of what will be dealt once the cycle [delay] passes. The only exception is
     *   if the [player]'s respective queue list is cleared, which would remove the hit before it
     *   has been processed.
     *
     * @param damage The initial damage intended for the [player]. This value may change based on
     *   various factors from [modifier].
     * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
     *   reference [hitmark_groups] for a list of available hitmark groups.
     * @param specific If `true`, only the [player] will see the hitsplat; this does not affect
     *   actual damage calculations.
     * @param sourceWeapon An optional [ObjType] reference of a "weapon" used by the [source] that
     *   hit modifiers and/or processors can use for specialized logic. Typically unnecessary when
     *   [source] is an [Npc], though there may be niche use cases.
     * @param sourceSecondary Similar to [sourceWeapon], except this refers to objs that are **not**
     *   the primary weapon, such as ammunition for ranged attacks or objs tied to magic spells.
     * @param modifier A [PlayerHitModifier] used to adjust damage and other hit properties. By
     *   default, this is set to [StandardPlayerHitModifier], which applies standard modifications,
     *   such as damage reduction from protection prayers.
     * @see [BaseHitmarkGroups]
     */
    public fun queueHit(
        source: Npc,
        delay: Int,
        type: HitType,
        damage: Int,
        hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
        specific: Boolean = false,
        sourceWeapon: ObjType? = null,
        sourceSecondary: ObjType? = null,
        modifier: PlayerHitModifier = StandardPlayerHitModifier,
    ): Hit {
        return player.queueHit(
            source = source,
            delay = delay,
            type = type,
            damage = damage,
            hitmark = hitmark,
            specific = specific,
            sourceWeapon = sourceWeapon,
            sourceSecondary = sourceSecondary,
            modifier = modifier,
        )
    }

    /**
     * Queues a hit dealt by [source] with an impact cycle delay of [delay] before the hit is
     * displayed and health is deducted from the player.
     *
     * _[modifier] is applied immediately when this function is called (via
     * [PlayerHitModifier.modify]). This means that effects like prayer protection reducing damage
     * are handled at this point and **not** on impact._
     *
     * If you want the modifier to be applied on impact, use [queueImpactHit] instead.
     *
     * **Notes:**
     * - The [Hit.righthandObj] is implicitly set based on the `righthand` obj equipped in
     *   [Player.worn] for [source]. This behavior is not configurable to ensure consistency across
     *   systems such as [modifier] and other processors.
     * - [StandardPlayerHitProcessor] is invoked when the cycle [delay] completes and the hit takes
     *   effect. It is responsible for reducing the [player]'s health, handling armour degradation,
     *   recoil damage, displaying the hitsplat, and other related mechanics. This behavior **cannot
     *   be bypassed** when using this function; however, it can be changed when using
     *   [takeInstantHit].
     * - As the hit is immediately modified, this function **returns an accurate** [Hit]
     *   representation of what will be dealt once the cycle [delay] passes. The only exception is
     *   if the [player]'s respective queue list is cleared, which would remove the hit before it
     *   has been processed.
     *
     * @param damage The initial damage intended for the [player]. This value may change based on
     *   various factors from [modifier].
     * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
     *   reference [hitmark_groups] for a list of available hitmark groups.
     * @param sourceSecondary The "secondary" obj used in the attack by [source]. If the hit is from
     *   a ranged attack, this should be set to the ammunition obj (if applicable). If the attack is
     *   from a magic spell, this should be the associated spell obj.
     * @param modifier A [PlayerHitModifier] used to adjust damage and other hit properties. By
     *   default, this is set to [StandardPlayerHitModifier], which applies standard modifications,
     *   such as damage reduction from protection prayers.
     * @see [BaseHitmarkGroups]
     */
    public fun queueHit(
        source: Player,
        delay: Int,
        type: HitType,
        damage: Int,
        hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
        sourceSecondary: ObjType? = null,
        modifier: PlayerHitModifier = StandardPlayerHitModifier,
    ): Hit {
        return player.queueHit(
            source = source,
            delay = delay,
            type = type,
            damage = damage,
            hitmark = hitmark,
            sourceSecondary = sourceSecondary,
            modifier = modifier,
        )
    }

    /**
     * Queues a hit that does not originate from either a [Player] or an [Npc], with an impact cycle
     * delay of [delay] before the hit is displayed and health is deducted from the player.
     *
     * _[modifier] is applied immediately when this function is called (via
     * [PlayerHitModifier.modify]). This means that effects like prayer protection reducing damage
     * are handled at this point and **not** on impact._
     *
     * If you want the modifier to be applied on impact, use [queueImpactHit] instead.
     *
     * **Notes:**
     * - [StandardPlayerHitProcessor] is invoked when the cycle [delay] completes and the hit takes
     *   effect. It is responsible for reducing the [player]'s health, handling armour degradation,
     *   recoil damage, displaying the hitsplat, and other related mechanics. This behavior **cannot
     *   be bypassed** when using this function; however, it can be changed when using
     *   [takeInstantHit].
     * - As the hit is immediately modified, this function **returns an accurate** [Hit]
     *   representation of what will be dealt once the cycle [delay] passes. The only exception is
     *   if the [player]'s respective queue list is cleared, which would remove the hit before it
     *   has been processed.
     *
     * @param damage The initial damage intended for the [player]. This value may change based on
     *   various factors from [modifier].
     * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
     *   reference [hitmark_groups] for a list of available hitmark groups.
     * @param specific If `true`, only the [player] will see the hitsplat; this does not affect
     *   actual damage calculations.
     * @param modifier A [PlayerHitModifier] used to adjust damage and other hit properties. By
     *   default, this is set to [StandardPlayerHitModifier], which applies standard modifications,
     *   such as damage reduction from protection prayers.
     * @param strongQueue If `false`, the hit will be queued through [Player.queue] instead of
     *   [Player.strongQueue]. This is `true` by default. Currently, the only known case for setting
     *   this to `false` is for 'Ring of Recoil' damage, but other use cases may exist.
     * @see [BaseHitmarkGroups]
     */
    public fun queueHit(
        delay: Int,
        type: HitType,
        damage: Int,
        hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
        specific: Boolean = false,
        modifier: PlayerHitModifier = StandardPlayerHitModifier,
        strongQueue: Boolean = true,
    ): Hit {
        return player.queueHit(
            delay = delay,
            type = type,
            damage = damage,
            hitmark = hitmark,
            specific = specific,
            modifier = modifier,
            strongQueue = strongQueue,
        )
    }

    /**
     * Instantly applies [damage] to [player]. By default, this function applies no modification to
     * the hit ([NoopPlayerHitModifier]) unless explicitly provided through [modifier].
     *
     * @param damage The initial damage intended for the [player]. This value may be adjusted by
     *   [modifier] based on various factors.
     * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
     *   reference [hitmark_groups] for a list of available hitmark groups.
     * @param specific If `true`, only the [player] will see the hitsplat; this does not affect
     *   actual damage calculations.
     * @param modifier A [PlayerHitModifier] that modifies the damage and other properties.
     * @param processor An [InstantPlayerHitProcessor] that processes the [Hit] instantly. Defaults
     *   to [DamageOnlyPlayerHitProcessor], meaning effects such as degradation and recoil damage
     *   **will not** apply.
     */
    public fun takeInstantHit(
        type: HitType,
        damage: Int,
        hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
        specific: Boolean = false,
        modifier: PlayerHitModifier = NoopPlayerHitModifier,
        processor: InstantPlayerHitProcessor = context.instantHitProcessor,
    ): Hit {
        return player.takeInstantHit(
            type = type,
            damage = damage,
            hitmark = hitmark,
            specific = specific,
            modifier = modifier,
            processor = processor,
        )
    }

    /**
     * Queues a hit dealt by [source] with an impact cycle delay of [delay] before the hit is
     * displayed and health is deducted from the player.
     *
     * _[modifier] is applied **on impact** (via [PlayerHitModifier.modify]). This means that
     * effects like prayer protection reducing damage are handled right before the hit damage is
     * reduced from the player's health._
     *
     * If you want to apply the modifier as soon as the hit is queued, use [queueHit] instead.
     *
     * **Notes:**
     * - [damage] is **not** capped based on the [player]'s current health.
     * - [StandardPlayerHitProcessor] is invoked when the cycle [delay] completes and the hit takes
     *   effect. It is responsible for reducing the [player]'s health, handling armour degradation,
     *   recoil damage, displaying the hitsplat, and other related mechanics. This behavior **cannot
     *   be bypassed** when using this function; however, it can be changed when using
     *   [takeInstantHit].
     * - Unlike the [queueHit] variants, this function **cannot** return an accurate [Hit]
     *   representation (and thus does not return one at all). This is because the hit is scheduled
     *   for modification only _after_ [delay] cycles have passed, and the only way to retrieve the
     *   modified value would be by suspending execution - something we do not do here for multiple
     *   reasons.
     *
     * @param damage The initial damage intended for the [player]. This value may change based on
     *   various factors from [modifier].
     * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
     *   reference [hitmark_groups] for a list of available hitmark groups.
     * @param specific If `true`, only the [player] will see the hitsplat; this does not affect
     *   actual damage calculations.
     * @param sourceWeapon An optional [ObjType] reference of a "weapon" used by the [source] that
     *   hit modifiers and/or processors can use for specialized logic. Typically unnecessary when
     *   [source] is an [Npc], though there may be niche use cases.
     * @param sourceSecondary Similar to [sourceWeapon], except this refers to objs that are **not**
     *   the primary weapon, such as ammunition for ranged attacks or objs tied to magic spells.
     * @param modifier A [PlayerHitModifier] used to adjust damage and other hit properties. By
     *   default, this is set to [StandardPlayerHitModifier], which applies standard modifications,
     *   such as damage reduction from protection prayers.
     * @see [BaseHitmarkGroups]
     */
    public fun queueImpactHit(
        source: Npc,
        delay: Int,
        type: HitType,
        damage: Int,
        hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
        specific: Boolean = false,
        sourceWeapon: ObjType? = null,
        sourceSecondary: ObjType? = null,
        modifier: PlayerHitModifier = StandardPlayerHitModifier,
    ) {
        player.queueImpactHit(
            source = source,
            delay = delay,
            type = type,
            damage = damage,
            hitmark = hitmark,
            specific = specific,
            sourceWeapon = sourceWeapon,
            sourceSecondary = sourceSecondary,
            modifier = modifier,
        )
    }

    /**
     * Queues a hit dealt by [source] with an impact cycle delay of [delay] before the hit is
     * displayed and health is deducted from the player.
     *
     * _[modifier] is applied **on impact** (via [PlayerHitModifier.modify]). This means that
     * effects like prayer protection reducing damage are handled right before the hit damage is
     * reduced from the player's health._
     *
     * If you want to apply the modifier as soon as the hit is queued, use [queueHit] instead.
     *
     * **Notes:**
     * - The [Hit.righthandObj] is implicitly set based on the `righthand` obj equipped in
     *   [Player.worn] for [source]. This behavior is not configurable to ensure consistency across
     *   systems such as [modifier] and other processors.
     * - [StandardPlayerHitProcessor] is invoked when the cycle [delay] completes and the hit takes
     *   effect. It is responsible for reducing the [player]'s health, handling armour degradation,
     *   recoil damage, displaying the hitsplat, and other related mechanics. This behavior **cannot
     *   be bypassed** when using this function; however, it can be changed when using
     *   [takeInstantHit].
     * - Unlike the [queueHit] variants, this function **cannot** return an accurate [Hit]
     *   representation (and thus does not return one at all). This is because the hit is scheduled
     *   for modification only _after_ [delay] cycles have passed, and the only way to retrieve the
     *   modified value would be by suspending execution - something we do not do here for multiple
     *   reasons.
     *
     * @param damage The initial damage intended for the [player]. This value may change based on
     *   various factors from [modifier].
     * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
     *   reference [hitmark_groups] for a list of available hitmark groups.
     * @param sourceSecondary The "secondary" obj used in the attack by [source]. If the hit is from
     *   a ranged attack, this should be set to the ammunition obj (if applicable). If the attack is
     *   from a magic spell, this should be the associated spell obj.
     * @param modifier A [PlayerHitModifier] used to adjust damage and other hit properties. By
     *   default, this is set to [StandardPlayerHitModifier], which applies standard modifications,
     *   such as damage reduction from protection prayers.
     * @see [BaseHitmarkGroups]
     */
    public fun queueImpactHit(
        source: Player,
        delay: Int,
        type: HitType,
        damage: Int,
        hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
        sourceSecondary: ObjType? = null,
        modifier: PlayerHitModifier = StandardPlayerHitModifier,
    ) {
        player.queueImpactHit(
            source = source,
            delay = delay,
            type = type,
            damage = damage,
            hitmark = hitmark,
            sourceSecondary = sourceSecondary,
            modifier = modifier,
        )
    }

    /**
     * Queues a hit that does not originate from either a [Player] or an [Npc], with an impact cycle
     * delay of [delay] before the hit is displayed and health is deducted from the player.
     *
     * _[modifier] is applied **on impact** (via [PlayerHitModifier.modify]). This means that
     * effects like prayer protection reducing damage are handled right before the hit damage is
     * reduced from the player's health._
     *
     * If you want to apply the modifier as soon as the hit is queued, use [queueHit] instead.
     *
     * **Notes:**
     * - [StandardPlayerHitProcessor] is invoked when the cycle [delay] completes and the hit takes
     *   effect. It is responsible for reducing the [player]'s health, handling armour degradation,
     *   recoil damage, displaying the hitsplat, and other related mechanics. This behavior **cannot
     *   be bypassed** when using this function; however, it can be changed when using
     *   [takeInstantHit].
     * - Unlike the [queueHit] variants, this function **cannot** return an accurate [Hit]
     *   representation (and thus does not return one at all). This is because the hit is scheduled
     *   for modification only _after_ [delay] cycles have passed, and the only way to retrieve the
     *   modified value would be by suspending execution - something we do not do here for multiple
     *   reasons.
     *
     * @param damage The initial damage intended for the [player]. This value may change based on
     *   various factors from [modifier].
     * @param hitmark The hitmark group used for the visual hitsplat. See [BaseHitmarkGroups] or
     *   reference [hitmark_groups] for a list of available hitmark groups.
     * @param specific If `true`, only the [player] will see the hitsplat; this does not affect
     *   actual damage calculations.
     * @param modifier A [PlayerHitModifier] used to adjust damage and other hit properties. By
     *   default, this is set to [StandardPlayerHitModifier], which applies standard modifications,
     *   such as damage reduction from protection prayers.
     * @see [BaseHitmarkGroups]
     */
    public fun queueImpactHit(
        delay: Int,
        type: HitType,
        damage: Int,
        hitmark: HitmarkTypeGroup = hitmark_groups.regular_damage,
        specific: Boolean = false,
        modifier: PlayerHitModifier = StandardPlayerHitModifier,
    ) {
        player.queueImpactHit(
            delay = delay,
            type = type,
            damage = damage,
            hitmark = hitmark,
            specific = specific,
            modifier = modifier,
            strongQueue = true,
        )
    }

    internal fun findHitNpcSource(hit: Hit): Npc? {
        return hit.resolveNpcSource(context.npcList)
    }

    internal fun findHitPlayerSource(hit: Hit): Player? {
        return hit.resolvePlayerSource(context.playerList)
    }

    @InternalApi
    public fun processQueuedHit(hit: Hit) {
        processQueuedHit(hit, StandardPlayerHitProcessor)
    }

    @InternalApi
    public fun processQueuedHit(builder: HitBuilder, modifier: PlayerHitModifier) {
        processQueuedHit(builder, modifier, StandardPlayerHitProcessor)
    }

    public fun restoreToplevelTabs(tabTargets: Iterable<ComponentType>) {
        // TODO(combat): Publish gameframe-related event for `restoretabs`.
    }

    public fun restoreToplevelTabs(vararg tabTarget: ComponentType) {
        restoreToplevelTabs(tabTarget.toList())
    }

    public fun timer(timerType: TimerType, cycles: Int) {
        player.timer(timerType, cycles)
    }

    public fun clearTimer(timerType: TimerType) {
        player.clearTimer(timerType)
    }

    public fun softTimer(timerType: TimerType, cycles: Int) {
        player.softTimer(timerType, cycles)
    }

    public fun clearSoftTimer(timerType: TimerType) {
        player.clearSoftTimer(timerType)
    }

    public fun weakQueue(queue: QueueType, cycles: Int, args: Any? = null) {
        player.weakQueue(queue, cycles, args)
    }

    public fun clearWeakQueue(queue: QueueType) {
        player.clearWeakQueue(queue)
    }

    public fun softQueue(queue: QueueType, cycles: Int, args: Any? = null) {
        player.softQueue(queue, cycles, args)
    }

    public fun queue(queue: QueueType, cycles: Int, args: Any? = null) {
        player.queue(queue, cycles, args)
    }

    public fun strongQueue(queue: QueueType, cycles: Int, args: Any? = null) {
        player.strongQueue(queue, cycles, args)
    }

    public fun longQueueAccelerate(queue: QueueType, cycles: Int, args: Any? = null) {
        player.longQueueAccelerate(queue, cycles, args)
    }

    public fun longQueueDiscard(queue: QueueType, cycles: Int, args: Any? = null) {
        player.longQueueDiscard(queue, cycles, args)
    }

    public fun clearQueue(queue: QueueType) {
        player.clearQueue(queue)
    }

    /**
     * Adds "hero points" (also known as kill credits) for [source], where [points] typically
     * represent the amount of damage dealt to [player].
     */
    public fun heroPoints(source: Player, points: Int) {
        player.heroPoints(source, points)
    }

    /**
     * Finds the player with the highest "hero points" stored in this [Player.heroPoints].
     *
     * **Notes:**
     * - Only players who have dealt damage greater than `0` can occupy an entry.
     * - [Player.heroPoints] is limited to `16` entries by default. Once all entries are occupied,
     *   no additional players can accrue kill credit (hero points) for this [player] until their
     *   hero points are cleared.
     */
    public fun findHero(): Player? {
        return player.findHero(context.playerList)
    }

    /** Returns `true` if [coords] is within [area]. */
    public fun inArea(area: AreaType, coords: CoordGrid): Boolean {
        return context.areaChecker.inArea(area, coords)
    }

    /** @see [org.rsmod.api.player.mapMultiway] */
    public fun mapMultiway(): Boolean {
        return player.mapMultiway(context.areaChecker)
    }

    /** @see [org.rsmod.api.player.music.MusicPlayer.unlockAndPlay] */
    public fun musicPlay(musicRow: DbRowType) {
        context.musicPlayer.unlockAndPlay(player, musicRow)
    }

    public fun combatClearQueue() {
        player.combatClearQueue()
    }

    public fun clearPendingAction() {
        player.clearPendingAction(context.eventBus)
    }

    /**
     * Sets the player's `walkTrigger` to [trigger], allowing the underlying [Player.walkTrigger]
     * function to determine if the change is permitted based on the [WalkTriggerType.priority]
     * rules.
     *
     * Unlike [trySetWalkTrigger], this function does *not* return a success status. If it is
     * important to know whether the trigger was applied, use [trySetWalkTrigger] instead.
     *
     * **See:** Documentation in [WalkTriggerPriority] for priority rules.
     *
     * @see [WalkTriggerPriority.None]
     * @see [WalkTriggerPriority.Low]
     * @see [WalkTriggerPriority.High]
     */
    public fun walkTrigger(trigger: WalkTriggerType) {
        player.walkTrigger(trigger)
    }

    /**
     * Checks whether [trigger] can replace the player's current walk trigger, based on the
     * [WalkTriggerType.priority] rules.
     *
     * This does *not* modify the player's walk trigger.
     *
     * **See:** Documentation in [WalkTriggerPriority] for priority rules.
     *
     * @see [WalkTriggerPriority.None]
     * @see [WalkTriggerPriority.Low]
     * @see [WalkTriggerPriority.High]
     */
    public fun canSetWalkTrigger(trigger: WalkTriggerType): Boolean {
        val current = player.walkTrigger ?: return true
        return trigger.priority.canOverwrite(current.priority)
    }

    /**
     * Attempts to set the players `walkTrigger` to [trigger], ensuring that walk trigger priority
     * rules are respected.
     *
     * If the existing walk trigger cannot be overwritten due to its priority, the update is
     * **rejected**, and this function returns `false`. Otherwise, the trigger is updated, and
     * `true` is returned.
     *
     * **See:** Documentation in [WalkTriggerPriority] for priority rules.
     *
     * @see [WalkTriggerPriority.None]
     * @see [WalkTriggerPriority.Low]
     * @see [WalkTriggerPriority.High]
     */
    public fun trySetWalkTrigger(trigger: WalkTriggerType): Boolean {
        return player.walkTrigger(trigger)
    }

    public fun publish(event: UnboundEvent) {
        context.eventBus.publish(event)
    }

    public fun publish(event: KeyedEvent) {
        context.eventBus.publish(event)
    }

    public suspend fun <T : SuspendEvent<ProtectedAccess>> publish(event: T): Boolean {
        return context.eventBus.publish(this, event)
    }

    public fun logOut() {
        if (canLogout()) {
            assertLogoutState()
            player.manualLogout = true
        }
    }

    public fun preventLogout(message: String, cycles: Int) {
        player.preventLogoutMessage = message
        player.preventLogoutUntil = mapClock + cycles
    }

    public suspend fun startDialogue(conversation: suspend Dialogue.() -> Unit) {
        val dialogue = Dialogue(this, npc = null, faceFar = false)
        conversation(dialogue)
    }

    public suspend fun startDialogue(
        npc: Npc,
        faceFar: Boolean = false,
        conversation: suspend Dialogue.() -> Unit,
    ) {
        val dialogue = Dialogue(this, npc, faceFar)
        conversation(dialogue)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun delay(cycles: Int = 1) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        val modal = player.ui.getModalOrNull(components.mainmodal)
        player.delay(cycles)
        coroutine.pause { player.isNotDelayed }
        resumeWithMainModalProtectedAccess(Unit, modal)
    }

    /**
     * Delays the [player] for a single cycle **only if** they moved in the previous cycle.
     *
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns `false` after
     *   suspension resumes.
     * @see [regainProtectedAccess]
     */
    public suspend fun arriveDelay() {
        if (!player.hasMovedPreviousCycle) {
            return
        }
        delay()
    }

    /**
     * Delays the player for a number of ticks equal to the time duration of [seq].
     *
     * @param seq The seq type whose tick duration determines the delay.
     * @throws IllegalStateException if [UnpackedSeqType.tickDuration] is `0`.
     * @throws ProtectedAccessLostException if [regainProtectedAccess] returns false after
     *   suspension resumes.
     * @see [regainProtectedAccess]
     */
    public suspend fun delay(seq: SeqType) {
        val ticks = context.seqTypes[seq].tickDuration
        check(ticks > 0) { "Seq tick duration must be positive: ${context.seqTypes[seq]}" }
        delay(cycles = ticks)
    }

    /**
     * Delays the player for up to `10` cycles or until the `MapBuildComplete` packet is received,
     * whichever happens first.
     *
     * _Note: This function does not delay or suspend if the player's **current** coords do not
     * require rebuilding their build area._
     *
     * @see [BuildAreaUtils.requiresNewBuildArea]
     * @see [delay]
     */
    public suspend fun loadDelay() {
        val requiresBuildAreaRebuild = BuildAreaUtils.requiresNewBuildArea(player)
        if (!requiresBuildAreaRebuild) {
            return
        }
        for (i in 0 until 10) {
            if (player.lastMapBuildComplete >= mapClock) {
                break
            }
            delay(1)
        }
    }

    /**
     * Suspends the [coroutine] until the player receives the expected [input].
     *
     * **Notes:**
     * - This does not `delay` the player.
     * - This function is niche and only required in specific scenarios. For example, certain modals
     *   suspend while waiting for a "non-traditional" [ResumePauseButtonInput] - such as one sent
     *   from an `IfButton` click. Examples include the bank tutorial and the items-kept-on-death
     *   interfaces.
     * - Since this function calls [resumeWithMainModalProtectedAccess], the player's currently
     *   opened modal (if any) **must remain unchanged** from the start of this call until the
     *   suspension ends. Otherwise, the [ProtectedAccess] scope will terminate itself from
     *   [ProtectedAccessLostException].
     *
     * @param input the expected input type to suspend on, provided as a [KClass].
     * @return the input value of type [T] once received.
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumeWithMainModalProtectedAccess]
     */
    private suspend fun <T : Any> await(input: KClass<T>): T {
        val modal = player.ui.getModalOrNull(components.mainmodal)
        val value = coroutine.pause(input)
        return resumeWithMainModalProtectedAccess(value, modal)
    }

    /**
     * Suspends the coroutine until a [ResumePauseButtonInput] is received.
     *
     * This function is used in specific scenarios where the player must wait for a non-standard
     * input - such as a button click from an interface like the bank tutorial or items kept on
     * death. It ensures that the player's current modal remains unchanged during the suspension to
     * maintain protected access.
     *
     * @return the received [ResumePauseButtonInput] instance.
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [await]
     */
    public suspend fun pauseButton(): ResumePauseButtonInput = await(ResumePauseButtonInput::class)

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun mesbox(text: String) {
        val alignment = context.alignment
        val pages = alignment.generateMesPageList(text)
        for (page in pages) {
            val (pgText, lineCount) = page
            val lineHeight = alignment.mesLineHeight(lineCount)
            mesboxPage(pgText, lineHeight, constants.cm_pausebutton, context.eventBus)
        }
    }

    private suspend fun mesboxPage(
        text: String,
        lineHeight: Int,
        pauseText: String,
        eventBus: EventBus,
    ) {
        player.ifMesbox(text, pauseText, lineHeight, eventBus)
        val modal = player.ui.getModalOrNull(components.chatbox_chatmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.messagebox_pbutton)
    }

    /**
     * Similar to [mesbox], but does **not** suspend nor show a `Click here to continue` pause
     * button.
     *
     * This dialogue is meant to be used in a script sequence where the script itself will replace
     * or close the dialogue after a certain number of cycles.
     */
    public fun mesboxNp(text: String) {
        val alignment = context.alignment
        val pages = alignment.generateMesPageList(text)
        if (pages.size > 1) {
            throw IllegalStateException("Text too long: $text")
        }
        val (pgText, lineCount) = pages.first()
        val lineHeight = alignment.mesLineHeight(lineCount)
        player.ifMesbox(pgText, pauseText = "", lineHeight, context.eventBus)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun objbox(obj: ObjType, text: String) {
        objbox(obj, zoom = 400, text)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun objbox(obj: ObjType, zoom: Int, text: String) {
        val alignment = context.alignment
        val pages = alignment.generateChatPageList(text)
        for (page in pages) {
            objboxPage(obj, zoom, page.text, constants.cm_pausebutton, context.eventBus)
        }
    }

    private suspend fun objboxPage(
        obj: ObjType,
        zoom: Int,
        text: String,
        pauseText: String,
        eventBus: EventBus,
    ) {
        player.ifObjbox(text, obj.id, zoom, pauseText, eventBus)
        val modal = player.ui.getModalOrNull(components.chatbox_chatmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.objectbox_pbutton)
    }

    /**
     * Similar to [objbox], but does **not** suspend nor show a `Click here to continue` pause
     * button.
     *
     * This dialogue is meant to be used in a script sequence where the script itself will replace
     * or close the dialogue after a certain number of cycles.
     */
    public fun objboxNp(obj: ObjType, text: String) {
        objboxNp(obj, zoom = 400, text)
    }

    /**
     * Similar to [objbox], but does **not** suspend nor show a `Click here to continue` pause
     * button.
     *
     * This dialogue is meant to be used in a script sequence where the script itself will replace
     * or close the dialogue after a certain number of cycles.
     */
    public fun objboxNp(obj: ObjType, zoom: Int, text: String) {
        val alignment = context.alignment
        val pages = alignment.generateChatPageList(text)
        if (pages.size > 1) {
            throw IllegalStateException("Text too long: $text")
        }
        val page = pages.first()
        player.ifObjbox(page.text, obj.id, zoom, pauseText = "", context.eventBus)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun objbox(obj: InvObj, text: String) {
        objbox(obj, zoom = 400, text)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun objbox(obj: InvObj, zoom: Int, text: String) {
        val alignment = context.alignment
        val pages = alignment.generateChatPageList(text)
        for (page in pages) {
            objboxPage(obj, zoom, page.text, constants.cm_pausebutton, context.eventBus)
        }
    }

    private suspend fun objboxPage(
        obj: InvObj,
        zoom: Int,
        text: String,
        pauseText: String = constants.cm_pausebutton,
        eventBus: EventBus = context.eventBus,
    ) {
        player.ifObjbox(text, obj.id, zoom, pauseText, eventBus)
        val modal = player.ui.getModalOrNull(components.chatbox_chatmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.objectbox_pbutton)
    }

    /**
     * Similar to [objbox], but does **not** suspend nor show a `Click here to continue` pause
     * button.
     *
     * This dialogue is meant to be used in a script sequence where the script itself will replace
     * or close the dialogue after a certain number of cycles.
     */
    public fun objboxNp(obj: InvObj, text: String) {
        objboxNp(obj, zoom = 400, text)
    }

    /**
     * Similar to [objbox], but does **not** suspend nor show a `Click here to continue` pause
     * button.
     *
     * This dialogue is meant to be used in a script sequence where the script itself will replace
     * or close the dialogue after a certain number of cycles.
     */
    public fun objboxNp(obj: InvObj, zoom: Int, text: String) {
        val alignment = context.alignment
        val pages = alignment.generateChatPageList(text)
        if (pages.size > 1) {
            throw IllegalStateException("Text too long: $text")
        }
        val page = pages.first()
        player.ifObjbox(page.text, obj.id, zoom, pauseText = "", context.eventBus)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun doubleobjbox(obj1: ObjType, obj2: ObjType, text: String) {
        doubleobjbox(obj1, zoom1 = 400, obj2, zoom2 = 400, text)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun doubleobjbox(
        obj1: ObjType,
        zoom1: Int,
        obj2: ObjType,
        zoom2: Int,
        text: String,
    ) {
        val alignment = context.alignment
        val pages = alignment.generateChatPageList(text)
        for (page in pages) {
            doubleobjboxPage(
                obj1 = obj1,
                zoom1 = zoom1,
                obj2 = obj2,
                zoom2 = zoom2,
                text = page.text,
                pauseText = constants.cm_pausebutton,
                eventBus = context.eventBus,
            )
        }
    }

    private suspend fun doubleobjboxPage(
        obj1: ObjType,
        zoom1: Int,
        obj2: ObjType,
        zoom2: Int,
        text: String,
        pauseText: String,
        eventBus: EventBus,
    ) {
        player.ifDoubleobjbox(text, obj1.id, zoom1, obj2.id, zoom2, pauseText, eventBus)
        val modal = player.ui.getModalOrNull(components.chatbox_chatmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.objectbox_double_pbutton)
    }

    /**
     * Similar to [doubleobjbox], but does **not** suspend nor show a `Click here to continue` pause
     * button.
     *
     * This dialogue is meant to be used in a script sequence where the script itself will replace
     * or close the dialogue after a certain number of cycles.
     */
    public fun doubleobjboxNp(obj1: ObjType, obj2: ObjType, text: String) {
        doubleobjboxNp(obj1, zoom1 = 400, obj2, zoom2 = 400, text)
    }

    /**
     * Similar to [doubleobjbox], but does **not** suspend nor show a `Click here to continue` pause
     * button.
     *
     * This dialogue is meant to be used in a script sequence where the script itself will replace
     * or close the dialogue after a certain number of cycles.
     */
    public fun doubleobjboxNp(obj1: ObjType, zoom1: Int, obj2: ObjType, zoom2: Int, text: String) {
        val alignment = context.alignment
        val pages = alignment.generateChatPageList(text)
        if (pages.size > 1) {
            throw IllegalStateException("Text too long: $text")
        }
        val page = pages.first()
        player.ifDoubleobjbox(page.text, obj1.id, zoom1, obj2.id, zoom2, "", context.eventBus)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun doubleobjbox(obj1: InvObj, obj2: InvObj, text: String) {
        doubleobjbox(obj1, zoom1 = 400, obj2, zoom2 = 400, text)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun doubleobjbox(
        obj1: InvObj,
        zoom1: Int,
        obj2: InvObj,
        zoom2: Int,
        text: String,
    ) {
        val alignment = context.alignment
        val pages = alignment.generateChatPageList(text)
        for (page in pages) {
            doubleobjboxPage(
                obj1 = obj1,
                zoom1 = zoom1,
                obj2 = obj2,
                zoom2 = zoom2,
                text = page.text,
                pauseText = constants.cm_pausebutton,
                eventBus = context.eventBus,
            )
        }
    }

    private suspend fun doubleobjboxPage(
        obj1: InvObj,
        zoom1: Int,
        obj2: InvObj,
        zoom2: Int,
        text: String,
        pauseText: String,
        eventBus: EventBus,
    ) {
        player.ifDoubleobjbox(text, obj1.id, zoom1, obj2.id, zoom2, pauseText, eventBus)
        val modal = player.ui.getModalOrNull(components.chatbox_chatmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.objectbox_double_pbutton)
    }

    /**
     * Similar to [doubleobjbox], but does **not** suspend nor show a `Click here to continue` pause
     * button.
     *
     * This dialogue is meant to be used in a script sequence where the script itself will replace
     * or close the dialogue after a certain number of cycles.
     */
    public fun doubleobjboxNp(obj1: InvObj, obj2: InvObj, text: String) {
        doubleobjboxNp(obj1, zoom1 = 400, obj2, zoom2 = 400, text)
    }

    /**
     * Similar to [doubleobjbox], but does **not** suspend nor show a `Click here to continue` pause
     * button.
     *
     * This dialogue is meant to be used in a script sequence where the script itself will replace
     * or close the dialogue after a certain number of cycles.
     */
    public fun doubleobjboxNp(obj1: InvObj, zoom1: Int, obj2: InvObj, zoom2: Int, text: String) {
        val alignment = context.alignment
        val pages = alignment.generateChatPageList(text)
        if (pages.size > 1) {
            throw IllegalStateException("Text too long: $text")
        }
        val page = pages.first()
        player.ifDoubleobjbox(page.text, obj1.id, zoom1, obj2.id, zoom2, "", context.eventBus)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun <T> choice2(
        choice1: String,
        result1: T,
        choice2: String,
        result2: T,
        title: String = constants.cm_options,
    ): T {
        player.ifChoice(title, "$choice1|$choice2", choiceCountInclusive = 2, context.eventBus)
        val modal = player.ui.getModalOrNull(components.chatbox_chatmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.chatmenu_pbutton)
        return when (input.subcomponent) {
            1 -> result1
            2 -> result2
            else -> error("Invalid choice `${input.subcomponent}` for `$player`. (input=$input)")
        }
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun <T> choice3(
        choice1: String,
        result1: T,
        choice2: String,
        result2: T,
        choice3: String,
        result3: T,
        title: String = constants.cm_options,
    ): T {
        player.ifChoice(
            title,
            "$choice1|$choice2|$choice3",
            choiceCountInclusive = 3,
            context.eventBus,
        )
        val modal = player.ui.getModalOrNull(components.chatbox_chatmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.chatmenu_pbutton)
        return when (input.subcomponent) {
            1 -> result1
            2 -> result2
            3 -> result3
            else -> error("Invalid choice `${input.subcomponent}` for `$player`. (input=$input)")
        }
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun <T> choice4(
        choice1: String,
        result1: T,
        choice2: String,
        result2: T,
        choice3: String,
        result3: T,
        choice4: String,
        result4: T,
        title: String = constants.cm_options,
    ): T {
        player.ifChoice(
            title,
            "$choice1|$choice2|$choice3|$choice4",
            choiceCountInclusive = 4,
            context.eventBus,
        )
        val modal = player.ui.getModalOrNull(components.chatbox_chatmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.chatmenu_pbutton)
        return when (input.subcomponent) {
            1 -> result1
            2 -> result2
            3 -> result3
            4 -> result4
            else -> error("Invalid choice `${input.subcomponent}` for `$player`. (input=$input)")
        }
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun <T> choice5(
        choice1: String,
        result1: T,
        choice2: String,
        result2: T,
        choice3: String,
        result3: T,
        choice4: String,
        result4: T,
        choice5: String,
        result5: T,
        title: String = constants.cm_options,
    ): T {
        player.ifChoice(
            title,
            "$choice1|$choice2|$choice3|$choice4|$choice5",
            choiceCountInclusive = 5,
            context.eventBus,
        )
        val modal = player.ui.getModalOrNull(components.chatbox_chatmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.chatmenu_pbutton)
        return when (input.subcomponent) {
            1 -> result1
            2 -> result2
            3 -> result3
            4 -> result4
            5 -> result5
            else -> error("Invalid choice `${input.subcomponent}` for `$player`. (input=$input)")
        }
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun chatPlayerNoAnim(text: String) {
        chatPlayer(null, text)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun chatPlayer(
        mesanim: UnpackedMesAnimType?,
        text: String,
        title: String = player.displayName,
    ) {
        val alignment = context.alignment
        val pages = alignment.generateChatPageList(text)
        for (page in pages) {
            val (pgText, lineCount) = page
            val lineHeight = alignment.chatLineHeight(lineCount)
            val chatanim = mesanim?.splitGetAnim(lineCount)
            chatPlayerPage(
                text = pgText,
                chatanim = chatanim,
                lineHeight = lineHeight,
                title = title,
                pauseText = constants.cm_pausebutton,
                eventBus = context.eventBus,
            )
        }
    }

    private suspend fun chatPlayerPage(
        text: String,
        chatanim: SeqType?,
        lineHeight: Int,
        title: String,
        pauseText: String,
        eventBus: EventBus,
    ) {
        player.ifChatPlayer(title, text, chatanim, pauseText, lineHeight, eventBus)
        val modal = player.ui.getModalOrNull(components.chatbox_chatmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.chat_right_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun chatNpcNoAnim(
        npc: Npc,
        text: String,
        title: String = npc.resolveVisName(),
        faceFar: Boolean = false,
    ) {
        chatNpc(npc, mesanim = null, text = text, title = title, faceFar = faceFar)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun chatNpc(
        npc: Npc,
        mesanim: UnpackedMesAnimType?,
        text: String,
        title: String = npc.resolveVisName(),
        faceFar: Boolean = false,
    ) {
        val alignment = context.alignment
        val pages = alignment.generateChatPageList(text)
        for (page in pages) {
            val (pgText, lineCount) = page
            val lineHeight = alignment.chatLineHeight(lineCount)
            val chatanim = mesanim?.splitGetAnim(lineCount)
            chatNpcPage(
                title = title,
                npc = npc,
                text = pgText,
                chatanim = chatanim,
                lineHeight = lineHeight,
                faceFar = faceFar,
                pauseText = constants.cm_pausebutton,
                eventBus = context.eventBus,
            )
        }
    }

    private suspend fun chatNpcPage(
        title: String,
        npc: Npc,
        text: String,
        chatanim: SeqType?,
        lineHeight: Int,
        faceFar: Boolean,
        pauseText: String,
        eventBus: EventBus,
    ) {
        val inCombatMode = npc.mode == NpcMode.OpPlayer2 || npc.mode == NpcMode.ApPlayer2
        if (!inCombatMode) {
            npc.playerFace(player, faceFar = faceFar)
        }
        player.facePathingEntitySquare(npc)
        player.ifChatNpcSpecific(title, npc.type, text, chatanim, pauseText, lineHeight, eventBus)
        val modal = player.ui.getModalOrNull(components.chatbox_chatmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.chat_left_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun chatNpcNoTurn(
        npc: Npc,
        mesanim: UnpackedMesAnimType?,
        text: String,
        title: String = npc.resolveVisName(),
    ) {
        val alignment = context.alignment
        val pages = alignment.generateChatPageList(text)
        for (page in pages) {
            val (pgText, lineCount) = page
            val lineHeight = alignment.chatLineHeight(lineCount)
            val chatanim = mesanim?.splitGetAnim(lineCount)
            chatNpcNoTurnPage(
                title = title,
                npc = npc,
                text = pgText,
                chatanim = chatanim,
                lineHeight = lineHeight,
                pauseText = constants.cm_pausebutton,
                eventBus = context.eventBus,
            )
        }
    }

    private suspend fun chatNpcNoTurnPage(
        title: String,
        npc: Npc,
        text: String,
        chatanim: SeqType?,
        lineHeight: Int,
        pauseText: String,
        eventBus: EventBus,
    ) {
        player.facePathingEntitySquare(npc)
        player.ifChatNpcSpecific(title, npc.type, text, chatanim, pauseText, lineHeight, eventBus)
        val modal = player.ui.getModalOrNull(components.chatbox_chatmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.chat_left_pbutton)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun chatNpcSpecific(
        title: String,
        type: NpcType,
        mesanim: UnpackedMesAnimType,
        text: String,
    ) {
        val alignment = context.alignment
        val pages = alignment.generateChatPageList(text)
        for (page in pages) {
            val (pgText, lineCount) = page
            val lineHeight = alignment.chatLineHeight(lineCount)
            val chatanim = mesanim.splitGetAnim(lineCount)
            chatNpcSpecificPage(
                title = title,
                type = type,
                text = pgText,
                chatanim = chatanim,
                lineHeight = lineHeight,
                pauseText = constants.cm_pausebutton,
                eventBus = context.eventBus,
            )
        }
    }

    private suspend fun chatNpcSpecificPage(
        title: String,
        type: NpcType,
        text: String,
        chatanim: SeqType,
        lineHeight: Int,
        pauseText: String,
        eventBus: EventBus,
    ) {
        player.ifChatNpcSpecific(title, type, text, chatanim, pauseText, lineHeight, eventBus)
        val modal = player.ui.getModalOrNull(components.chatbox_chatmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.chat_left_pbutton)
    }

    /**
     * Similar to [chatNpcSpecific], but does **not** suspend nor show a `Click here to continue`
     * pause button.
     *
     * This dialogue is meant to be used in a script sequence where the script itself will replace
     * or close the dialogue after a certain number of cycles.
     */
    public fun chatNpcSpecificNp(
        title: String,
        type: NpcType,
        mesanim: UnpackedMesAnimType,
        text: String,
    ) {
        val alignment = context.alignment
        val pages = alignment.generateChatPageList(text)
        if (pages.size > 1) {
            throw IllegalStateException("Text too long: $text")
        }
        val (pgText, lineCount) = pages.first()
        val lineHeight = alignment.chatLineHeight(lineCount)
        val chatanim = mesanim.splitGetAnim(lineCount)
        player.ifChatNpcSpecific(title, type, pgText, chatanim, "", lineHeight, context.eventBus)
    }

    /**
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun stringDialog(title: String): String {
        mesLayerMode9(player, title)
        val modal = player.ui.getModalOrNull(components.mainmodal)
        val input = coroutine.pause(ResumePStringDialogInput::class)
        return resumeWithMainModalProtectedAccess(input.text, modal)
    }

    /**
     * **Note:** The returned integer will _always_ be positive. To allow negative values, use
     * [numberDialog] instead.
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun countDialog(title: String = constants.cm_count): Int {
        mesLayerMode7(player, title)
        val modal = player.ui.getModalOrNull(components.mainmodal)
        val input = coroutine.pause(ResumePCountDialogInput::class)
        return resumeWithMainModalProtectedAccess(input.count.absoluteValue, modal)
    }

    /**
     * A version of [countDialog] that allows the returned value to be negative.
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun numberDialog(title: String): Int {
        mesLayerMode7(player, title)
        val modal = player.ui.getModalOrNull(components.mainmodal)
        val input = coroutine.pause(ResumePCountDialogInput::class)
        return resumeWithMainModalProtectedAccess(input.count, modal)
    }

    /**
     * @param stockMarketRestriction If `true` the search will be restricted to only objs that can
     *   be found in the grand exchange.
     * @param enumRestriction If an enum (with key of `ObjType` and value of `Boolean`) is provided,
     *   the search is restricted to only the entries in said enum.
     * @param showLastSearched If `true` the search will present the last selected obj.
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun objDialog(
        title: String = constants.cm_obj,
        stockMarketRestriction: Boolean = true,
        enumRestriction: EnumType<ObjType, Boolean>? = null,
        showLastSearched: Boolean = false,
    ): UnpackedObjType {
        mesLayerMode14(player, title, stockMarketRestriction, enumRestriction, showLastSearched)
        val modal = player.ui.getModalOrNull(components.mainmodal)
        val input = coroutine.pause(ResumePObjDialogInput::class)
        return resumeWithMainModalProtectedAccess(input.obj, modal)
    }

    /**
     * Returns `true` if the player selects the "Yes" confirmation to destroy the specified obj.
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumePauseButtonWithProtectedAccess]
     */
    public suspend fun confirmDestroy(
        obj: ObjType,
        count: Int,
        header: String,
        text: String,
    ): Boolean {
        player.ifConfirmDestroy(header, text, obj.id, count, context.eventBus)
        val modal = player.ui.getModalOrNull(components.chatbox_chatmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        resumePauseButtonWithProtectedAccess(input, modal, components.confirmdestroy_pbutton)
        return when (input.subcomponent) {
            0 -> false
            1 -> true
            else -> error("Invalid choice `${input.subcomponent}` for `$player`. (input=$input)")
        }
    }

    /**
     * Returns `true` if the player selects the [confirm] option.
     *
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun confirmOverlay(
        target: ComponentType,
        title: String,
        text: String,
        cancel: String,
        confirm: String,
    ): Boolean {
        player.ifConfirmOverlay(target, title, text, cancel, confirm, context.eventBus)
        val modal = player.ui.getModalOrNull(components.mainmodal)
        val input = coroutine.pause(ResumePCountDialogInput::class)
        val confirmed = resumeWithMainModalProtectedAccess(input.count != 0, modal)
        player.ifConfirmOverlayClose(context.eventBus)
        return confirmed
    }

    /**
     * Opens a list selection modal, suspending until the [player] selects one of the [choices].
     *
     * @param hotkeys If `true` the menu interface will allow number keys to be used to select a
     *   listed choice.
     * @return The selected choice subcomponent id ranging from `0` to `127`.
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @throws IllegalArgumentException if [choices] has more than `127` elements.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun menu(title: String, hotkeys: Boolean, choices: List<String>): Int {
        require(choices.size < 128) { "Can only have up to 127 `choices`. (size=${choices.size})" }
        player.ifMenu(title, choices.joinToString("|"), hotkeys, context.eventBus)
        val modal = player.ui.getModalOrNull(components.mainmodal)
        val input = coroutine.pause(ResumePauseButtonInput::class)
        chatDefaultRestoreInput(player)
        return resumeWithMainModalProtectedAccess(input.subcomponent.absoluteValue, modal)
    }

    /**
     * Opens a list selection modal, suspending until the [player] selects one of the [choices].
     *
     * @param hotkeys If `true` the menu interface will allow number keys to be used to select a
     *   listed choice.
     * @return The selected choice subcomponent id ranging from `0` to `127`.
     * @throws ProtectedAccessLostException if the player could not retain protected access after
     *   the coroutine suspension.
     * @throws IllegalArgumentException if [choices] has more than `127` elements.
     * @see [resumeWithMainModalProtectedAccess]
     */
    public suspend fun menu(title: String, vararg choices: String, hotkeys: Boolean = false): Int {
        return menu(title, hotkeys, choices.toList())
    }

    /**
     * Ensures we can still obtain protected access for [player]. If protected access cannot be
     * regained, this function throws a [ProtectedAccessLostException], which will cause the current
     * `withProtectedAccess` lambda block to exit gracefully.
     *
     * The thrown exception will be suppressed by [Player.advanceActiveCoroutine] and/or
     * [Player.resumeActiveCoroutine], so it will not interrupt the server or the player. While
     * losing protected access is handled gracefully, it is something callers should be aware of
     * when using a suspend function in this [ProtectedAccess] scope.
     *
     * For example:
     * ```
     * player.withProtectedAccess {
     *  val input = countDialog()
     *  // While this lambda is suspended waiting for countDialog input, something has added a
     *  // delay to `player.`
     *
     *  // Input was somehow passed along and returns the number 5 to the suspending `countDialog`
     *  // call. However, the player is "delayed" and thus `Player.isAccessProtected` will return
     *  // true, causing `regainProtectedAccess` to throw `ProtectedAccessLostException`, and this
     *  // lambda to exit gracefully.
     *
     *  // This message will never be sent to the player
     *  player.mes("Your input is: $input.")
     *  // Nor will anything below this...
     *  player.invAdd(objs.jug_empty, input)
     * }
     * ```
     *
     * @throws ProtectedAccessLostException
     * @see [Player.isAccessProtected]
     */
    @Throws(ProtectedAccessLostException::class)
    private fun regainProtectedAccess() {
        if (player.isAccessProtected) {
            logger.debug { "Protected-access could not be re-obtained for player: $player" }
            throw ProtectedAccessLostException()
        }
    }

    /**
     * Helper function to attempt and resume a call-site from a `ResumePauseButtonInput` suspension
     * while ensuring that the [ResumePauseButtonInput.component] is associated with the
     * [expectedComponent] and that the same [expectedModal] is still opened after the suspension.
     *
     * @param expectedComponent the [ComponentType] that had its [IfEvent.PauseButton] bitmask
     *   enabled and what is expected of the player to click in order to "continue."
     * @param expectedModal the [Component] that is expected to be the player's active modal.
     * @throws ProtectedAccessLostException if the player is `delayed`, their active coroutine does
     *   not match this scope's [coroutine], the current modal does not match [expectedModal], or if
     *   [expectedComponent] `isType` returns `false` for [ResumePauseButtonInput.component].
     * @see [resumeWithModalProtectedAccess]
     */
    private fun resumePauseButtonWithProtectedAccess(
        input: ResumePauseButtonInput,
        expectedModal: Component?,
        expectedComponent: ComponentType,
    ) {
        if (!expectedComponent.isType(input.component)) {
            logger.debug {
                "Protected-access was lost due to unexpected component: " +
                    "player=$player, " +
                    "received=${input.component.internalName ?: input}, " +
                    "expected=${expectedComponent.internalName ?: expectedComponent}"
            }
            throw ProtectedAccessLostException()
        }
        resumeWithModalProtectedAccess(null, expectedModal, components.chatbox_chatmodal)
    }

    /**
     * Helper function to attempt and resume a call-site from a suspension point while ensuring that
     * the [expectedModal] remained open.
     *
     * @param returnWithProtectedAccess the value to be returned after verifying protected access
     *   conditions are met.
     * @param expectedModal the [Component] that is expected to be the player's active modal.
     * @throws ProtectedAccessLostException if the player is `delayed`, their active coroutine does
     *   not match this scope's [coroutine], or if the current modal does not match [expectedModal].
     */
    private fun <T> resumeWithModalProtectedAccess(
        returnWithProtectedAccess: T,
        expectedModal: Component?,
        modalTarget: ComponentType,
    ): T {
        if (player.isDelayed) {
            logger.debug { "Protected-access was lost due to delay: player=$player" }
            throw ProtectedAccessLostException()
        }

        if (player.activeCoroutine !== coroutine) {
            logger.debug {
                "Protected-access was lost due to coroutine mismatch: " +
                    "player=$player, scopeCoroutine=$coroutine"
            }
            throw ProtectedAccessLostException()
        }

        val currentModal = player.ui.getModalOrNull(modalTarget)
        if (currentModal != expectedModal) {
            logger.debug {
                "Protected-access was lost due to unexpected modal: " +
                    "player=$player, " +
                    "received=$currentModal, " +
                    "expected=$expectedModal"
            }
            throw ProtectedAccessLostException()
        }

        return returnWithProtectedAccess
    }

    /**
     * Helper function that calls [resumeWithModalProtectedAccess] with the `modalTarget` set to
     * `components.main_modal`.
     *
     * @see [resumeWithModalProtectedAccess]
     */
    private fun <T> resumeWithMainModalProtectedAccess(
        returnWithProtectedAccess: T,
        expectedModal: Component?,
    ): T {
        return resumeWithModalProtectedAccess(
            returnWithProtectedAccess,
            expectedModal,
            components.mainmodal,
        )
    }

    /* Client script helper functions */
    public fun runClientScript(id: Int, vararg args: Any): Unit = player.runClientScript(id, *args)

    public fun camForceAngle(rate: Int, rate2: Int): Unit =
        ClientScripts.camForceAngle(player, rate, rate2)

    public fun interfaceInvInit(
        inv: Inventory,
        target: ComponentType,
        objRowCount: Int,
        objColCount: Int,
        dragType: Int = 0,
        dragComponent: ComponentType? = null,
        op1: String? = null,
        op2: String? = null,
        op3: String? = null,
        op4: String? = null,
        op5: String? = null,
    ): Unit =
        ClientScripts.interfaceInvInit(
            player = player,
            inv = inv,
            target = target,
            objRowCount = objRowCount,
            objColCount = objColCount,
            dragType = dragType,
            dragComponent = dragComponent,
            op1 = op1,
            op2 = op2,
            op3 = op3,
            op4 = op4,
            op5 = op5,
        )

    public fun toplevelSidebuttonSwitch(side: Int) {
        ClientScripts.toplevelSidebuttonSwitch(player, side)
    }

    /* Cam helper functions */
    public fun camLookAt(dest: CoordGrid, height: Int, rate: Int, rate2: Int) {
        Camera.camLookAt(player, dest, height, rate, rate2)
    }

    public fun camMoveTo(dest: CoordGrid, height: Int, rate: Int, rate2: Int) {
        Camera.camMoveTo(player, dest, height, rate, rate2)
    }

    public fun camReset() {
        Camera.camReset(player)
    }

    /* Cinematic helper functions */
    public fun camModeClose() {
        Cinematic.setCameraMode(player, CameraMode.Close)
    }

    public fun camModeFar() {
        Cinematic.setCameraMode(player, CameraMode.Far)
    }

    public fun camModeFixed() {
        Cinematic.setCameraMode(player, CameraMode.Fixed)
    }

    public fun camModeReset() {
        Cinematic.setCameraMode(player, CameraMode.Normal)
    }

    public fun minimapHideFull() {
        Cinematic.setMinimapState(player, MinimapState.Disabled)
    }

    public fun minimapNoOps() {
        Cinematic.setMinimapState(player, MinimapState.MinimapNoOp)
    }

    public fun minimapHideMap() {
        Cinematic.setMinimapState(player, MinimapState.MinimapHidden)
    }

    public fun minimapHideCompass() {
        Cinematic.setMinimapState(player, MinimapState.CompassHidden)
    }

    public fun minimapNoOpsHideCompass() {
        Cinematic.setMinimapState(player, MinimapState.MinimapNoOpCompassHidden)
    }

    public fun minimapReset() {
        Cinematic.setMinimapState(player, MinimapState.Normal)
    }

    public fun hideTopLevel() {
        Cinematic.setHideToplevel(player, hide = true)
    }

    public fun showTopLevel() {
        Cinematic.setHideToplevel(player, hide = false)
    }

    public fun clearHealthHud() {
        Cinematic.clearHealthHud(player)
    }

    public fun hideHealthHud() {
        Cinematic.setHideHealthHud(player, hide = true)
    }

    public fun showHealthHud() {
        Cinematic.setHideHealthHud(player, hide = false)
    }

    public fun tempDisableAcceptAid() {
        Cinematic.disableAcceptAid(player)
    }

    public fun restoreLastAcceptAid() {
        Cinematic.restoreAcceptAid(player)
    }

    public fun hideEntityOps() {
        Cinematic.setHideEntityOps(player, hide = true)
    }

    public fun showEntityOps() {
        Cinematic.setHideEntityOps(player, hide = false)
    }

    public fun closeTopLevelTabs() {
        Cinematic.closeToplevelTabs(player, context.eventBus)
    }

    public fun closeTopLevelTabsLenient() {
        Cinematic.closeToplevelTabsLenient(player, context.eventBus)
    }

    public fun openTopLevelTabs() {
        Cinematic.openTopLevelTabs(player, context.eventBus)
    }

    public fun fadeOverlay(
        startColour: Int,
        startTransparency: Int,
        endColour: Int,
        endTransparency: Int,
        clientDuration: Int,
    ) {
        Cinematic.fadeOverlay(
            player,
            startColour,
            startTransparency,
            endColour,
            endTransparency,
            clientDuration,
            context.eventBus,
        )
    }

    public fun closeFadeOverlay(cycles: Int = 3) {
        longQueueDiscard(queues.fade_overlay_close, cycles)
    }

    /* Interface helper functions */
    public fun ifClose() {
        player.ifClose(context.eventBus)
    }

    public fun ifCloseSub(interf: InterfaceType) {
        player.ifCloseSub(interf, context.eventBus)
    }

    public fun ifOpenSide(interf: InterfaceType) {
        player.ifOpenSide(interf, context.eventBus)
    }

    /**
     * Difference with [ifOpenMainModal] is that this function will **not** send
     * `toplevel_mainmodal_open` (script 2524) before opening the interface.
     */
    public fun ifOpenMain(interf: InterfaceType) {
        player.ifOpenMain(interf, context.eventBus)
    }

    public fun ifOpenMainSidePair(
        main: InterfaceType,
        side: InterfaceType,
        colour: Int = -1,
        transparency: Int = -1,
    ) {
        player.ifOpenMainSidePair(main, side, colour, transparency, context.eventBus)
    }

    /**
     * Difference with [ifOpenMain] is that this function will send `toplevel_mainmodal_open`
     * (script 2524) before opening the interface in the main modal position.
     */
    public fun ifOpenMainModal(interf: InterfaceType, colour: Int = -1, transparency: Int = -1) {
        player.ifOpenMainModal(interf, context.eventBus, colour, transparency)
    }

    public fun ifOpenOverlay(interf: InterfaceType, target: ComponentType) {
        player.ifOpenOverlay(interf, target, context.eventBus)
    }

    public fun ifOpenOverlay(interf: InterfaceType) {
        player.ifOpenOverlay(interf, context.eventBus)
    }

    public fun ifOpenFullOverlay(interf: InterfaceType) {
        player.ifOpenFullOverlay(interf, context.eventBus)
    }

    public fun ifOpenSub(interf: InterfaceType, target: ComponentType, type: IfSubType) {
        player.ifOpenSub(interf, target, type, context.eventBus)
    }

    public fun ifSetAnim(target: ComponentType, seq: SeqType?) {
        player.ifSetAnim(target, seq)
    }

    public fun ifSetEvents(target: ComponentType, range: IntRange, vararg event: IfEvent) {
        player.ifSetEvents(target, range, *event)
    }

    public fun ifSetNpcHead(target: ComponentType, npc: NpcType) {
        player.ifSetNpcHead(target, npc)
    }

    public fun ifSetPlayerHead(target: ComponentType) {
        player.ifSetPlayerHead(target)
    }

    public fun ifSetText(target: ComponentType, text: String) {
        player.ifSetText(target, text)
    }

    public fun ifSetObj(target: ComponentType, obj: ObjType, zoom: Int) {
        player.ifSetObj(target, obj, zoom)
    }

    public fun ifSetHide(target: ComponentType, hide: Boolean) {
        player.ifSetHide(target, hide)
    }

    /* Inventory helper functions */
    public fun invTakeFee(fee: Int, inv: Inventory = this.inv): Boolean {
        return player.invTakeFee(fee, inv)
    }

    public fun invCoinTotal(inv: Inventory = this.inv): Int {
        return invTotal(inv, objs.coins)
    }

    public fun invTotal(inv: Inventory, content: ContentGroupType): Int {
        val types = context.objTypes
        var count = 0
        for (obj in inv) {
            val filtered = obj ?: continue
            val type = types[filtered]
            if (type.isContentType(content)) {
                count += filtered.count
            }
        }
        return count
    }

    public fun invTotal(inv: Inventory, obj: ObjType): Int {
        return inv.count(context.objTypes[obj])
    }

    public fun invContains(inv: Inventory, content: ContentGroupType): Boolean {
        val types = context.objTypes
        return inv.any { it != null && types[it].contentGroup == content.id }
    }

    public fun inv(inv: InvType): Inventory {
        val type = context.invTypes[inv]
        return player.invMap.getOrPut(type)
    }

    public operator fun Inventory.contains(content: ContentGroupType): Boolean {
        return invContains(this, content)
    }

    /* Loc helper functions (lc=loc config) */
    public fun <T : Any> lcParam(type: LocType, param: ParamType<T>): T {
        return context.locTypes[type].param(param)
    }

    public fun <T : Any> lcParamOrNull(type: LocType, param: ParamType<T>): T? {
        return context.locTypes[type].paramOrNull(param)
    }

    public fun <T : Any> locParam(loc: LocInfo, param: ParamType<T>): T {
        return context.locTypes[loc].param(param)
    }

    public fun <T : Any> locParamOrNull(loc: LocInfo, param: ParamType<T>): T? {
        return context.locTypes[loc].paramOrNull(param)
    }

    public fun <T : Any> locParam(loc: BoundLocInfo, param: ParamType<T>): T {
        return context.locTypes[loc].param(param)
    }

    public fun <T : Any> locParamOrNull(loc: BoundLocInfo, param: ParamType<T>): T? {
        return context.locTypes[loc].paramOrNull(param)
    }

    public fun locAnim(repo: WorldRepository, loc: LocInfo, seq: SeqType) {
        repo.locAnim(loc, seq)
    }

    public fun locAnim(repo: WorldRepository, loc: BoundLocInfo, seq: SeqType) {
        repo.locAnim(loc, seq)
    }

    /* Map helper functions */
    public fun spotanimMap(
        repo: WorldRepository,
        spotanim: SpotanimType,
        coord: CoordGrid,
        height: Int = 0,
        delay: Int = 0,
    ) {
        repo.spotanimMap(spotanim, coord, height, delay)
    }

    /* Message game helper functions */
    public fun mes(text: String) {
        player.mes(text, ChatType.GameMessage)
    }

    public fun mes(text: String, type: ChatType) {
        player.mes(text, type)
    }

    public fun spam(text: String) {
        player.spam(text)
    }

    /* Midi helper functions */
    public fun midiJingle(jingle: JingleType) {
        player.midiJingle(jingle)
    }

    public fun midiSong(midi: MidiType) {
        player.midiSong(midi)
    }

    /* Npc helper functions (nc=npc config) */
    public fun <T : Any> ncParam(type: NpcType, param: ParamType<T>): T {
        return context.npcTypes[type].param(param)
    }

    public fun <T : Any> ncParamOrNull(type: NpcType, param: ParamType<T>): T? {
        return context.npcTypes[type].paramOrNull(param)
    }

    public fun npcPlayerFaceClose(npc: Npc, target: Player = this.player) {
        npc.playerFaceClose(target)
    }

    public fun npcPlayerFace(npc: Npc, target: Player = this.player) {
        npc.playerFace(target)
    }

    public fun npcPlayerEscape(npc: Npc, target: Player = this.player) {
        npc.playerEscape(target)
    }

    public fun npcResetMode(npc: Npc) {
        npc.resetMode()
    }

    /**
     * Transmogrifies the [npc] into [into], reassigning its internal `uid`. Since npc interactions
     * validate `uid`s before processing, this will automatically cancel any ongoing interactions
     * with this [npc].
     *
     * @param duration The cycle duration that the [npc] will remain as [into] before automatically
     *   changing back to its original type. Set to `Int.MAX_VALUE` to bypass this behavior.
     */
    @OptIn(InternalApi::class)
    public fun npcChangeType(npc: Npc, into: NpcType, duration: Int) {
        val type = context.npcTypes[into]
        npc.transmog(type, duration)
        npc.assignUid()
    }

    /**
     * Returns the current npc type for [npc], considering `multinpc` from the [player]'s vars and
     * any transmogrification the npc may have undergone.
     */
    public fun npcVisType(npc: Npc): UnpackedNpcType {
        val multiNpc = context.npcInteractions.multiNpc(npc.visType, player.vars)
        return multiNpc ?: npc.visType
    }

    /**
     * Retrieves the [param] value for the base [npc].
     *
     * _Note: This retrieves the parameter from the npc's **base** type, ignoring any `multinpc` or
     * transmogrification effects._
     *
     * @throws IllegalStateException if npc type does not have an associated value for [param] and
     *   [param] does not have a [ParamType.default] value.
     */
    public fun <T : Any> npcParam(npc: Npc, param: ParamType<T>): T {
        return npc.type.param(param)
    }

    /**
     * Retrieves the [param] value for the base [npc], or returns `null` if the npc's type lacks an
     * associated value for [param] and [param] does not have a [ParamType.default] value.
     *
     * _Note: This retrieves the parameter from the npc's **base** type, ignoring any `multinpc` or
     * transmogrification effects._
     */
    public fun <T : Any> npcParamOrNull(npc: Npc, param: ParamType<T>): T? {
        return npc.type.paramOrNull(param)
    }

    /* Obj helper functions (oc=obj config) */
    public fun ocCert(type: ObjType): UnpackedObjType {
        val types = context.objTypes
        return types.cert(types[type])
    }

    public fun ocUncert(type: ObjType): UnpackedObjType {
        val types = context.objTypes
        return types.uncert(types[type])
    }

    public fun ocName(type: ObjType): String {
        return context.objTypes[type].name
    }

    public fun <T : Any> ocParam(obj: InvObj, type: ParamType<T>): T {
        return context.objTypes[obj].param(type)
    }

    public fun <T : Any> ocParamOrNull(obj: InvObj?, type: ParamType<T>): T? {
        return if (obj == null) null else context.objTypes[obj].paramOrNull(type)
    }

    public fun ocIsContentType(obj: InvObj?, content: ContentGroupType): Boolean {
        return obj != null && context.objTypes[obj].contentGroup == content.id
    }

    public fun ocIsType(obj: InvObj?, type: ObjType): Boolean {
        return obj.isType(type)
    }

    public fun ocIsType(obj: InvObj?, type: ObjType, vararg others: ObjType): Boolean {
        return obj.isType(type) || others.any(obj::isType)
    }

    public fun ocTradable(obj: InvObj): Boolean {
        return context.objTypes[obj].tradeable
    }

    public fun ocCategory(type: UnpackedObjType?, catTypes: CategoryTypeList): CategoryType? {
        return if (type == null) null else catTypes[type.category]
    }

    // TODO: Decide if we either want to keep this and make it public; or remove it and refactor
    //  its usage (only called in one place as of now due to laziness).
    internal fun ocType(obj: InvObj?): UnpackedObjType? {
        return if (obj == null) null else context.objTypes[obj]
    }

    /* Seq helper functions */
    /** Returns the total time duration of [seq] in _**client frames**_. */
    public fun seqLength(seq: SeqType): Int {
        return context.seqTypes[seq].totalDelay
    }

    /** Returns the total time duration of [seq] in _**server ticks**_. */
    public fun seqTicks(seq: SeqType): Int {
        return context.seqTypes[seq].tickDuration
    }

    /* Sound helper functions */
    public fun soundSynth(synth: SynthType, loops: Int = 1, delay: Int = 0) {
        player.soundSynth(synth, loops, delay)
    }

    public fun soundArea(
        repo: WorldRepository,
        source: CoordGrid,
        synth: SynthType,
        delay: Int = 0,
        loops: Int = 1,
        radius: Int = 5,
        size: Int = 0,
    ) {
        repo.soundArea(source, synth, delay, loops, radius, size)
    }

    public fun soundArea(
        repo: WorldRepository,
        source: PathingEntity,
        synth: SynthType,
        delay: Int = 0,
        loops: Int = 1,
        radius: Int = 5,
    ) {
        repo.soundArea(source, synth, delay, loops, radius)
    }

    /**
     * Increments [opHeldCallCount] counter and throws [IllegalStateException] if the call count
     * exceeds a certain threshold. This is done as a counter-measure to avoid [StackOverflowError]
     * under a specific scenario of `opHeld` calls.
     *
     * This occurs when an `opHeldN` function is called from an `onOpHeldN` script that comes from
     * the same inv slot obj. Assuming there are no `delay` or similar suspending calls in between
     * each script, this leads to infinite recursion:
     * ```
     * onOpHeldN -> opHeldN -> onOpHeldN -> opHeldN -> ...
     * ```
     *
     * @throws IllegalStateException
     */
    private fun checkOpHeldCallLimit() {
        if (opHeldCallCount++ >= 25) {
            throw IllegalStateException("Detected `opHeld` infinite recursion: $this")
        }
    }

    private fun Npc.resolveVisName(): String {
        if (!type.isMultiNpc) {
            return type.name
        }
        val visType = npcVisType(this)
        return visType.name
    }

    // The player should not be able to trigger a manual logout (e.g., by clicking the logout
    // button) if the server has already initiated a logout through other means.
    private fun assertLogoutState() {
        check(!player.loggingOut) { "Player already logging out." }
        check(!player.pendingLogout) { "Player already marked for pending logout." }
        check(!player.clientDisconnected.get()) { "Client already marked as disconnected." }
        check(!player.forceDisconnect) { "Player already marked for forced disconnection." }
    }

    @OptIn(InternalApi::class)
    private fun canLogout(): Boolean {
        return !player.isPendingLogout() && !player.pendingLogout && !player.loggingOut
    }

    override fun toString(): String {
        return "ProtectedAccess(player=$player, coroutine=$coroutine)"
    }
}

private fun <T> lazy(init: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, init)

private fun UnpackedMesAnimType.splitGetAnim(lines: Int) =
    when (lines) {
        1 -> len1
        2 -> len2
        3 -> len3
        else -> len4
    }

/**
 * This function should only be called directly under specific circumstances. Prefer calling
 * [org.rsmod.api.player.protect.ProtectedAccess.clearPendingAction] instead.
 */
public fun Player.clearPendingAction(eventBus: EventBus) {
    ifClose(eventBus)
    cancelActiveCoroutine()
    clearInteraction()
}

package org.rsmod.game.entity

import org.rsmod.annotations.InternalApi
import org.rsmod.game.entity.npc.NpcInfoProtocol
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.entity.npc.NpcUid
import org.rsmod.game.entity.npc.OpVisibility
import org.rsmod.game.entity.player.PlayerUid
import org.rsmod.game.entity.util.EntityFaceTarget
import org.rsmod.game.entity.util.PathingEntityCommon
import org.rsmod.game.headbar.Headbar
import org.rsmod.game.hero.HeroPoints
import org.rsmod.game.hit.Hitmark
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.map.Direction
import org.rsmod.game.movement.BlockWalk
import org.rsmod.game.movement.MoveRestrict
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.obj.Obj
import org.rsmod.game.queue.AiQueueType
import org.rsmod.game.queue.NpcQueueList
import org.rsmod.game.seq.EntitySeq
import org.rsmod.game.timer.NpcTimerMap
import org.rsmod.game.type.content.ContentGroupType
import org.rsmod.game.type.headbar.HeadbarType
import org.rsmod.game.type.hitmark.HitmarkTypeGroup
import org.rsmod.game.type.hunt.HuntModeType
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.timer.TimerType
import org.rsmod.game.vars.VarNpcIntMap
import org.rsmod.game.vars.VarNpcStrMap
import org.rsmod.map.CoordGrid
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.collision.CollisionStrategy

public class Npc(
    public val type: UnpackedNpcType,
    override val avatar: NpcAvatar = NpcAvatar(type.size),
) : PathingEntity() {
    public constructor(type: UnpackedNpcType, coords: CoordGrid) : this(type) {
        this.coords = coords
        this.spawnCoords = coords
    }

    override val blockWalkCollisionFlag: Int?
        get() = blockWalk.collisionFlag

    override val collisionStrategy: CollisionStrategy?
        get() = moveRestrict.collisionStrategy

    override val heroPoints: HeroPoints = HeroPoints(type.heroCount)

    public val vars: VarNpcIntMap = VarNpcIntMap()
    public val strVars: VarNpcStrMap = VarNpcStrMap()

    public val timerMap: NpcTimerMap = NpcTimerMap()
    public val queueList: NpcQueueList = NpcQueueList()

    public var pendingAiQueue: AiQueueType? = null
        private set

    public var pendingAiQueueCycle: Int = 0

    public var uid: NpcUid = NpcUid.NULL
        private set

    public var spawnCoords: CoordGrid = coords
    public var defaultMoveSpeed: MoveSpeed = MoveSpeed.Walk
    public var respawnDir: Direction = type.respawnDir
    public var respawns: Boolean = false

    public var mode: NpcMode? = type.defaultMode

    public var aiTimerStart: Int = type.timer
    public var aiTimer: Int = type.timer

    public var lifecycleAddCycle: Int = -1
    public var lifecycleDelCycle: Int = -1
    public var lifecycleRevealCycle: Int = -1
    public var lifecycleRespawnCycle: Int = -1
    public var lifecycleChangeCycle: Int = -1
    public var lifecycleDelayedAddCycle: Int = -1
    public var lifecycleDelayedAddDuration: Int = -1

    public var attackLvl: Int = type.attack
    public var strengthLvl: Int = type.strength
    public var defenceLvl: Int = type.defence
    public var hitpoints: Int = type.hitpoints
    public var rangedLvl: Int = type.ranged
    public var magicLvl: Int = type.magic

    public var baseAttackLvl: Int = type.attack
    public var baseStrengthLvl: Int = type.strength
    public var baseDefenceLvl: Int = type.defence
    public var baseHitpointsLvl: Int = type.hitpoints
    public var baseRangedLvl: Int = type.ranged
    public var baseMagicLvl: Int = type.magic

    public var regenClock: Int = 0
    public val regenRate: Int = type.regenRate

    public var huntClock: Int = 0
    // `Obj` and `LocInfo` are lightweight, otherwise we would wrap them in `WeakReference`.
    public var huntObj: Obj? = null
    public var huntLoc: LocInfo? = null
    public var huntNpc: NpcUid = NpcUid.NULL
    public var huntPlayer: PlayerUid = PlayerUid.NULL
    public var huntRange: Int = type.huntRange
        private set

    public var huntMode: Int? = type.huntMode
        private set

    /**
     * The combat xp multiplier stored as an integer, with the decimal value scaled by `1000`. For
     * example, a value of `1075` represents a `1.075x` multiplier (`+7.5%`).
     */
    public var combatXpMultiplier: Int = 0

    public var patrolWaypointIndex: Int = 0
    public var patrolIdleCycles: Int = -1
    public var patrolPauseCycles: Int = 0

    public var wanderIdleCycles: Int = -1

    public var actionDelay: Int = -1

    public var transmog: UnpackedNpcType? = null
        private set

    public var cachedHitmark: HitmarkTypeGroup? = null

    public val isDelayed: Boolean
        get() = delay > processedMapClock

    public val isNotDelayed: Boolean
        get() = !isDelayed

    public val isBusy: Boolean
        get() = isDelayed

    public val canProcess: Boolean
        get() = isNotDelayed && isVisible

    public val id: Int
        get() = type.id

    public val name: String
        get() = type.name

    public val moveRestrict: MoveRestrict
        get() = type.moveRestrict

    public val blockWalk: BlockWalk
        get() = type.blockWalk

    public val wanderRange: Int
        get() = type.wanderRange

    public val defaultMode: NpcMode
        get() = type.defaultMode

    public val visType: UnpackedNpcType
        get() = transmog ?: type

    public var infoProtocol: NpcInfoProtocol
        get() = avatar.infoProtocol
        set(value) {
            avatar.infoProtocol = value
        }

    @InternalApi
    public fun assignUid() {
        check(slotId != INVALID_SLOT) { "`slotId` must be set before assigning a uid." }
        this.uid = NpcUid(slotId, visType.id)
    }

    @InternalApi
    public fun clearUid() {
        this.uid = NpcUid.NULL
    }

    public fun walk(dest: CoordGrid) {
        abortRoute()
        moveSpeed = defaultMoveSpeed
        routeDestination.add(dest)
    }

    public fun teleport(collision: CollisionFlagMap, dest: CoordGrid): Unit =
        PathingEntityCommon.teleport(this, collision, dest)

    public fun telejump(collision: CollisionFlagMap, dest: CoordGrid): Unit =
        PathingEntityCommon.telejump(this, collision, dest)

    public fun aiTimer(cycles: Int) {
        this.aiTimerStart = cycles
        this.aiTimer = cycles
    }

    public fun timer(timer: TimerType, cycles: Int) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        timerMap.schedule(timer, interval = cycles)
    }

    public fun clearTimer(timer: TimerType) {
        timerMap.remove(timer)
    }

    public fun aiQueue(queue: AiQueueType, cycles: Int) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        pendingAiQueue = queue
        pendingAiQueueCycle = cycles
    }

    @InternalApi
    public fun clearPendingAiQueue() {
        pendingAiQueue = null
    }

    public fun queue(queue: QueueType, cycles: Int, args: Any? = null) {
        require(cycles > 0) { "`cycles` must be greater than 0. (cycles=$cycles)" }
        queueList.add(queue, cycles, args)
    }

    public fun clearQueue(queue: QueueType) {
        queueList.removeAll(queue)
    }

    public fun setHunt(range: Int) {
        require(range >= 0) { "`range` must be positive. (range=$range)" }
        huntRange = range
    }

    public fun setHuntMode(mode: HuntModeType) {
        huntMode = mode.id
    }

    @InternalApi
    public fun resetHunt() {
        huntClock = 0
        huntObj = null
        huntLoc = null
        huntNpc = NpcUid.NULL
        huntPlayer = PlayerUid.NULL
        huntRange = type.huntRange
        huntMode = type.huntMode
    }

    @InternalApi
    public fun resetDefaults() {
        clearInteraction()
        resetFaceEntity()
        resetHunt()
        aiTimer = type.timer
    }

    @InternalApi
    public fun setRespawnValues() {
        pendingTelejump = true
        transmog = null
        cachedHitmark = null
        mode = defaultMode
        assignUid()
        clearInteraction()
        clearFaceEntity()
        resetPendingFaceSquare()
        resetHunt()
        resetAnim()
        showAllOps()
        copyStats(type)
        clearHeroPoints()
        queueList.clear()
        vars.backing.clear()
        strVars.backing.clear()
    }

    public fun resetMovement() {
        moveSpeed = MoveSpeed.Stationary
        abortRoute()
        clearInteraction()
    }

    override fun anim(seq: SeqType, delay: Int, priority: Int) {
        val setSequence = PathingEntityCommon.anim(this, seq, delay, priority)
        if (!setSequence) {
            return
        }
        if (pendingSequence == EntitySeq.ZERO) {
            infoProtocol.setSequence(-1, 0)
        } else {
            infoProtocol.setSequence(pendingSequence.id, pendingSequence.delay)
        }
    }

    override fun resetAnim() {
        pendingSequence = EntitySeq.ZERO
        infoProtocol.setSequence(-1, 0)
    }

    override fun spotanim(spot: SpotanimType, delay: Int, height: Int, slot: Int) {
        PathingEntityCommon.spotanim(this, spot.id, delay, height, slot)
        infoProtocol.setSpotanim(spot.id, delay, height, slot)
    }

    public fun say(text: String) {
        infoProtocol.setSay(text)
    }

    public fun showHeadbar(headbar: Headbar) {
        infoProtocol.showHeadbar(headbar)
    }

    public fun showHitmark(hitmark: Hitmark) {
        infoProtocol.showHitmark(hitmark)
    }

    public fun showAllOps() {
        infoProtocol.toggleOps(OpVisibility.showAll())
    }

    public fun hideAllOps() {
        infoProtocol.toggleOps(OpVisibility.hideAll())
    }

    public fun facePlayer(target: Player) {
        if (faceEntity.playerSlot != target.slotId) {
            PathingEntityCommon.facePlayer(this, target)
            infoProtocol.setFacePathingEntity(faceEntity.entitySlot)
        }
    }

    public fun faceNpc(target: Npc) {
        if (faceEntity.npcSlot != target.slotId) {
            PathingEntityCommon.faceNpc(this, target)
            infoProtocol.setFacePathingEntity(faceEntity.entitySlot)
        }
    }

    public fun resetFaceEntity() {
        // This is an assumption that the official game has a similar noop when the npc is not
        // currently facing a pathing entity.
        if (faceEntity != EntityFaceTarget.NULL) {
            PathingEntityCommon.resetFaceEntity(this)
            infoProtocol.setFacePathingEntity(faceEntity.entitySlot)
        }
    }

    private fun clearFaceEntity() {
        PathingEntityCommon.resetFaceEntity(this)
    }

    public fun transmog(type: UnpackedNpcType, duration: Int) {
        cachedHitmark = null
        transmog = type
        lifecycleChangeCycle = if (duration == Int.MAX_VALUE) -1 else currentMapClock + duration
        infoProtocol.setTransmog(type.id)
    }

    public fun resetTransmog() {
        cachedHitmark = null
        transmog = null
        lifecycleChangeCycle = -1
        infoProtocol.resetTransmog(originalType = id)
    }

    public fun copyStats(from: UnpackedNpcType) {
        copyBaseStats(from)
        copyCurrentStats(from)
    }

    public fun copyBaseStats(from: UnpackedNpcType) {
        baseAttackLvl = from.attack
        baseStrengthLvl = from.strength
        baseDefenceLvl = from.defence
        baseHitpointsLvl = from.hitpoints
        baseRangedLvl = from.ranged
        baseMagicLvl = from.magic
    }

    public fun copyCurrentStats(from: UnpackedNpcType) {
        attackLvl = from.attack
        strengthLvl = from.strength
        defenceLvl = from.defence
        hitpoints = from.hitpoints
        rangedLvl = from.ranged
        magicLvl = from.magic
    }

    public fun facingTarget(playerList: PlayerList): Player? =
        if (isFacingPlayer) {
            playerList[faceEntity.playerSlot]
        } else {
            null
        }

    public fun facingTarget(npcList: NpcList): Npc? =
        if (isFacingNpc) {
            npcList[faceEntity.npcSlot]
        } else {
            null
        }

    public fun resetMode() {
        clearInteraction()
        resetFaceEntity()
        mode = null
    }

    public fun defaultMode() {
        clearInteraction()
        resetFaceEntity()

        mode = defaultMode
    }

    public fun noneMode() {
        resetMovement()
        resetFaceEntity()

        mode = NpcMode.None
    }

    public fun playerEscape(target: Player) {
        resetMovement()

        mode = NpcMode.PlayerEscape
        facePlayer(target)
    }

    public fun playerFaceClose(target: Player) {
        resetMovement()

        mode = NpcMode.PlayerFaceClose
        facePlayer(target)
    }

    public fun playerFace(target: Player) {
        resetMovement()

        mode = NpcMode.PlayerFace
        facePlayer(target)
    }

    public fun playerFace(target: Player, faceFar: Boolean): Unit =
        if (faceFar) {
            playerFace(target)
        } else {
            playerFaceClose(target)
        }

    @InternalApi
    public fun clearIdleCycles() {
        wanderIdleCycles = -1
        patrolIdleCycles = -1
    }

    @InternalApi
    public fun isAnyoneNear(): Boolean {
        return infoProtocol.isActive()
    }

    public fun isType(type: UnpackedNpcType): Boolean = this.type.isType(type)

    public fun isVisType(type: UnpackedNpcType): Boolean = this.visType.isType(type)

    /**
     * Returns the headbar associated with [headbar] param for the **current** npc [visType].
     *
     * @throws IllegalStateException if [visType] does not have a value associated with the headbar
     *   [param] and [param] does not have a non-null `default` [HeadbarType] value.
     */
    public fun visHeadbar(headbar: ParamType<HeadbarType>): HeadbarType = visType.param(headbar)

    /**
     * Returns the param value associated with [param] from the **base** npc [type], or `null` if
     * the type does not have a value associated with [param] and [param] does not have a non-null
     * `default` value.
     *
     * If you wish to retrieve the param value for the current (transmog) type, use [visType] to
     * retrieve it.
     */
    public fun <T : Any> paramOrNull(param: ParamType<T>): T? = type.paramOrNull(param)

    /**
     * Returns the param value associated with [param] from the **base** npc [type].
     *
     * If you wish to retrieve the param value for the current (transmog) type, use [visType] to
     * retrieve it.
     *
     * @throws IllegalStateException if the type does not have a value associated with [param] and
     *   [param] does not have a non-null `default` value.
     */
    public fun <T : Any> param(param: ParamType<T>): T = type.param(param)

    public fun isContentType(content: ContentGroupType): Boolean = type.contentGroup == content.id

    override fun toString(): String = "Npc(uid=$uid, slot=$slotId, coords=$coords, type=$type)"
}

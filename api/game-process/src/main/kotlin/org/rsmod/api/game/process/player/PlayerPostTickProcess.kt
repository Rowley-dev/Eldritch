package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.game.process.GameLifecycle
import org.rsmod.api.player.forceDisconnect
import org.rsmod.api.player.output.MiscOutput
import org.rsmod.api.utils.logging.GameExceptionHandler
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.entity.util.EntityFaceAngle
import org.rsmod.game.seq.EntitySeq

public class PlayerPostTickProcess
@Inject
constructor(
    private val eventBus: EventBus,
    private val playerList: PlayerList,
    private val zoneUpdates: PlayerZoneUpdateProcessor,
    private val buildAreas: PlayerBuildAreaProcessor,
    private val regions: PlayerRegionProcessor,
    private val facing: PlayerFaceSquareProcessor,
    private val mapUpdates: PlayerMapUpdateProcessor,
    private val runUpdates: PlayerRunUpdateProcessor,
    private val invUpdates: PlayerInvUpdateProcessor,
    private val statUpdates: PlayerStatUpdateProcessor,
    private val exceptionHandler: GameExceptionHandler,
) {
    public fun process() {
        computeSharedBuffers()
        updateProtocolInfo()
        processPostTick()
        finalizePostTick()
    }

    private fun computeSharedBuffers() {
        zoneUpdates.computeEnclosedBuffers()
    }

    private fun updateProtocolInfo() {
        for (player in playerList) {
            player.tryOrDisconnect {
                facing.process(this)
                regions.process(this)
                buildAreas.process(this)
                clientCycle.update(this)
            }
        }
        eventBus.publish(GameLifecycle.UpdateInfo)
    }

    private fun processPostTick() {
        for (player in playerList) {
            player.tryOrDisconnect {
                processMapChanges()
                processClientCycle()
                processZoneUpdates()
                processInvUpdates()
                processStatUpdates()
                processRunUpdates()
                processClientState()
                cleanUpPendingUpdates()
            }
        }
    }

    private fun Player.processMapChanges() {
        mapUpdates.process(this)
    }

    private fun Player.processClientCycle() {
        if (loggingOut) {
            clientCycle.release()
            return
        }
        clientCycle.flush(this)
    }

    private fun Player.processZoneUpdates() {
        zoneUpdates.process(this)
    }

    private fun Player.processInvUpdates() {
        invUpdates.process(this)
    }

    private fun Player.processStatUpdates() {
        statUpdates.process(this)
    }

    private fun Player.processRunUpdates() {
        runUpdates.process(this)
    }

    private fun Player.processClientState() {
        if (closeClient) {
            closeClient = false
            closeClient()
            return
        }
        flushClient()
    }

    private fun Player.closeClient() {
        MiscOutput.logout(this)
        client.flushHighPriority()
        client.close()
    }

    private fun Player.flushClient() {
        MiscOutput.serverTickEnd(this)
        client.flush()
    }

    private fun Player.cleanUpPendingUpdates() {
        pendingSay = null
        pendingStepCount = 0
        pendingTeleport = false
        pendingTelejump = false
        pendingRunWeight = false
        pendingExactMove = null
        pendingFaceAngle = EntityFaceAngle.NULL
        pendingSequence = EntitySeq.NULL
        pendingSpotanims.clear()
        activeHeadbars.clear()
        activeHitmarks.clear()
        appearance.clearRebuildFlag()
    }

    private fun finalizePostTick() {
        zoneUpdates.clearEnclosedBuffers()
        zoneUpdates.clearPendingZoneUpdates()
        invUpdates.cleanUp()
    }

    private inline fun Player.tryOrDisconnect(block: Player.() -> Unit) =
        try {
            block(this)
        } catch (e: Exception) {
            forceDisconnect()
            exceptionHandler.handle(e) { "Error processing post-tick for player: $this." }
        } catch (e: NotImplementedError) {
            forceDisconnect()
            exceptionHandler.handle(e) { "Error processing post-tick for player: $this." }
        }
}

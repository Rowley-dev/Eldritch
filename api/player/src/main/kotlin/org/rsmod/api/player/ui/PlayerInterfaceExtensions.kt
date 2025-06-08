package org.rsmod.api.player.ui

import net.rsprot.protocol.game.outgoing.interfaces.IfCloseSub
import net.rsprot.protocol.game.outgoing.interfaces.IfMoveSub
import net.rsprot.protocol.game.outgoing.interfaces.IfOpenSub
import net.rsprot.protocol.game.outgoing.interfaces.IfOpenTop
import net.rsprot.protocol.game.outgoing.interfaces.IfSetAnim
import net.rsprot.protocol.game.outgoing.interfaces.IfSetEvents
import net.rsprot.protocol.game.outgoing.interfaces.IfSetHide
import net.rsprot.protocol.game.outgoing.interfaces.IfSetNpcHead
import net.rsprot.protocol.game.outgoing.interfaces.IfSetNpcHeadActive
import net.rsprot.protocol.game.outgoing.interfaces.IfSetObject
import net.rsprot.protocol.game.outgoing.interfaces.IfSetPlayerHead
import net.rsprot.protocol.game.outgoing.interfaces.IfSetText
import net.rsprot.protocol.game.outgoing.misc.player.TriggerOnDialogAbort
import org.rsmod.annotations.InternalApi
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.input.ResumePCountDialogInput
import org.rsmod.api.player.input.ResumePNameDialogInput
import org.rsmod.api.player.input.ResumePObjDialogInput
import org.rsmod.api.player.input.ResumePStringDialogInput
import org.rsmod.api.player.input.ResumePauseButtonInput
import org.rsmod.api.player.output.ChatType
import org.rsmod.api.player.output.ClientScripts.chatboxMultiInit
import org.rsmod.api.player.output.ClientScripts.confirmDestroyInit
import org.rsmod.api.player.output.ClientScripts.confirmOverlayInit
import org.rsmod.api.player.output.ClientScripts.ifSetTextAlign
import org.rsmod.api.player.output.ClientScripts.menu
import org.rsmod.api.player.output.ClientScripts.objboxSetButtons
import org.rsmod.api.player.output.ClientScripts.skillMultiInit
import org.rsmod.api.player.output.ClientScripts.topLevelChatboxResetBackground
import org.rsmod.api.player.output.ClientScripts.topLevelMainModalBackground
import org.rsmod.api.player.output.ClientScripts.topLevelMainModalOpen
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.runClientScript
import org.rsmod.api.player.vars.intVarBit
import org.rsmod.coroutine.GameCoroutine
import org.rsmod.coroutine.resume.DeferredResumeCondition
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.InvObj
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.interf.IfSubType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.ui.Component
import org.rsmod.game.ui.UserInterface
import org.rsmod.game.ui.UserInterfaceMap

private typealias OpenSub = org.rsmod.api.player.ui.IfOpenSub

private typealias CloseSub = org.rsmod.api.player.ui.IfCloseSub

private typealias MoveSub = org.rsmod.api.player.ui.IfMoveSub

private var Player.chatModalUnclamp: Int by intVarBit(varbits.chatmodal_unclamp)

public fun Player.ifSetObj(target: ComponentType, obj: ObjType, zoomOrCount: Int) {
    client.write(IfSetObject(target.packed, obj.id, zoomOrCount))
}

public fun Player.ifSetObj(target: ComponentType, obj: InvObj, zoomOrCount: Int) {
    client.write(IfSetObject(target.packed, obj.id, zoomOrCount))
}

public fun Player.ifSetAnim(target: ComponentType, seq: SeqType?) {
    client.write(IfSetAnim(target.interfaceId, target.component, seq?.id ?: -1))
}

public fun Player.ifSetPlayerHead(target: ComponentType) {
    client.write(IfSetPlayerHead(target.interfaceId, target.component))
}

/** @see [IfSetNpcHead] */
public fun Player.ifSetNpcHead(target: ComponentType, npc: NpcType) {
    client.write(IfSetNpcHead(target.interfaceId, target.component, npc.id))
}

/** @see [IfSetNpcHeadActive] */
public fun Player.ifSetNpcHeadActive(target: ComponentType, npcSlotId: Int) {
    client.write(IfSetNpcHeadActive(target.interfaceId, target.component, npcSlotId))
}

public fun Player.ifOpenSide(interf: InterfaceType, eventBus: EventBus) {
    openModal(interf, components.sidemodal, eventBus)
}

public fun Player.ifOpenMainModal(
    interf: InterfaceType,
    eventBus: EventBus,
    colour: Int = -1,
    transparency: Int = -1,
) {
    topLevelMainModalOpen(this, colour, transparency)
    ifOpenMain(interf, eventBus)
}

public fun Player.ifOpenMain(interf: InterfaceType, eventBus: EventBus) {
    openModal(interf, components.mainmodal, eventBus)
}

public fun Player.ifOpenMainSidePair(
    main: InterfaceType,
    side: InterfaceType,
    colour: Int,
    transparency: Int,
    eventBus: EventBus,
) {
    topLevelMainModalBackground(this, colour, transparency)
    openModal(main, components.mainmodal, eventBus)
    openModal(side, components.sidemodal, eventBus)
}

public fun Player.ifOpenOverlay(interf: InterfaceType, target: ComponentType, eventBus: EventBus) {
    ifOpenSub(interf, target, IfSubType.Overlay, eventBus)
}

public fun Player.ifOpenOverlay(interf: InterfaceType, eventBus: EventBus) {
    ifOpenOverlay(interf, components.toplevel_target_floater, eventBus)
}

public fun Player.ifOpenFullOverlay(interf: InterfaceType, eventBus: EventBus) {
    ifOpenOverlay(interf, components.toplevel_target_overlay_atmosphere, eventBus)
}

/**
 * Difference from [ifCloseModals]: this function clears all weak queues for the player and closes
 * any active dialog.
 *
 * @see [cancelActiveDialog]
 */
public fun Player.ifClose(eventBus: EventBus) {
    cancelActiveDialog()
    weakQueueList.clear()
    ifCloseModals(eventBus)
}

/**
 * Cancels and closes any active _input_ dialog suspension.
 *
 * #### Note
 * This is a custom concept and not part of any known game mechanic or command. It is primarily used
 * internally to close suspending input dialogs during the handling of `If3Button` packets.
 *
 * **Warning:** Avoid calling this function unless you fully understand its implications, as it can
 * interrupt active dialog-related processes.
 */
@InternalApi("Usage of this function should only be used internally, or sparingly.")
public fun Player.ifCloseInputDialog() {
    val coroutine = activeCoroutine ?: return
    if (coroutine.requiresInputDialogAbort()) {
        cancelActiveCoroutine()
        client.write(TriggerOnDialogAbort)
    }
}

/**
 * If [requiresInputDialogAbort] or [requiresPauseDialogAbort] conditions are met, the player's
 * active script will be cancelled ([Player.cancelActiveCoroutine]) and [TriggerOnDialogAbort] will
 * be sent to their client.
 */
private fun Player.cancelActiveDialog() {
    val coroutine = activeCoroutine ?: return
    if (coroutine.requiresPauseDialogAbort() || coroutine.requiresInputDialogAbort()) {
        cancelActiveCoroutine()
        client.write(TriggerOnDialogAbort)
    }
}

/**
 * Checks if the coroutine is suspended on a [DeferredResumeCondition] and the deferred type matches
 * [ResumePauseButtonInput], which occurs during dialogs with `Click here to continue`-esque pause
 * buttons.
 */
private fun GameCoroutine.requiresPauseDialogAbort(): Boolean =
    isAwaiting(ResumePauseButtonInput::class)

/**
 * Checks if the coroutine is suspended on a [DeferredResumeCondition] and the deferred type matches
 * any input from dialogue boxes built through cs2, which are not standard modals or overlays.
 */
private fun GameCoroutine.requiresInputDialogAbort(): Boolean {
    return isAwaitingAny(
        ResumePCountDialogInput::class,
        ResumePNameDialogInput::class,
        ResumePStringDialogInput::class,
        ResumePObjDialogInput::class,
    )
}

public fun Player.ifCloseModals(eventBus: EventBus) {
    // This gives us an iterable copy of the entries, so we are safe to modify `ui.modals` while
    // closing them.
    val modalEntries = ui.modals.entries()
    for ((key, value) in modalEntries) {
        val interf = UserInterface(value)
        val target = Component(key)
        closeModal(interf, target, eventBus)
    }
    // Make sure _all_ modals were closed. If not, then something is wrong, and we'd rather force
    // the player to disconnect than to allow them to keep modals open when they shouldn't.
    check(ui.modals.isEmpty()) {
        "Could not close all modals for player `$this`. (modals=${ui.modals})"
    }
}

public fun Player.ifSetEvents(target: ComponentType, range: IntRange, vararg event: IfEvent) {
    val packed = event.fold(0) { sum, element -> sum or element.bitmask }
    ui.events.add(target, range, packed)
    client.write(IfSetEvents(target.interfaceId, target.component, range.first, range.last, packed))
}

public fun Player.ifSetText(target: ComponentType, text: String) {
    client.write(IfSetText(target.interfaceId, target.component, text))
}

public fun Player.ifSetHide(target: ComponentType, hide: Boolean) {
    client.write(IfSetHide(target.interfaceId, target.component, hide))
}

public fun Player.ifOpenTop(topLevel: InterfaceType) {
    val userInterface = UserInterface(topLevel.id)
    ui.topLevel = userInterface
    client.write(IfOpenTop(topLevel.id))
}

public fun Player.ifMoveTop(dest: InterfaceType, eventBus: EventBus) {
    check(ui.topLevel != UserInterface.NULL) {
        "This function can only be used after `ifOpenTop` has been called. " +
            "Use `ifOpenTop` instead."
    }
    eventBus.publish(IfMoveTop(this, dest))
}

public fun Player.ifOpenSub(
    interf: InterfaceType,
    target: ComponentType,
    type: IfSubType,
    eventBus: EventBus,
): Unit =
    when (type) {
        IfSubType.Modal -> openModal(interf, target, eventBus)
        IfSubType.Overlay -> openOverlay(interf, target, eventBus)
    }

public fun Player.ifCloseSub(interf: InterfaceType, eventBus: EventBus) {
    closeModal(interf, eventBus)
    closeOverlay(interf, eventBus)
}

public fun Player.ifCloseModal(interf: InterfaceType, eventBus: EventBus) {
    closeModal(interf, eventBus)
}

public fun Player.ifCloseOverlay(interf: InterfaceType, eventBus: EventBus) {
    closeOverlay(interf, eventBus)
}

private fun Player.openModal(interf: InterfaceType, target: ComponentType, eventBus: EventBus) {
    val idComponent = target.toIdComponent()
    val idInterface = interf.toIdInterface()
    triggerCloseSubs(idComponent, eventBus)
    ui.removeQueuedCloseSub(target)
    ui.modals[idComponent] = idInterface

    // Translate any gameframe target component when sent to the client. As far as the server is
    // aware, the interface is being opened on the "base" target component. (when applicable)
    val translated = ui.translate(idComponent)
    client.write(IfOpenSub(translated.parent, translated.child, interf.id, IfSubType.Modal.id))

    eventBus.publish(OpenSub(this, idInterface, idComponent, IfSubType.Modal))
}

private fun Player.openOverlay(interf: InterfaceType, target: ComponentType, eventBus: EventBus) {
    val idComponent = target.toIdComponent()
    val idInterface = interf.toIdInterface()
    triggerCloseSubs(idComponent, eventBus)
    ui.removeQueuedCloseSub(target)
    ui.overlays[idComponent] = idInterface

    // Translate any gameframe target component when sent to the client. As far as the server is
    // aware, the interface is being opened on the "base" target component. (when applicable)
    val translated = ui.translate(idComponent)
    client.write(IfOpenSub(translated.parent, translated.child, interf.id, IfSubType.Overlay.id))

    eventBus.publish(OpenSub(this, idInterface, idComponent, IfSubType.Overlay))
}

private fun Player.closeModal(interf: InterfaceType, eventBus: EventBus) {
    val idInterface = interf.toIdInterface()
    val target = ui.modals.getComponent(idInterface)
    if (target != null) {
        closeModal(idInterface, target, eventBus)
    }
}

private fun Player.closeModal(interf: UserInterface, target: Component, eventBus: EventBus) {
    ui.modals.remove(target)
    ui.events.clear(interf)

    // Translate any gameframe target component when sent to the client. As far as the server
    // is aware, the interface was open on the "base" target component. (when applicable)
    val translated = ui.translate(target)
    client.write(IfCloseSub(translated.parent, translated.child))

    eventBus.publish(CloseSub(this, interf, target))

    closeOverlayChildren(interf, eventBus)
}

private fun Player.closeOverlay(interf: InterfaceType, eventBus: EventBus) {
    val idInterface = interf.toIdInterface()
    val target = ui.overlays.getComponent(idInterface)
    if (target != null) {
        closeOverlay(idInterface, target, eventBus)
    }
}

private fun Player.closeOverlay(interf: UserInterface, target: Component, eventBus: EventBus) {
    ui.overlays.remove(target)
    ui.events.clear(interf)

    // Translate any gameframe target component when sent to the client. As far as the server
    // is aware, the interface was open on the "base" target component. (when applicable)
    val translated = ui.translate(target)
    client.write(IfCloseSub(translated.parent, translated.child))

    eventBus.publish(CloseSub(this, interf, target))

    closeOverlayChildren(interf, eventBus)
}

private fun Player.closeOverlayChildren(parent: UserInterface, eventBus: EventBus) {
    // This gives us an iterable copy of the entries, so we are safe to modify `ui.overlays` while
    // closing them.
    val overlayEntries = ui.overlays.entries()
    for ((key, value) in overlayEntries) {
        val interf = UserInterface(value)
        val target = Component(key)
        if (target.parent == parent.id) {
            closeOverlay(interf, target, eventBus)
        }
    }
}

@InternalApi("Usage of this function should only be used internally")
public fun Player.closeSubs(from: Component, eventBus: EventBus) {
    val remove = ui.modals.remove(from) ?: ui.overlays.remove(from)
    if (remove != null) {
        ui.events.clear(remove)

        // Translate any gameframe target component when sent to the client. As far as the server
        // is aware, the interface was open on the "base" target component. (when applicable)
        val translated = ui.translate(from)
        client.write(IfCloseSub(translated.parent, translated.child))

        eventBus.publish(CloseSub(this, remove, from))

        closeOverlayChildren(remove, eventBus)
    }
}

/**
 * Similar to [closeSubs], but only triggers "close sub" scripts and does _not_ send [IfCloseSub]
 * packet to the client.
 */
private fun Player.triggerCloseSubs(from: Component, eventBus: EventBus) {
    val remove = ui.modals.remove(from) ?: ui.overlays.remove(from)
    if (remove != null) {
        ui.events.clear(remove)
        eventBus.publish(CloseSub(this, remove, from))
        triggerCloseOverlayChildren(remove, eventBus)
    }
}

/**
 * Similar to [closeOverlayChildren], but only triggers "close sub" scripts and does _not_ send
 * [IfCloseSub] packet to the client.
 */
private fun Player.triggerCloseOverlayChildren(parent: UserInterface, eventBus: EventBus) {
    // This gives us an iterable copy of the entries, so we are safe to modify `ui.overlays` while
    // closing them.
    val overlayEntries = ui.overlays.entries()
    for ((key, value) in overlayEntries) {
        val interf = UserInterface(value)
        val target = Component(key)
        if (target.parent == parent.id) {
            triggerCloseOverlay(interf, target, eventBus)
        }
    }
}

/**
 * Similar to [closeOverlay], but only triggers "close sub" scripts and does _not_ send [IfCloseSub]
 * packet to the client.
 */
private fun Player.triggerCloseOverlay(
    interf: UserInterface,
    target: Component,
    eventBus: EventBus,
) {
    ui.overlays.remove(target)
    ui.events.clear(interf)
    eventBus.publish(CloseSub(this, interf, target))
    triggerCloseOverlayChildren(interf, eventBus)
}

public fun Player.ifMoveSub(
    source: Component,
    dest: Component,
    base: Component,
    eventBus: EventBus,
) {
    client.write(IfMoveSub(source.packed, dest.packed))
    eventBus.publish(MoveSub(this, base.packed))
}

private fun InterfaceType.toIdInterface() = UserInterface(id)

private fun ComponentType.toIdComponent() = Component(packed)

private fun UserInterfaceMap.translate(component: Component): Component =
    gameframe.getOrNull(component) ?: component

/*
 * Dialogue helper functions
 *
 * These functions are intended to help with displaying various dialogue interfaces to the player.
 * However, they do _not_ properly handle state suspension or resuming from player input.
 *
 * Important: These functions should only be used internally within systems that properly manage
 * player state, input handling, and coroutine suspension. Direct usage in other contexts may result
 * in unwanted behavior.
 */

internal fun Player.ifMesbox(text: String, pauseText: String, lineHeight: Int, eventBus: EventBus) {
    mes(text, ChatType.Mesbox)
    openModal(interfaces.messagebox, components.chatbox_chatmodal, eventBus)
    ifSetText(components.messagebox_text, text)
    ifSetTextAlign(this, components.messagebox_text, alignH = 1, alignV = 1, lineHeight)
    ifSetPauseText(components.messagebox_pbutton, pauseText)
    // TODO: Look into clientscript to name property and place in clientscript utility class.
    runClientScript(1508, "0")
}

internal fun Player.ifObjbox(
    text: String,
    obj: Int,
    zoom: Int,
    pauseText: String,
    eventBus: EventBus,
) {
    mes(text, ChatType.Mesbox)
    ifOpenChat(interfaces.obj_dialogue, constants.modal_infinitewidthandheight, eventBus)
    objboxSetButtons(this, pauseText)
    if (pauseText.isNotBlank()) {
        ifSetEvents(components.objectbox_pbutton, 0..1, IfEvent.PauseButton)
    } else {
        // Note: This purposefully disables `if_events` for subcomponents -1 to -1.
        ifSetEvents(components.objectbox_pbutton, -1..-1)
    }
    ifSetObj(components.objectbox_item, obj, zoom)
    ifSetText(components.objectbox_text, text)
}

internal fun Player.ifDoubleobjbox(
    text: String,
    obj1: Int,
    zoom1: Int,
    obj2: Int,
    zoom2: Int,
    pauseText: String,
    eventBus: EventBus,
) {
    mes(text, ChatType.Mesbox)
    ifOpenChat(interfaces.double_obj_dialogue, constants.modal_infinitewidthandheight, eventBus)
    ifSetPauseText(components.objectbox_double_pbutton, pauseText)
    ifSetObj(components.objectbox_double_model1, obj1, zoom1)
    ifSetObj(components.objectbox_doublee_model2, obj2, zoom2)
    ifSetText(components.objectbox_double_text, text)
}

internal fun Player.ifConfirmDestroy(
    header: String,
    text: String,
    obj: Int,
    count: Int,
    eventBus: EventBus,
) {
    ifOpenChat(interfaces.destroy_obj_dialogue, constants.modal_fixedwidthandheight, eventBus)
    confirmDestroyInit(this, header, text, obj, count)
    ifSetEvents(components.confirmdestroy_pbutton, 0..1, IfEvent.PauseButton)
}

internal fun Player.ifConfirmOverlay(
    target: ComponentType,
    title: String,
    text: String,
    cancel: String,
    confirm: String,
    eventBus: EventBus,
) {
    ifOpenSub(interfaces.popupoverlay, target, IfSubType.Overlay, eventBus)
    confirmOverlayInit(this, target, title, text, cancel, confirm)
}

internal fun Player.ifConfirmOverlayClose(eventBus: EventBus): Unit =
    ifCloseOverlay(interfaces.popupoverlay, eventBus)

internal fun Player.ifMenu(
    title: String,
    joinedChoices: String,
    hotkeys: Boolean,
    eventBus: EventBus,
) {
    ifOpenMainModal(interfaces.menu, eventBus)
    menu(this, title, joinedChoices, hotkeys)
    ifSetEvents(components.menu_list, 0..127, IfEvent.PauseButton)
}

internal fun Player.ifSkillMultiSelect(
    quantitySelectionOptions: Int,
    title: String,
    maximumQuantity: Int,
    ObjType1Id: Int? = -1,
    ObjType2Id: Int? = -1,
    ObjType3Id: Int? = -1,
    ObjType4Id: Int? = -1,
    ObjType5Id: Int? = -1,
    ObjType6Id: Int? = -1,
    ObjType7Id: Int? = -1,
    ObjType8Id: Int? = -1,
    ObjType9Id: Int? = -1,
    ObjType10Id: Int? = -1,
    defaultQuantity: Int,
    eventBus: EventBus,
) {
    skillMultiInit(this,
        quantitySelectionOptions,
        title,
        maximumQuantity,
        ObjType1Id,
        ObjType2Id,
        ObjType3Id,
        ObjType4Id,
        ObjType5Id,
        ObjType6Id,
        ObjType7Id,
        ObjType8Id,
        ObjType9Id,
        ObjType10Id,
        defaultQuantity,
    )
    ifOpenSub(interfaces.skillmulti, components.chatbox_chatmodal, IfSubType.Modal, eventBus)
    ifSetEvents(components.skillmulti_a, 0..quantitySelectionOptions, IfEvent.PauseButton)
//        return resumeWithMainModalProtectedAccess(input.subcomponent.absoluteValue, modal)
}

/** @see [chatboxMultiInit] */
internal fun Player.ifChoice(
    title: String,
    joinedChoices: String,
    choiceCountInclusive: Int,
    eventBus: EventBus,
) {
    ifOpenChat(interfaces.chatmenu, constants.modal_infinitewidthandheight, eventBus)
    chatboxMultiInit(this, title, joinedChoices)
    ifSetEvents(components.chatmenu_pbutton, 1..choiceCountInclusive, IfEvent.PauseButton)
}

internal fun Player.ifChatPlayer(
    title: String,
    text: String,
    expression: SeqType?,
    pauseText: String,
    lineHeight: Int,
    eventBus: EventBus,
) {
    mes("$title|$text", ChatType.Dialogue)
    ifOpenChat(interfaces.chat_right, constants.modal_fixedwidthandheight, eventBus)
    ifSetPlayerHead(components.chat_right_head)
    ifSetAnim(components.chat_right_head, expression)
    ifSetText(components.chat_right_name, title)
    ifSetText(components.chat_right_text, text)
    ifSetTextAlign(this, components.chat_right_text, alignH = 1, alignV = 1, lineHeight)
    ifSetPauseText(components.chat_right_pbutton, pauseText)
}

internal fun Player.ifChatNpcActive(
    title: String,
    npcSlotId: Int,
    text: String,
    chatanim: SeqType?,
    pauseText: String,
    lineHeight: Int,
    eventBus: EventBus,
) {
    mes("$title|$text", ChatType.Dialogue)
    ifOpenChat(interfaces.chat_left, constants.modal_fixedwidthandheight, eventBus)
    ifSetNpcHeadActive(components.chat_left_head, npcSlotId)
    ifSetAnim(components.chat_left_head, chatanim)
    ifSetText(components.chat_left_name, title)
    ifSetText(components.chat_left_text, text)
    ifSetTextAlign(this, components.chat_left_text, alignH = 1, alignV = 1, lineHeight)
    ifSetPauseText(components.chat_left_pbutton, pauseText)
}

internal fun Player.ifChatNpcSpecific(
    title: String,
    type: NpcType,
    text: String,
    chatanim: SeqType?,
    pauseText: String,
    lineHeight: Int,
    eventBus: EventBus,
) {
    mes("$title|$text", ChatType.Dialogue)
    ifOpenChat(interfaces.chat_left, constants.modal_infinitewidthandheight, eventBus)
    ifSetNpcHead(components.chat_left_head, type)
    ifSetAnim(components.chat_left_head, chatanim)
    ifSetText(components.chat_left_name, title)
    ifSetText(components.chat_left_text, text)
    ifSetTextAlign(this, components.chat_left_text, alignH = 1, alignV = 1, lineHeight)
    ifSetPauseText(components.chat_left_pbutton, pauseText)
}

internal fun Player.ifOpenChat(interf: InterfaceType, widthAndHeightMode: Int, eventBus: EventBus) {
    chatModalUnclamp = widthAndHeightMode
    topLevelChatboxResetBackground(this)
    openModal(interf, components.chatbox_chatmodal, eventBus)
}

private fun Player.ifSetPauseText(component: ComponentType, text: String) {
    if (text.isNotBlank()) {
        ifSetEvents(component, -1..-1, IfEvent.PauseButton)
    } else {
        ifSetEvents(component, -1..-1)
    }
    ifSetText(component, text)
}

private fun Player.ifSetObj(target: ComponentType, obj: Int, zoomOrCount: Int) {
    client.write(IfSetObject(target.packed, obj, zoomOrCount))
}

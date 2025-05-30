package org.rsmod.api.type.editors.varp

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.script.dsl.VarpPluginBuilder
import org.rsmod.game.type.varp.UnpackedVarpType
import org.rsmod.game.type.varp.VarpType

public abstract class VarpEditor : TypeEditor<UnpackedVarpType>() {
    public fun edit(type: VarpType, init: VarpPluginBuilder.() -> Unit) {
        val builder = VarpPluginBuilder(type.internalNameValue)

        // We call `defaultTransmit` because `VarpPluginBuilder` defaults `transmit` to
        // `VarpTransmitLevel.Never`. Since most server-created varps are server-side only,
        // this is a sensible default for new varps.
        //
        // However, when _editing_ an existing varp, we want to inherit its default `transmit` level
        // from the cache. Without this, `edit` would always override the existing `transmit` level,
        // which could lead to unexpected and unwanted behavior.
        builder.defaultTransmit()

        builder.apply(init)

        val type = builder.build(id = -1)
        cache += type
    }
}

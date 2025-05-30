package org.rsmod.api.cache.types.inv

import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import kotlin.math.min
import org.openrs2.cache.Cache
import org.rsmod.api.cache.Js5Archives
import org.rsmod.api.cache.Js5Configs
import org.rsmod.api.cache.util.EncoderContext
import org.rsmod.api.cache.util.encodeConfig
import org.rsmod.game.type.inv.InvTypeBuilder
import org.rsmod.game.type.inv.UnpackedInvType

public object InvTypeEncoder {
    public fun encodeAll(
        cache: Cache,
        types: Iterable<UnpackedInvType>,
        ctx: EncoderContext,
    ): List<UnpackedInvType> {
        val buffer = PooledByteBufAllocator.DEFAULT.buffer()
        val archive = Js5Archives.CONFIG
        val config = Js5Configs.INV
        val packed = mutableListOf<UnpackedInvType>()
        for (type in types) {
            val oldBuf =
                if (cache.exists(archive, config, type.id)) {
                    cache.read(archive, config, type.id)
                } else {
                    null
                }
            val newBuf =
                buffer.clear().encodeConfig {
                    encodeJs5(type, this)
                    if (ctx.encodeFull) {
                        encodeGame(type, this)
                    }
                }
            if (newBuf != oldBuf) {
                cache.write(archive, config, type.id, newBuf)
                packed += type
            }
            oldBuf?.release()
        }
        buffer.release()
        return packed
    }

    public fun encodeJs5(type: UnpackedInvType, data: ByteBuf): Unit =
        with(type) {
            data.writeByte(2)
            data.writeShort(type.size)
        }

    public fun encodeGame(type: UnpackedInvType, data: ByteBuf): Unit =
        with(type) {
            if (type.scope != InvTypeBuilder.DEFAULT_SCOPE) {
                data.writeByte(200)
                data.writeByte(type.scope.id)
            }

            if (type.stack != InvTypeBuilder.DEFAULT_STACK) {
                data.writeByte(201)
                data.writeByte(type.stack.id)
            }

            if (type.size != InvTypeBuilder.DEFAULT_SIZE) {
                if (type.size < 255) {
                    data.writeByte(202)
                    data.writeByte(type.size)
                } else {
                    data.writeByte(203)
                    data.writeShort(type.size)
                }
            }

            if (type.flags != InvTypeBuilder.DEFAULT_FLAGS) {
                data.writeByte(204)
                data.writeInt(type.flags)
            }

            val defaultStock = type.stock
            if (defaultStock?.isNotEmpty() == true) {
                check(defaultStock.size < 256) { "Can only store up to 255 default stock." }
                data.writeByte(205)
                data.writeByte(defaultStock.size - 1)
                for (stock in defaultStock) {
                    if (stock == null) {
                        data.writeByte(0)
                        continue
                    }
                    data.writeByte(1 + min(254, stock.count))
                    if (stock.count >= 254) {
                        data.writeInt(stock.count)
                    }
                    data.writeShort(stock.obj)
                    data.writeShort(stock.restockCycles)
                }
            }
        }
}

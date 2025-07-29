package me.obsilabor.tpshud.server

import me.obsilabor.tpshud.networking.CommonHandshakePayload
import me.obsilabor.tpshud.networking.CommonTickRatePayload
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket
import java.util.*

class TpsHudFabricServer : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        PayloadTypeRegistry.playS2C().register(CommonHandshakePayload.PAYLOAD_ID, CommonHandshakePayload.CODEC)
        PayloadTypeRegistry.playS2C().register(CommonTickRatePayload.PAYLOAD_ID, CommonTickRatePayload.CODEC)
        ServerLifecycleEvents.SERVER_STARTED.register {
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    for (player in it.playerManager.playerList) {
                        ServerPlayNetworking.send(player, CommonTickRatePayload(it.averageTickTime.toDouble(), false))
                    }
                }
            }, 0, 20)
        }
        ServerPlayConnectionEvents.JOIN.register { _, connection, _ ->
            connection.sendPacket(CustomPayloadS2CPacket(CommonHandshakePayload()))
        }
    }
}
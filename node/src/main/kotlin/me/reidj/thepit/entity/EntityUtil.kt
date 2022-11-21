package me.reidj.thepit.entity

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import me.reidj.thepit.app
import me.reidj.thepit.player.User
import net.minecraft.server.v1_12_R1.Packet
import net.minecraft.server.v1_12_R1.PacketPlayOutMultiPacket
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object EntityUtil {

    private val viewEntities = hashMapOf<Player, HashSet<Int>>()
    private val entities = hashSetOf<Entity>()

    fun generateEntities(user: User) {
        // TODO Добавить спавн несколько мобов
        user.dungeon.entities.forEach {
            entities.add(it.key)
            repeat(it.value) {
                spawn(user.player)
            }
        }
    }

    private fun spawn(player: Player) {
        entities.forEach {
            it.create()
            viewEntities[player]?.add(it.current.entityId)
            app.worldMeta.world.handle.addEntity(it.current.entity, CreatureSpawnEvent.SpawnReason.CUSTOM)
        }
    }

    fun register(player: Player) {
        viewEntities[player] = hashSetOf()

        packetListener<PacketPlayOutMultiPacket>(player) {
            packets.forEach { packet ->
                if (packet is PacketPlayOutSpawnEntityLiving) {
                    if (viewEntities[player]?.contains(packet.a) == true) return@forEach
                    packet.c = 1
                    packet.d = 0.0
                    packet.e = 0.0
                    packet.f = 0.0
                }
            }
        }
    }

    private inline fun <reified T : Packet<*>> packetListener(player: Player, noinline handler: T.() -> Unit) {
        (player as CraftPlayer).handle.playerConnection.networkManager.channel.pipeline()
            .addBefore("packet_handler", UUID.randomUUID().toString(), object : ChannelDuplexHandler() {
                override fun write(ctx: ChannelHandlerContext?, msg: Any?, promise: ChannelPromise?) {
                    if (msg is T) {
                        handler.invoke(msg)
                    }
                    super.write(ctx, msg, promise)
                }
            })
    }
}
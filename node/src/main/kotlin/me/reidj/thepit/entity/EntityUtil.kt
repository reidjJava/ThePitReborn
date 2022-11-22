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
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object EntityUtil {

    private val viewEntities = hashMapOf<Player, HashSet<Int>>()

    fun spawn(user: User) {
        user.dungeon.entities.forEach { entry ->
            repeat(entry.value) { 
                val location = user.dungeon.getLocation()
                val current = app.worldMeta.world.createEntity(location, entry.key.entityType.clazz).getBukkitEntity()
                val entity = current as LivingEntity

                viewEntities[user.player]?.add(entity.entityId)
                app.worldMeta.world.handle.addEntity(current.entity, CreatureSpawnEvent.SpawnReason.CUSTOM)
                entry.key.create(entity)
                user.dungeon.removeLocation(location)
            }
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
            .addBefore("packet_handler", player.name, object : ChannelDuplexHandler() {
                override fun write(ctx: ChannelHandlerContext?, msg: Any?, promise: ChannelPromise?) {
                    if (msg is T) {
                        handler.invoke(msg)
                    }
                    super.write(ctx, msg, promise)
                }
            })
    }
}
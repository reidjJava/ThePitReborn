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
import ru.cristalix.core.util.UtilEntity
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object EntityUtil {

    private val viewEntities = hashMapOf<UUID, HashSet<Int>>()
    private val enemies = hashMapOf<Player, ArrayList<org.bukkit.entity.Entity>>()

    val targetPlayer = hashMapOf<UUID, UUID>()

    fun spawn(user: User) {
        val dungeon = user.dungeon!!
        val partySize = dungeon.party.size
        val player = user.player

        enemies[player] = arrayListOf()

        dungeon.entities.forEach {
            val uuid = user.stat.uuid
            dungeon.entitiesLocations.forEach { location ->
                it.create(location)
                it.setTarget(uuid)

                it.changeDamage(partySize + 3.0)
                it.changeHealth(partySize + 12.0)

                dungeon.party
                    .mapNotNull { app.getUser(it) }
                    .filter { it.dungeon?.uuid == dungeon.uuid }
                    .forEach { member -> viewEntities[member.stat.uuid]?.add(it.entity.entityId) }

                enemies[player]?.add(it.entity)

                app.getWorld().handle.addEntity(it.current.entity, CreatureSpawnEvent.SpawnReason.CUSTOM)

                UtilEntity.setScale(it.entity, it.scale.x, it.scale.y, it.scale.z)
            }
        }
    }

    fun register(player: Player) {
        val uuid = player.uniqueId
        viewEntities[uuid] = hashSetOf()

        packetListener<PacketPlayOutMultiPacket>(player) {
            packets.forEach { packet ->
                if (packet is PacketPlayOutSpawnEntityLiving) {
                    if (viewEntities[uuid]?.contains(packet.a) == true) return@forEach
                    packet.c = 1
                    packet.d = 0.0
                    packet.e = 0.0
                    packet.f = 0.0
                }
            }
        }
    }

    fun clearEntities(user: User) {
        enemies[user.player]?.forEach { removeEntity(it) }
        enemies[user.player]?.clear()
    }

    fun removeEntity(entity: org.bukkit.entity.Entity) {
        targetPlayer.remove(entity.uniqueId)
        viewEntities.keys.forEach {
            viewEntities[it]!!.remove(entity.entityId)
        }
        entity.remove()
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
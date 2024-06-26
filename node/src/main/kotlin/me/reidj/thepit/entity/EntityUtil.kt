package me.reidj.thepit.entity

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import me.func.mod.util.after
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
    private val enemies = hashMapOf<UUID, ArrayList<org.bukkit.entity.Entity>>()

    val targetPlayer = hashMapOf<UUID, UUID>()

    fun spawn(user: User) {
        val dungeon = user.dungeon!!
        val type = dungeon.type
        val partySize = dungeon.party.size
        val uuid = user.stat.uuid

        after(3) {
            enemies[uuid] = arrayListOf()

            type.entitiesLocations.forEach { location ->
                val mob = type.entities.random()
                if (dungeon.leader == uuid) {
                    mob.create(user.player, location)
                    mob.setTarget(uuid)
                    mob.changeDamage(partySize + 3.0)
                    mob.changeHealth(partySize + 12.0)

                    app.getWorld().handle.addEntity(mob.current.entity, CreatureSpawnEvent.SpawnReason.CUSTOM)

                    UtilEntity.setScale(mob.entity, mob.scale.x, mob.scale.y, mob.scale.z)

                    enemies[uuid]?.add(mob.entity)
                    viewEntities[uuid]?.add(mob.entity.entityId)
                } else {
                    enemies[dungeon.leader]?.forEach {
                        viewEntities[uuid]?.add(it.entityId)
                        enemies[uuid]?.add(it)
                    }
                }
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

    fun clearEntities(user: User, isKillEntity: Boolean) {
        val uuid = user.stat.uuid
        enemies[uuid]?.forEach { removeEntity(it, isKillEntity) }
        enemies[uuid]?.clear()
    }

    fun removeEntity(entity: org.bukkit.entity.Entity, isKillEntity: Boolean) {
        targetPlayer.remove(entity.uniqueId)
        viewEntities.keys.forEach {
            viewEntities[it]!!.remove(entity.entityId)
        }
        if (isKillEntity) {
            entity.remove()
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
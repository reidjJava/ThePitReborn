package me.reidj.thepit.listener

import me.func.mod.Anime
import me.func.mod.util.after
import me.func.mod.util.command
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.status.MessageStatus
import me.reidj.thepit.clock.detail.CombatManager
import me.reidj.thepit.player.prepare.PreparePlayerBrain
import me.reidj.thepit.util.systemMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class SpawnHandler : Listener {

    private val uuids = hashSetOf<UUID>()

    init {
        command("spawn") { player, _ ->
            val uuid = player.uniqueId

            if (uuid in uuids || CombatManager.containKey(uuid)) {
                return@command
            }

            uuids.add(uuid)

            Anime.timer(player, "До телепортации на спавн", 10)

            after(10 * 20) {
                if (uuid in uuids) {
                    PreparePlayerBrain.spawnTeleport(player)
                    uuids.remove(uuid)
                }
            }
        }
    }

    @EventHandler
    fun PlayerMoveEvent.handle() {
        val uuid = player.uniqueId
        if (uuid in uuids && (to.blockX != from.blockX || to.blockZ != from.blockZ)) {
            uuids.remove(uuid)
            player.systemMessage(MessageStatus.ERROR, GlowColor.RED, "Телепортация на спавн была отменена!")
            Anime.timer(player, "До телепортации на спавн", 0)
        }
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        uuids.remove(player.uniqueId)
    }

    @EventHandler
    fun PlayerKickEvent.handle() {
        uuids.remove(player.uniqueId)
    }
}
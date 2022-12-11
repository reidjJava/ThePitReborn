package me.reidj.thepit.dungeon

import me.reidj.thepit.app
import me.reidj.thepit.util.teleportAndPlayMusic
import org.bukkit.entity.Player
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class DungeonData(
    val uuid: UUID,
    val type: DungeonType,
    val leader: UUID,
    var party: MutableSet<UUID> = mutableSetOf(),
    ) {
    fun teleport(player: Player) = player.teleportAndPlayMusic(type.location)

    fun getPartyMemberWithUser() = party.mapNotNull { app.getUser(it) }
}

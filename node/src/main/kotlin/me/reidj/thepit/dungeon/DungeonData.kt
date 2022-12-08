package me.reidj.thepit.dungeon

import me.reidj.thepit.app
import me.reidj.thepit.entity.Entity
import me.reidj.thepit.util.teleportAndPlayMusic
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class DungeonData(
    val uuid: UUID,
    val dungeonLocation: Location,
    val entities: List<Entity>,
    val entitiesLocations: List<Location>,
    val leader: UUID,
    var party: MutableSet<UUID> = mutableSetOf(),
    ) {
    fun teleport(player: Player) = player.teleportAndPlayMusic(dungeonLocation)

    fun getPartyMemberWithUser() = party.mapNotNull { app.getUser(it) }
}

package me.reidj.thepit.dungeon

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
    val dungeonLocation: Location,
    val entities: MutableList<Entity>,
    val entitiesLocations: MutableList<Location>,
    var party: MutableSet<UUID> = mutableSetOf(),
    ) {
    fun teleport(player: Player) = player.teleportAndPlayMusic(dungeonLocation)
}

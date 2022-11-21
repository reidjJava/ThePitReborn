package me.reidj.thepit.dungeon

import me.reidj.thepit.entity.Entity
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class DungeonData(
    val dungeonLocation: Location,
    val entities: HashMap<Entity, Int>,
    val party: HashSet<UUID>
) {
    fun teleport(player: Player) = player.teleport(dungeonLocation)
}

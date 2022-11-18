package me.reidj.thepit.dungeon

import org.bukkit.entity.Player
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class DungeonData(val type: DungeonType, val party: HashSet<UUID>) {
    fun teleport(player: Player) = player.teleport(type.dungeonLocation)
}

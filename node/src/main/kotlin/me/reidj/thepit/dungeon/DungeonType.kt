package me.reidj.thepit.dungeon

import me.reidj.thepit.app
import me.reidj.thepit.entity.Entity
import me.reidj.thepit.entity.Zombie
import org.bukkit.Location

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
enum class DungeonType(
    val dungeonLocation: Location,
    val entities: HashMap<Entity, Int>
) {
    TEST(
        Location(app.worldMeta.world, -423.0, 57.0, 612.0),
        hashMapOf(Zombie(Location(app.worldMeta.world, -430.0, 57.0, 611.0)) to 5)
    )
}
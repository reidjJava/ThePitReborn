package me.reidj.thepit.dungeon

import me.reidj.thepit.app
import me.reidj.thepit.entity.Entity
import me.reidj.thepit.entity.successor.CaveTroll
import me.reidj.thepit.entity.successor.Kobold
import me.reidj.thepit.entity.successor.Orc
import me.reidj.thepit.entity.successor.Urukhai
import org.bukkit.Location

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
enum class DungeonType(
    val title: String,
    val tag: String,
    val location: Location,
    val entities: List<Entity>,
    val entitiesLocations: List<Location>
) {
    FORGOTTEN_MINES(
        "Заброшенные шахты",
        "mine",
        app.worldMeta.label("dungeon-mine")!!.clone().also {
            it.y += 1.0
            it.yaw = 90f
        },
        listOf(Orc(), CaveTroll(), Urukhai(), Kobold()),
        app.worldMeta.labels("mine-mob")
    ),
    ;
}
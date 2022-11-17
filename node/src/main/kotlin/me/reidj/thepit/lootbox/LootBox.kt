package me.reidj.thepit.lootbox

import me.func.mod.world.Banners
import me.func.mod.world.Banners.location
import org.bukkit.Location

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class LootBox(
    val title: String,
    val location: Location,
    val drops: Set<BoxDropType> = setOf()
) {
    fun createBanner() {
        Banners.new {
            content = "$title лутбокс"
            opacity = 0.0
            location(location)
        }
    }
}

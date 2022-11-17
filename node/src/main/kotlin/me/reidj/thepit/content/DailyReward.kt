package me.reidj.thepit.content

import dev.implario.bukkit.item.item
import me.reidj.thepit.player.User
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
enum class DailyReward(val title: String, val itemStack: ItemStack, val give: (User) -> Any) {
    ONE("1 день", item { type(Material.CLAY_BALL) }, {}),
    TWO("2 день", item { type(Material.CLAY_BALL) }, {}),
    THREE("3 день", item { type(Material.CLAY_BALL) }, {}),
    FOUR("4 день", item { type(Material.CLAY_BALL) }, {}),
    FIVE("5 день", item { type(Material.CLAY_BALL) }, {}),
    SIX("6 день", item { type(Material.CLAY_BALL) }, {}),
    SEVEN("7 день", item { type(Material.CLAY_BALL) }, {}),
    ;
}
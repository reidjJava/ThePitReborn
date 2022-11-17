package me.reidj.thepit.lootbox

import dev.implario.bukkit.item.item
import me.func.protocol.data.rare.DropRare
import org.bukkit.inventory.ItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
enum class BoxDropType(
    val title: String,
    val lootBox: String,
    val itemStack: ItemStack,
    val chance: Double,
    val rare: DropRare
) {
    TEST("Тест1", "halloween", item { type(org.bukkit.Material.CLAY_BALL) }, 0.25, DropRare.EPIC),
}
package me.reidj.thepit.entity.drop

import me.func.mod.Anime
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class EntityDrop(private val chance: Double, private val itemStack: ItemStack) {

    fun give(player: Player) {
        if (Math.random() > chance) {
            player.inventory.addItem(itemStack)
            Anime.alert(player, "Поздравляем!", "Вам выпал предмет - ${itemStack.itemMeta.displayName}")
        }
    }
}

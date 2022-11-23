package me.reidj.thepit.entity.drop

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class EntityDrop(private val chance: Double, private val itemStack: ItemStack) {

    fun give(player: Player) {
        if (Math.random() < chance) {
            return
        }
        player.inventory.addItem(itemStack)
    }
}

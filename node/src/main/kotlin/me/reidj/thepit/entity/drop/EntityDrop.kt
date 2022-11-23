package me.reidj.thepit.entity.drop

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class EntityDrop(private val chance: Double, private val itemStack: ItemStack) {

    fun give(player: Player): Boolean {
        if (Math.random() < chance) {
            player.inventory.addItem(itemStack)
            return true
        }
        return false
    }
}

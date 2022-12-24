package me.reidj.thepit.entity.drop

import me.func.mod.Anime
import me.reidj.thepit.player.User
import org.bukkit.inventory.ItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class EntityDrop(private val chance: Double, private val itemStack: ItemStack) {

    fun give(user: User) {
        if (Math.random() > chance) {
            user.player.inventory.addItem(itemStack)
            user.createBackpack()
            Anime.alert(user.player, "Поздравляем!", "Вам выпал предмет - ${itemStack.itemMeta.displayName}")
        }
    }
}

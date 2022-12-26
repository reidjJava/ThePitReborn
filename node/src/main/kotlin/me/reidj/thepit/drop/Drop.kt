package me.reidj.thepit.drop

import me.func.mod.Anime
import me.reidj.thepit.player.User
import me.reidj.thepit.util.hasKeyOfType
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class Drop(private val chance: Double, private val itemStack: ItemStack) {

    fun give(user: User) {
        if (chance == 0.0) {
            val nmsItem = CraftItemStack.asNMSCopy(itemStack)
            val tag = nmsItem.tag
            if (nmsItem.hasKeyOfType("uuidBackpack", 8)) {
                tag.setString("uuidBackpack", UUID.randomUUID().toString())
            }
            user.player.inventory.addItem(nmsItem.asBukkitMirror())
        } else {
            if (Math.random() > chance) {
                user.player.inventory.addItem(itemStack)
                Anime.alert(user.player, "Поздравляем!", "Вам выпал предмет - ${itemStack.itemMeta.displayName}")
            }
        }
        user.createBackpack()
    }
}
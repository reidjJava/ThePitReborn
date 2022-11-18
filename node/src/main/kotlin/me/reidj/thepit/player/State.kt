package me.reidj.thepit.player

import dev.implario.bukkit.item.item
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
interface State {

    companion object {
        val backItem: ItemStack by lazy {
            item {
                type(Material.CLAY_BALL)
                text("§cВернуться")
                nbt("other", "cancel")
            }
        }
    }

    fun enterState(user: User)

    fun leaveState(user: User)
}
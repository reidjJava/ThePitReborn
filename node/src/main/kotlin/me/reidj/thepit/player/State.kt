package me.reidj.thepit.player

import dev.implario.bukkit.item.item
import me.reidj.thepit.app
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
                nbt("click", "exit")
            }
        }
    }

    fun enterState(user: User)

    fun playerVisible(): Boolean

    fun leaveState(user: User)

    fun getUsers(): Collection<User> = app.getUsers().filter { it.state == this }
}
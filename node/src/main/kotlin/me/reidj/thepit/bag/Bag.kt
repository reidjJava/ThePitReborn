package me.reidj.thepit.bag

import me.reidj.thepit.app
import me.reidj.thepit.util.itemInMainHand
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class Bag : Listener {

    @EventHandler
    fun PlayerInteractEvent.handle() {
        val user = app.getUser(player) ?: return
        val nmsItem = CraftItemStack.asNMSCopy(player.itemInMainHand())
        val tag = nmsItem.tag
        if (nmsItem.hasTag() && tag.hasKeyOfType("bag", 8)) {
            player.openInventory((user.bagInventory[tag.getUUID("bag")] ?: return).second)
        }
    }
}
package me.reidj.thepit.backpack

import me.reidj.thepit.app
import me.reidj.thepit.util.hasKeyOfType
import me.reidj.thepit.util.itemInMainHand
import me.reidj.thepit.util.rightClick
import me.reidj.thepit.util.setItemInMainHand
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType.CHEST
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class BackpackHandler : Listener {

    @EventHandler
    fun PlayerInteractEvent.handle() {
        val user = app.getUser(player) ?: return
        val nmsItem = CraftItemStack.asNMSCopy(player.itemInMainHand())
        val tag = nmsItem.tag
        if (action.rightClick()) {
            nmsItem.hasKeyOfType("uuidBackpack", 8) {
                player.openInventory(user.backpackInventory[UUID.fromString(tag.getString("uuidBackpack"))])
            }
        }
    }

    @EventHandler
    fun InventoryCloseEvent.handle() {
        if (inventory.type == CHEST) {
            val player = player as Player
            inventory.filterNotNull().forEach {
                val nmsItem = CraftItemStack.asNMSCopy(it)
                if (nmsItem.hasKeyOfType("uuidBackpack", 8)) {
                    player.setItemInMainHand(it)
                    inventory.remove(it)
                }
            }
            val user = app.getUser(player) ?: return
            val nmsItem = CraftItemStack.asNMSCopy(player.itemInMainHand())
            nmsItem.hasKeyOfType("uuidBackpack", 8) {
                nmsItem.tag.setString("items", user.toBase64(inventory))
                player.setItemInMainHand(nmsItem.asBukkitMirror())
            }
        }
    }
}
package me.reidj.thepit.listener

import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.status.MessageStatus
import me.reidj.thepit.app
import me.reidj.thepit.clock.detail.CombatManager
import me.reidj.thepit.dungeon.Dungeon
import me.reidj.thepit.player.prepare.PrepareGuide
import me.reidj.thepit.util.systemMessage
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class ThePitHandler : Listener {

    @EventHandler
    fun InventoryCloseEvent.handle() {
        val inventory = player.inventory
        if (inventory.itemInOffHand != null) {
            player.inventory.addItem(inventory.itemInOffHand)
            inventory.itemInOffHand = null
        }
    }

    @EventHandler
    fun PlayerCommandPreprocessEvent.handle() {
        if (CombatManager.containKey(player.uniqueId)) {
            player.systemMessage(MessageStatus.ERROR, GlowColor.RED, "Вы в ПВП")
            cancel = true
        }
        cancel = (app.getUser(player) ?: return).state is PrepareGuide
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (item == null) {
            return
        }
        val nmsItem = CraftItemStack.asNMSCopy(item)
        val tag = nmsItem.tag ?: return
        if (nmsItem.hasTag() && tag.hasKeyOfType("click", 8)) {
            val user = app.getUser(player) ?: return
            if (user.state is Dungeon) {
                user.state!!.leaveState(user)
                user.setState(null)
            }
        }
    }
}
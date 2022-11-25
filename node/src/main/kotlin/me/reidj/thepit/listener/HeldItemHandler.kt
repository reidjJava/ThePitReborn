package me.reidj.thepit.listener

import me.reidj.thepit.attribute.AttributeType
import me.reidj.thepit.attribute.AttributeUtil
import me.reidj.thepit.player.prepare.PreparePlayerBrain
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerItemHeldEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class HeldItemHandler : Listener {

    @EventHandler
    fun PlayerItemHeldEvent.handle() {
        val armorContents = AttributeUtil.getAllItems(player, player.inventory.getItem(newSlot))
        PreparePlayerBrain.applyAttributes(player, armorContents, AttributeType.HEALTH)
    }

    @EventHandler
    fun InventoryClickEvent.handle() {
        val player = whoClicked as Player
        PreparePlayerBrain.applyAttributes(player, AttributeUtil.getAllItems(player), AttributeType.HEALTH)
    }
}
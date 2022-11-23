package me.reidj.thepit.listener

import me.reidj.thepit.attribute.AttributeType
import me.reidj.thepit.attribute.AttributeUtil.updateAllAttributes
import me.reidj.thepit.player.prepare.PreparePlayerBrain
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class HeldItemHandler : Listener {

    @EventHandler
    fun PlayerItemHeldEvent.handle() {
        val armorContents = player.inventory.armorContents.toMutableList().apply { add(player.inventory.getItem(newSlot)) }
            .toTypedArray()
        updateAllAttributes(player, armorContents)
        PreparePlayerBrain.applyAttributes(player, armorContents, AttributeType.HEALTH)
    }
}
package me.reidj.thepit.listener

import me.reidj.thepit.attribute.AttributeType
import me.reidj.thepit.attribute.AttributeUtil
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityRegainHealthEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class PlayerRegenerationHandler : Listener {

    @EventHandler
    fun EntityRegainHealthEvent.handle() {
        if (entity is Player) {
            val player = entity as Player
            amount += AttributeUtil.getAttributeValue(
                AttributeType.REGENERATION,
                player.inventory.armorContents.toMutableList().apply { add(player.itemInHand) }.toTypedArray()
            )
        }
    }
}
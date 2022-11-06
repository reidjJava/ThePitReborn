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
            amount += AttributeUtil.getAttributeValue(
                AttributeType.REGENERATION.name.lowercase(),
                (entity as Player).inventory.armorContents
            )
        }
    }
}
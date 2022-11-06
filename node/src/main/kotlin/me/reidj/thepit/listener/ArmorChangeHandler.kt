package me.reidj.thepit.listener

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import me.reidj.thepit.attribute.AttributeUtil
import me.reidj.thepit.attribute.AttributeType
import me.reidj.thepit.util.attributeUpdate
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class ArmorChangeHandler : Listener {

    @EventHandler
    fun PlayerArmorChangeEvent.handle() {
        val tags = player.inventory.armorContents.toMutableList()
        AttributeType.values().map { it.name.lowercase() }.forEach {
            val verifiedNewItem = AttributeUtil.getAttributeValue(it, tags)
            if (newItem != null) {
                player.attributeUpdate(it, verifiedNewItem)
            }
        }
    }
}
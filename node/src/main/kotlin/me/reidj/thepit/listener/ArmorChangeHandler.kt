package me.reidj.thepit.listener

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import me.reidj.thepit.attribute.AttributeType
import me.reidj.thepit.attribute.AttributeUtil
import me.reidj.thepit.player.prepare.PreparePlayerBrain
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
        val armorContents = player.inventory.armorContents
        AttributeType.values().map { it.name.lowercase() }.forEach {
            player.attributeUpdate(it, AttributeUtil.getAttributeValue(it, armorContents))
        }
        PreparePlayerBrain.setMaxHealth(
            player,
            AttributeUtil.getAttributeValue(AttributeType.HEALTH.name.lowercase(), armorContents)
        )
    }
}
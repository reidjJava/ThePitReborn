package me.reidj.thepit.listener

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import me.reidj.thepit.attribute.AttributeType
import me.reidj.thepit.attribute.AttributeUtil
import me.reidj.thepit.player.prepare.PreparePlayerBrain
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class ArmorChangeHandler : Listener {

    @EventHandler
    fun PlayerArmorChangeEvent.handle() {
        val armorContents =
            player.inventory.armorContents.toMutableList().apply { add(player.itemInHand) }.toTypedArray()
        AttributeUtil.updateAllAttributes(player)
        PreparePlayerBrain.setMaxHealth(
            player,
            AttributeUtil.getAttributeValue(AttributeType.HEALTH.name.lowercase(), armorContents)
        )
        PreparePlayerBrain.setMoveSpeed(
            player,
            AttributeUtil.getAttributeValue(AttributeType.MOVE_SPEED.name.lowercase(), armorContents)
        )
    }
}
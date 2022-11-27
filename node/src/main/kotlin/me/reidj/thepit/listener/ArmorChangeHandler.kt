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
        PreparePlayerBrain.applyAttributes(
            player,
            AttributeUtil.getAllItems(player),
            AttributeType.HEALTH,
            AttributeType.MOVE_SPEED
        )
    }
}
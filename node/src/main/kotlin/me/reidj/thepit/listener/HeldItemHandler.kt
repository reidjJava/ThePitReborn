package me.reidj.thepit.listener

import me.reidj.thepit.attribute.AttributeUtil.updateAllAttributes
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
        updateAllAttributes(player)
    }
}
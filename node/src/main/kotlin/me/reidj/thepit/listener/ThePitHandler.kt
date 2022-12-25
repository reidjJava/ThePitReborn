package me.reidj.thepit.listener

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.status.MessageStatus
import me.reidj.thepit.app
import me.reidj.thepit.attribute.AttributeType
import me.reidj.thepit.attribute.AttributeUtil
import me.reidj.thepit.client
import me.reidj.thepit.clock.detail.CombatManager
import me.reidj.thepit.player.prepare.PrepareGuide
import me.reidj.thepit.player.prepare.PreparePlayerBrain
import me.reidj.thepit.util.itemInOffHand
import me.reidj.thepit.util.systemMessage
import me.reidj.thepit.util.writeLog
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class ThePitHandler : Listener {

    @EventHandler
    fun InventoryCloseEvent.handle() {
        val player = player as Player
        val inventory = player.inventory
        if (inventory.itemInOffHand != null) {
            player.inventory.addItem(player.itemInOffHand())
            inventory.itemInOffHand = null
        }
    }

    @EventHandler
    fun AsyncPlayerChatEvent.handle() {
        client().writeLog("${player.name} написал в чат >> $message")
    }

    @EventHandler
    fun PlayerMoveEvent.handle() {
        app.eventManager.events["run"]?.on(PlayerMoveEvent::class.java, this)
    }

    @EventHandler
    fun PlayerArmorChangeEvent.handle() {
        PreparePlayerBrain.applyAttributes(
            player,
            AttributeUtil.getAllItems(player),
            AttributeType.HEALTH,
            AttributeType.MOVE_SPEED
        )
    }

    @EventHandler
    fun PlayerCommandPreprocessEvent.handle() {
        client().writeLog("${player.name} использовал команду ${message}.")
        if (CombatManager.containKey(player.uniqueId) && !player.isOp) {
            player.systemMessage(MessageStatus.ERROR, GlowColor.RED, "Вы в ПВП")
            cancel = true
        }
        cancel = (app.getUser(player) ?: return).state is PrepareGuide
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        app.eventManager.events["dragon_egg"]?.on(PlayerInteractEvent::class.java, this)
    }
}
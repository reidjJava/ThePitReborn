package me.reidj.thepit.listener

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityCombustEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class UnusedListener : Listener {

    @EventHandler
    fun LeavesDecayEvent.handle() { isCancelled = true }

    @EventHandler
    fun PlayerDropItemEvent.handle() {
        cancel = !player.isOp
    }

    @EventHandler
    fun BlockRedstoneEvent.handle() {
        newCurrent = oldCurrent
    }

    @EventHandler
    fun EntityExplodeEvent.handle() {
        cancel = entity.type == EntityType.PRIMED_TNT
    }

    @EventHandler
    fun BlockFadeEvent.handle() {
        cancelled = true
    }

    @EventHandler
    fun FoodLevelChangeEvent.handle() {
        cancel = true
    }

    @EventHandler
    fun BlockSpreadEvent.handle() {
        cancelled = true
    }

    @EventHandler
    fun PlayerSwapHandItemsEvent.handle() {
        cancelled = true
    }

    @EventHandler
    fun EntityChangeBlockEvent.handle() {
        cancel = true
    }

    @EventHandler
    fun BlockPlaceEvent.handle() {
        cancel = !player.isOp
    }

    @EventHandler
    fun BlockGrowEvent.handle() {
        cancelled = true
    }

    @EventHandler
    fun BlockPhysicsEvent.handle() {
        cancel = true
    }

    @EventHandler
    fun BlockBreakEvent.handle() {
        cancel = true
    }

    @EventHandler
    fun EntityCombustEvent.handle() {
        cancel = true
    }

    @EventHandler
    fun PrepareItemCraftEvent.handle() {
        inventory.result = null
    }

    @EventHandler
    fun PlayerArmorStandManipulateEvent.handle() {
        cancelled = true
    }

    @EventHandler
    fun PlayerBedEnterEvent.handle() {
        cancel = true
    }

    @EventHandler
    fun HangingBreakByEntityEvent.handle() {
        cancelled = true
    }
}
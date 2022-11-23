package me.reidj.thepit.entity

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class EntityHandler : Listener {

    @EventHandler
    fun EntityDeathEvent.handle() {
        drops.clear()
        droppedExp = 0

        if (!entity.hasMetadata("entity")) {
            return
        }

        val killer = getEntity().killer
        val entityType = EntityType.valueOf(getEntity().getMetadata("entity").toString().lowercase())

        entityType.entity.getDrops().forEach { it.give(killer) }

        EntityUtil.removeEntity(getEntity())
    }

    @EventHandler
    fun EntityDamageEvent.handle() {
        cancelled = entity.hasMetadata("boss") || cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK
    }
}
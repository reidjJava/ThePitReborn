package me.reidj.thepit.entity

import me.func.protocol.data.emoji.Emoji
import me.reidj.thepit.util.worldMessage
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTargetEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class EntityHandler : Listener {

    @EventHandler
    fun EntityTargetEvent.handle() {
        isCancelled = target != null && EntityUtil.targetPlayer[entity.uniqueId] != target.uniqueId
    }

    @EventHandler
    fun EntityDeathEvent.handle() {
        drops.clear()
        droppedExp = 0

        if (!entity.hasMetadata("entity")) {
            return
        }

        val killer = getEntity().killer
        val entityType = EntityType.valueOf(getEntity().getMetadata("entity").component1().asString().uppercase())

        killer.worldMessage(getEntity().location.clone().also { it.y += 2.0 }, "§l+3${Emoji.COIN}")

        entityType.entity.getDrops().forEach { it.give(killer) }

        EntityUtil.removeEntity(getEntity())
    }

    @EventHandler
    fun EntityDamageByEntityEvent.handle() {
        if (getEntity() is LivingEntity && getEntity().hasMetadata("entity") && damager is Player) {
            val entity = getEntity() as LivingEntity
            val entityType = EntityType.valueOf(entity.getMetadata("entity").component1().asString().uppercase())
            val ability = entityType.entity.abilities.find { !it.isOnCoolDown() } ?: return

            ability.onDamage(this)
            ability.lastUsed = System.currentTimeMillis()
        }
    }

    @EventHandler
    fun EntityDamageEvent.handle() {
        cancelled = entity.hasMetadata("boss") || cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK
    }
}
package me.reidj.thepit.entity

import me.func.protocol.data.emoji.Emoji
import me.reidj.thepit.app
import me.reidj.thepit.player.User
import me.reidj.thepit.util.playSound
import me.reidj.thepit.util.worldMessage
import net.minecraft.server.v1_12_R1.EnumParticle
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Creature
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

        entityType.entity.run {
            getDrops().forEach { it.give(killer) }
            abilities.clear()
            getDrops()
        }

        EntityUtil.removeEntity(getEntity(), true)

        playKillSound(killer, killer.location)
    }

    @EventHandler
    fun EntityDamageByEntityEvent.handle() {
        if (getEntity() is LivingEntity && getEntity().hasMetadata("entity") && damager is Player) {
            val entity = getEntity() as LivingEntity
            val damager = damager as Player
            val location = entity.location
            val entityType = EntityType.valueOf(entity.getMetadata("entity").component1().asString().uppercase())

            (entity as Creature).target = damager

            playDamageEffect(
                app.getUser(damager) ?: return,
                entityType.entity.sound,
                location.x,
                location.y,
                location.z
            )

            val ability = entityType.entity.abilities.find { !it.isOnCoolDown() } ?: return

            ability.onDamage(this)
            ability.lastUsed = System.currentTimeMillis()
        }
    }

    @EventHandler
    fun EntityDamageEvent.handle() {
        cancelled = entity.hasMetadata("boss") || cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK
    }

    private fun playKillSound(killer: Player, location: Location) {
        (app.getUser(killer) ?: return).dungeon?.getPartyMemberWithUser()?.forEach {
            it.playSound(
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                location.x,
                location.y,
                location.z
            )
        }
    }

    private fun playDamageEffect(user: User, sound: Sound, x: Double, y: Double, z: Double) {
        user.dungeon?.getPartyMemberWithUser()?.forEach {
            it.playSound(sound, x, y, z)
            it.sendPacket(
                PacketPlayOutWorldParticles(
                    EnumParticle.BLOCK_CRACK,
                    false,
                    x.toFloat(),
                    y.toFloat(),
                    z.toFloat(),
                    0.35f,
                    1.25f,
                    0.35f,
                    0.5f,
                    50,
                    152
                )
            )
        }
    }
}
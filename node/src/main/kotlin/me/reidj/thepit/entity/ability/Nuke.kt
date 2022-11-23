package me.reidj.thepit.entity.ability

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class Nuke : Ability() {

    override fun getCoolDown() = 100L

    override fun onDamage(event: EntityDamageByEntityEvent) {
        val damager = event.getDamager() as Player
        val location = damager.location

        damager.playSound(location, Sound.ENTITY_LIGHTNING_THUNDER, 6F, 2F)
        damager.damage(2.0)
    }
}
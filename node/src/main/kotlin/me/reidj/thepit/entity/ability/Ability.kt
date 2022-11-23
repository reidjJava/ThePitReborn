package me.reidj.thepit.entity.ability

import org.bukkit.event.entity.EntityDamageByEntityEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
abstract class Ability {

    var lastUsed: Long = 0

    abstract fun getCoolDown(): Long

    fun isOnCoolDown() = System.currentTimeMillis() - lastUsed < getCoolDown() * 1000L

    abstract fun onDamage(event: EntityDamageByEntityEvent)
}
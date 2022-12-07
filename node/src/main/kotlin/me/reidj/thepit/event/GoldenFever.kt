package me.reidj.thepit.event

import me.reidj.thepit.app
import org.bukkit.event.entity.PlayerDeathEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class GoldenFever : Event("Золотая лихорадка", "",120, 3600, null, null, mapOf(
    PlayerDeathEvent::class.java to event@{
        val event = it as PlayerDeathEvent
        val user = app.getUser(event.getEntity()) ?: return@event
        val killer = app.getUser(user.killer) ?: return@event

        event.isCancelled = true

        killer.giveMoney(3.0)
    }
))
package me.reidj.thepit.event

import org.bukkit.event.player.PlayerJoinEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class Test : Event("Битва за сало", 7, 15, mapOf(PlayerJoinEvent::class.java to {
    val event = it as PlayerJoinEvent
    event.player.sendMessage("salo")
}))
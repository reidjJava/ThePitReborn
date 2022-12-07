package me.reidj.thepit.event

import clepto.bukkit.B
import me.reidj.thepit.app
import org.bukkit.event.player.PlayerMoveEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class Run : Event("Забег", "", 180, 5400, null, {
    app.getUsers()
        .filter { it.numberOfBlocksPassed > 0 }
        .subList(0, 1)
        .forEach {
            B.bc("Победитель ${it.player.name}")
            it.giveMoney(500.0)
        }
    app.getUsers().forEach { it.numberOfBlocksPassed = 0 }
}, mapOf(PlayerMoveEvent::class.java to event@{
    val event = it as PlayerMoveEvent
    val user = app.getUser(event.player) ?: return@event
    val to = event.to
    val from = event.from

    if (to.blockX != from.blockX || to.blockZ != from.blockZ) {
        user.numberOfBlocksPassed += 1
    }
}))
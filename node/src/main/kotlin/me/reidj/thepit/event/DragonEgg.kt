package me.reidj.thepit.event

import me.reidj.thepit.app
import me.reidj.thepit.util.getBlockAt
import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

private val eggs = app.worldMeta.labels("egg")

class DragonEgg : Event("Яйцо дракона", 300, 1800, {
    eggs.forEach {
        it.getBlockAt().type = Material.DRAGON_EGG
    }
}, {
    eggs.forEach {
        it.getBlockAt().type = Material.AIR
    }
}, mapOf(PlayerInteractEvent::class.java to event@{
    val event = it as PlayerInteractEvent
    val action = event.action
    val blockClicked = event.blockClicked
    val user = app.getUser(event.player) ?: return@event

    if (action == Action.RIGHT_CLICK_BLOCK && blockClicked.type == Material.DRAGON_EGG) {
        blockClicked.type = Material.AIR
        user.giveMoney(150.0)
    }
})
)
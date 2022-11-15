package me.reidj.thepit.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.func.mod.Anime
import me.func.mod.ui.Glow
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.status.MessageStatus
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

private val barrier = ItemStack(Material.BARRIER)

fun coroutine() = CoroutineScope(Dispatchers.IO)

fun Player.errorMessage(subTitle: String) {
    Glow.animate(player, 2.0, GlowColor.RED)
    Anime.itemTitle(player, barrier, "Ошибка", subTitle, 2.0)
    Anime.close(player)
}

fun Player.systemMessage(messageStatus: MessageStatus, color: GlowColor, text: String) {
    Glow.animate(player, 2.0, color)
    Anime.systemMessage(player, messageStatus, text)
}
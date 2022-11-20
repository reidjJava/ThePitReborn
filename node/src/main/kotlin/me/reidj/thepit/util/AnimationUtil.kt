package me.reidj.thepit.util

import me.func.mod.conversation.ModTransfer
import me.func.protocol.data.color.GlowColor
import org.bukkit.entity.Player

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

private val color = GlowColor.RED

fun Player.killBoardMessage(message: String) = ModTransfer(message).send("thepit:killboard", player)

fun Player.attributeUpdate(name: String, value: Double) =
    ModTransfer(name, value).send("thepit:change-attribute", player)

fun sendRank(player: Player, image: String, players: Player) =
    ModTransfer()
        .uuid(player.uniqueId)
        .string("${image.lowercase()}.png")
        .double(player.location.x)
        .double(player.location.y)
        .double(player.location.z)
        .send("thepit:rank", players)

fun Player.timer(text: String, duration: Int, isResetLine: Boolean) = ModTransfer()
    .integer(duration)
    .string(text)
    .boolean(isResetLine)
    .integer(color.red)
    .integer(color.blue)
    .integer(color.green)
    .send("thepit:bar", player)
package me.reidj.thepit.util

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.Player

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

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
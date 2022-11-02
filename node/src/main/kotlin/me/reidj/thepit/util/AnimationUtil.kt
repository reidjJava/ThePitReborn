package me.reidj.thepit.util

import me.func.mod.conversation.ModTransfer
import org.bukkit.entity.Player

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

fun Player.killBoardMessage(message: String) = ModTransfer(message).send("thepit:killboard", player)

fun Player.attributeUpdate(name: String, value: Double) = ModTransfer(name, value).send("thepit:change-attribute", player)
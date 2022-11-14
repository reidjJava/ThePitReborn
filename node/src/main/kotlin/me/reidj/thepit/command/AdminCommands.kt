package me.reidj.thepit.command

import me.func.mod.util.command
import me.reidj.thepit.app
import me.reidj.thepit.player.User
import org.bukkit.Bukkit
import org.bukkit.GameMode
import ru.cristalix.core.formatting.Formatting

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class AdminCommands {

    init {
        regAdminCommand("money") { _, args ->
            (app.getUser(Bukkit.getPlayer(args[0])) ?: return@regAdminCommand).giveMoney(args[1].toDouble())
        }
        regAdminCommand("rankPoints") { _, args ->
            (app.getUser(Bukkit.getPlayer(args[0])) ?: return@regAdminCommand).giveRankingPoints(args[1].toInt())
        }
        regAdminCommand("gm") { user, args ->
            user.player.gameMode = if (args[0].toInt() == 0) GameMode.CREATIVE else GameMode.SURVIVAL
        }
        regAdminCommand("kills") { _, args ->
            (app.getUser(Bukkit.getPlayer(args[0])) ?: return@regAdminCommand).giveKill(args[1].toInt())
        }
        regAdminCommand("death") { _, args ->
            (app.getUser(Bukkit.getPlayer(args[0])) ?: return@regAdminCommand).giveDeath(args[1].toInt())
        }
    }

    private fun regAdminCommand(commandName: String, executor: (User, Array<out String>) -> Unit) {
        command(commandName) { player, args ->
            if (player.isOp || player.uniqueId.toString() in app.playerDataManager.godSet) {
                val user = app.getUser(player) ?: return@command
                executor(user, args)
                player.sendMessage(Formatting.fine("Успешно!"))
            } else {
                player.sendMessage(Formatting.error("Нет прав."))
            }
        }
    }
}
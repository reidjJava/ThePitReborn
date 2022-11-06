package me.reidj.thepit.player.prepare

import me.func.mod.conversation.ModLoader
import me.func.mod.ui.scoreboard.ScoreBoard
import me.reidj.thepit.player.User

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class PrepareMods : Prepare {

    override fun execute(user: User) {
        ModLoader.send("mod-bundle-1.0-SNAPSHOT.jar", user.player)
    }
}
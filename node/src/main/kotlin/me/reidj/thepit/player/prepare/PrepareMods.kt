package me.reidj.thepit.player.prepare

import me.func.mod.conversation.ModLoader
import me.func.mod.ui.scoreboard.ScoreBoard
import me.reidj.thepit.player.User
import ru.cristalix.core.realm.IRealmService

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class PrepareMods : Prepare {

    init {
        ScoreBoard.builder()
            .key("scoreboard")
            .header("ThePit")
            .empty()
            .dynamic("Онлайн") { "§b${IRealmService.get().getOnlineOnRealms("PIT")}" }
            .build()
    }

    override fun execute(user: User) {
        ScoreBoard.subscribe("scoreboard", user.player)
        ModLoader.send("mod-bundle-1.0-SNAPSHOT.jar", user.player)
    }
}
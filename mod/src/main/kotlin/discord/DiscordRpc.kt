package discord

import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class DiscordRpc {

    init {
        mod.registerChannel("thepit:discord:update") {
            UIEngine.clientApi.discordRpc().updateState(readUtf8())
        }
    }
}

import attribute.AttributeManager
import discord.DiscordRpc
import key_press.KeyManager
import mob.SkinManager
import overlay.TimerBar
import player.Healthbar
import player.KillBoard
import player.PlayerManager
import rank.Rank
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

const val NAMESPACE = "cache/animation"

class App : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        Healthbar()
        DiscordRpc()
        PlayerManager()
        TimerBar()
        Rank()
        AttributeManager()
        KillBoard()
        SkinManager()
        KeyManager()
    }
}
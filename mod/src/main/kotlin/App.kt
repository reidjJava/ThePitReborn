
import attribute.AttributeManager
import mob.SkinManager
import overlay.TimerBar
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

        PlayerManager()
        TimerBar()
        Rank()
        AttributeManager()
        KillBoard()
        SkinManager()
    }
}
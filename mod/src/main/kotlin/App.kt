
import attribute.AttributeManager
import dev.xdark.clientapi.entity.EntityPlayer
import dev.xdark.clientapi.event.entity.LivingUpdate
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

        registerHandler<LivingUpdate> {
            if (entity is EntityPlayer) {
                return@registerHandler
            }
            entity.renderTexture
            println(1233)
        }

        PlayerManager()
        TimerBar()
        Rank()
        AttributeManager()
        KillBoard()
    }
}
import attribute.AttributeManager
import player.KillBoard
import player.PlayerManager
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class App : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        PlayerManager()
        AttributeManager()
        KillBoard()
    }
}
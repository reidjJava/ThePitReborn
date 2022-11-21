package player

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.RenderTickPre
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*
import java.text.DecimalFormat

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class PlayerManager {

    private val healthPanel = createPanel(true)
    private val protectionPanel = createPanel(false)

    private val format = DecimalFormat("#,###.##")

    init {
        UIEngine.overlayContext.addChild(healthPanel, protectionPanel)

        mod.registerHandler<GameLoop> {
            healthPanel.enabled = screenCheck()
            protectionPanel.enabled = screenCheck()
        }

        mod.registerHandler<RenderTickPre> {
            val player = UIEngine.clientApi.minecraft().player

            (healthPanel.children[3] as TextElement).content = "§c❤ §f${toFormat(player.health.toDouble())} из ${toFormat(player.maxHealth.toDouble())}"
            (protectionPanel.children[3] as TextElement).content = "㱈 ${toFormat(player.totalArmorValue * 1.0 / 20 * 100)}%"
        }
    }

    private val panelOffsetX = 45.9

    private fun createPanel(isLeft: Boolean) = carved {
        origin = BOTTOM
        align = BOTTOM
        offset = V3(if (isLeft) -panelOffsetX else panelOffsetX, -22.0)
        size = V3(90.0, 13.0)
        color = Color(0, 0, 0, 0.62)
        +text {
            origin = CENTER
            align = CENTER
            shadow = true
            content = "Загрузка..."
        }
    }

    private fun screenCheck(): Boolean {
        val currentScreen = UIEngine.clientApi.minecraft().currentScreen()
        return currentScreen == null || currentScreen::class.java.simpleName == "aV"
    }

    private fun toFormat(double: Double): String = format.format(double)
}
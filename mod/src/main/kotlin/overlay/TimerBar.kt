package overlay

import dev.xdark.clientapi.event.lifecycle.GameLoop
import org.lwjgl.input.Keyboard
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class TimerBar {
    private val content = text {
        origin = TOP
        align = TOP
        color = WHITE
        shadow = true
        content = "Загрузка..."
        offset.y -= 15
    }
    private val line = carved {
        origin = LEFT
        align = LEFT
        size = V3(180.0, 5.0, 0.0)
        color = Color(42, 102, 189, 1.0)
    }
    private val coolDown = carved {
        offset.y += 30
        origin = TOP
        align = TOP
        size = V3(180.0, 5.0, 0.0)
        color = Color(0, 0, 0, 0.62)
        +content
    }

    init {
        mod.registerChannel("thepit:bar") {
            val time = readInt()
            val text = readUtf8() + " " + (time / 60).toString()
                .padStart(2, '0') + ":" + (time % 60).toString().padStart(2, '0') + " ⏳"
            val isResetLine = readBoolean()

            UIEngine.runningAnimations.clear()
            UIEngine.overlayContext.removeChild(coolDown.also { it.removeChild(line) })

            if (time <= 0) {
                return@registerChannel
            }

            line.color = Color(readInt(), readInt(), readInt(), 1.0)

            if (isResetLine) {
                line.size.x = 180.0
            }

            content.content = text

            UIEngine.overlayContext + coolDown.also { it.addChild(line) }

            line.animate(time - 0.1) { size.x = 0.0 }
        }

        mod.registerHandler<GameLoop> {
            coolDown.enabled = !Keyboard.isKeyDown(Keyboard.KEY_TAB)
        }
    }
}
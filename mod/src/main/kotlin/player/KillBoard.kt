package player

import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class KillBoard {

    data class KillBoard(
        val text: String,
        val box: CarvedRectangle = carved {
            val textLines = text.split("\n")
            val maxWidth = textLines.maxOf(UIEngine.clientApi.fontRenderer()::getStringWidth)
            val padding = 5.0

            size = V3(maxWidth + padding * 2, 4.0)
            color = Color(6, 6, 6, 0.5)
            align = BOTTOM
            origin = CENTER
            offset.y -= 70.0

            val textElement = +text {
                content = text
                align = LEFT
                origin = LEFT
                offset.x = padding
                shadow = true
            }
            size.y += textLines.size * textElement.lineHeight
        }
    )

    init {
        mod.registerChannel("thepit:killboard") {
            val text = NetUtil.readUtf8(this)
            val notice = KillBoard(text)
            val box = notice.box

            UIEngine.overlayContext + box

            box.children.reversed().forEach { current ->
                current.animate(1.5, Easings.QUINT_IN) {
                    this.color.alpha = 0.0
                }
            }
            box.animate(1.5, Easings.QUINT_IN) {
                this.color.alpha = 0.0
            }

            UIEngine.schedule(1.5) { UIEngine.overlayContext.removeChild(box) }
        }
    }
}
package attribute

import dev.xdark.clientapi.opengl.GlStateManager
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.LEFT
import ru.cristalix.uiengine.utility.text
import util.Formatter

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class Attribute(
    val isPercentage: Boolean = false,
    val title: String, var value: Double = 0.0, val text: TextElement = text {
        content = "$title: §9${Formatter.toFormat(value) + if (isPercentage) "%" else ""}"
        shadow = true
        origin = LEFT
        align = LEFT
        beforeRender { GlStateManager.disableLighting() }
        afterRender { GlStateManager.enableLighting() }
    }
) {

    fun updateContent(content: String) {
        text.content = text.content.replaceAfter("§9", content) + if (isPercentage) "%" else ""
    }
}

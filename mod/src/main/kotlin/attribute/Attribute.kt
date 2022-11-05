package attribute

import dev.xdark.clientapi.opengl.GlStateManager
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.RIGHT
import ru.cristalix.uiengine.utility.text

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class Attribute(
    val title: String, var value: Double = 0.0, val text: TextElement = text {
        content = "$title §9$value"
        shadow = true
        origin = RIGHT
        align = RIGHT
        beforeRender { GlStateManager.disableLighting() }
        afterRender { GlStateManager.enableLighting() }
    }
) {

    fun updateContent(content: String) {
        text.content = text.content.replaceAfter("§9", content)
    }
}

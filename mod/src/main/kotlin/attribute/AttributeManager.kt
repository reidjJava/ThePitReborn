package attribute

import dev.xdark.clientapi.event.render.GuiOverlayRender
import dev.xdark.clientapi.event.render.ItemTooltipRender
import dev.xdark.clientapi.gui.ingame.InventorySurvivalScreen
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.CENTER
import ru.cristalix.uiengine.utility.TRANSPARENT
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle
import util.Formatter


/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class AttributeManager {

    private val attributes = mutableMapOf(
        "health" to Attribute("§cЗдоровье§7:"),
        "regeneration" to Attribute("§dРегенерация§7:"),
        "damage" to Attribute("§4Урон§7:"),
        "chance_critical_damage" to Attribute("§eШанс критического урона§7:"),
        "critical_damage_strength" to Attribute("§3Сила критического урона§7:"),
        "vampirism" to Attribute("§5Похищение крови§7:"),
        "entity_protection" to Attribute("§aЗащита от сущностей§7:"),
    )

    private val box = rectangle {
        enabled = false
        size = V3(30.0, 100.0)
        origin = CENTER
        align = CENTER
        color = TRANSPARENT
        offset = V3(120.0, -65.0)
    }

    private val margin = 25.0

    init {
        attributes.values.map { it.text }.forEach {
            it.offset.y += 20.0 + margin * box.children.size / 2
            box.addChild(it)
        }

        UIEngine.postOverlayContext + box

        mod.registerHandler<ItemTooltipRender> {
            box.enabled = false
        }

        mod.registerHandler<GuiOverlayRender> {
            box.enabled = UIEngine.clientApi.minecraft().currentScreen() is InventorySurvivalScreen
        }

        mod.registerChannel("thepit:change-attribute") {
            val name = readUtf8()
            val value = readDouble()
            attributes[name]?.updateContent(Formatter.toFormat(value))
        }
    }
}

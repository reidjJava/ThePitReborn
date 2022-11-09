package me.reidj.thepit.item

import dev.implario.bukkit.item.item
import me.func.atlas.Atlas
import org.bukkit.Material

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class ItemManager {

    companion object {
        val items = hashMapOf<String, Item>()
    }

    init {
        Atlas.config(
            "https://raw.githubusercontent.com/reidjJava/ThePit-Items/main/items.yml",
        ).thenAccept { file ->
            println("Loaded! " + file.fileName)

            val configuration = file.configuration

            configuration.getConfigurationSection("items").getKeys(false).forEach {
                val path = "items.$it."
                val attributesPath = "${path}attributes"
                val sharpeningChancePath = "${path}sharpening_chance"
                val sharpeningPricePath = "${path}sharpening_price"
                items[it] = ItemStack(item {
                    type(Material.valueOf(configuration.getString("${path}material")))
                    text(
                        """
                        ${configuration.getString("${path}title")}
                        ${configuration.getList("${path}lore").joinToString("\n")}
                    """.trimIndent()
                    )
                    amount(1)
                    nbt("thepit", configuration.getString("${path}texture"))
                    if (configuration.isDouble(sharpeningChancePath)) {
                        nbt("sharpening_chance", configuration.getDouble(sharpeningChancePath))
                    }
                    if (configuration.isDouble(sharpeningPricePath)) {
                        nbt("sharpening_price", configuration.getDouble(sharpeningPricePath))
                    }
                    if (configuration.isList(attributesPath)) {
                        configuration.getStringList(attributesPath).forEach { attributes ->
                            val attribute = attributes.toString().split(":")
                            nbt(attribute[0], attribute[1] + ":" + attribute[2])
                        }
                    }
                })
            }
        }
    }
}
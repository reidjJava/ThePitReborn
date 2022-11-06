package me.reidj.thepit.item

import dev.implario.bukkit.item.item
import me.func.atlas.Atlas
import org.bukkit.Material
import java.util.*

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

            Atlas.section("items", "list").forEach {
                val path = "list.$it."
                items[it] = ItemStack(it, item {
                    type = Material.valueOf(configuration.getString("${path}material"))
                    text(
                        """
                        ${configuration.getString("${path}title")}
                        ${configuration.getList("${path}lore").joinToString("\n")}
                    """.trimIndent()
                    )
                    amount = 1
                    configuration.getList("${path}attributes").forEach { attributes ->
                        val attribute = attributes.toString().split(":")
                        nbt(attribute[0], attribute[1] + ":" + attribute[2])
                    }
                }, UUID.randomUUID())
            }
        }
    }
}
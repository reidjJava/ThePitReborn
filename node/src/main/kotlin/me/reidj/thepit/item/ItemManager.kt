package me.reidj.thepit.item

import dev.implario.bukkit.item.item
import me.func.atlas.Atlas
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class ItemManager {

    companion object {
        private val items = hashMapOf<String, ItemStack>()

        operator fun get(objectName: String) = items[objectName]!!
    }

    init {
        Atlas.config(
            "https://raw.githubusercontent.com/reidjJava/ThePit-Items/main/items.yml",
        ).thenAccept { file ->
            println("Loaded! " + file.fileName)

            val configuration = file.configuration

            configuration.getConfigurationSection("items").getKeys(false).forEach {
                val path = "items.$it."
                items[it] = item {
                    Artefact(this).init(it)
                    Bag(this).init(it)
                    Equipment(this).init(it)
                    SharpeningStone(this).init(it)
                    Cards(this).init(it)
                    type(Material.valueOf(configuration.getString("${path}material")))
                    text(
                        """
                        ${configuration.getString("${path}title")}
                        ${configuration.getList("${path}lore").joinToString("\n")}
                    """.trimIndent()
                    )
                    amount(1)
                    nbt("HideFlags", 63)
                    nbt("Unbreakable", 1)
                    nbt("thepit", configuration.getString("${path}texture"))
                }
            }
        }
    }
}
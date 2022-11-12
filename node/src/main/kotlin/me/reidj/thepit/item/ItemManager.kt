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
        val items = hashMapOf<String, ItemStack>()
    }

    init {
        Atlas.config(
            "https://raw.githubusercontent.com/reidjJava/ThePit-Items/main/items.yml",
        ).thenAccept { file ->
            println("Loaded! " + file.fileName)

            val configuration = file.configuration


            configuration.getConfigurationSection("items").getKeys(false).forEach {
                val path = "items.$it."
                val sharpeningLevelPath = "${path}sharpeningLevel"
                items[it] = item {
                    Equipment(this).init(it)
                    SharpeningStone(this).init(it)
                    type(Material.valueOf(configuration.getString("${path}material")))
                    text(
                        """
                        ${configuration.getString("${path}title")}
                        ${configuration.getList("${path}lore").joinToString("\n")}
                        
                    """.trimIndent()
                    )
                    amount(1)
                    nbt("thepit", configuration.getString("${path}texture"))
                    if (configuration.isInt(sharpeningLevelPath)) {
                        nbt("sharpeningLevel", configuration.getInt(sharpeningLevelPath))
                    }
                }
            }
        }
    }
}
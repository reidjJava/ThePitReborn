package me.reidj.thepit.listener

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import me.reidj.thepit.attribute.AttributeType
import me.reidj.thepit.util.attributeUpdate
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class ArmorChangeHandler : Listener {

    @EventHandler
    fun PlayerArmorChangeEvent.handle() {
        val tags = player.inventory.armorContents.map { it }.toMutableList()
        AttributeType.values().map { it.name.lowercase() }.forEach {
            val verifiedNewItem = getAttributeValue(it, tags.apply { add(newItem) })
            val verifiedOldItem = getAttributeValue(it, tags.apply { add(oldItem) })
            if (newItem != null) {
                player.attributeUpdate(it, verifiedNewItem)
            } else if (oldItem != null) {
                player.attributeUpdate(it, verifiedOldItem)
            }
        }
    }

    private fun getAttributeValue(objectName: String, items: List<ItemStack>) =
        items.map { CraftItemStack.asNMSCopy(it) }
            .filter { it.hasTag() && it.tag.hasKeyOfType(objectName, 99) }
            .sumOf { it.tag.getDouble(objectName) }

}
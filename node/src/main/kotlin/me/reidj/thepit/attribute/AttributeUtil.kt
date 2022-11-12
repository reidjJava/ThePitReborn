package me.reidj.thepit.attribute

import me.reidj.thepit.util.Formatter
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object AttributeUtil {

    fun generateAttribute(item: ItemStack): ItemStack {
        val nmsItem = CraftItemStack.asNMSCopy(item)
        val tag = nmsItem.tag
        val newLore = mutableListOf<String>()

        for (attribute in AttributeType.values()) {
            val objectName = attribute.name.lowercase()
            if (!tag.hasKey(objectName)) {
                continue
            }
            val pair = tag.getString(objectName).split(":")
            val minimum = pair[0].toDouble()
            val maximum = pair[1].toDouble()
            val result = Random.nextDouble((maximum - minimum) + 1) + minimum
            tag.setDouble(objectName, result)
            newLore.add("${attribute.title}§7: §9${Formatter.toFormat(result)}")
        }

        val itemStack = nmsItem.asBukkitMirror()

        addLoreWithAttribute(itemStack, newLore)

        return itemStack
    }

    fun addLoreWithAttribute(itemStack: ItemStack, newLore: List<String>){
        itemStack.itemMeta = itemStack.itemMeta.apply {
            lore = lore.also { it.addAll(newLore) }
        }
    }

    fun getAttributeValue(objectName: String, items: Array<ItemStack>) =
        items.map { CraftItemStack.asNMSCopy(it) }
            .filter { it.hasTag() && it.tag.hasKeyOfType(objectName, 99) }
            .sumOf { it.tag.getDouble(objectName) }
}
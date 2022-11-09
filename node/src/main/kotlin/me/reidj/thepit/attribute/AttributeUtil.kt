package me.reidj.thepit.attribute

import me.reidj.thepit.item.Item
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object AttributeUtil {

    fun generateAttribute(item: Item): ItemStack {
        val nmsItem = CraftItemStack.asNMSCopy(item.getItem())
        val tag = nmsItem.tag

        for (it in AttributeType.values().map { it.name.lowercase() }) {
            if (!tag.hasKey(it)) {
                continue
            }
            val pair = tag.getString(it).split(":")
            val minimum = pair[0].toDouble()
            val maximum = pair[1].toDouble()
            val result = Random.nextDouble((maximum - minimum) + 1) + minimum
            tag.setDouble(it, result)
        }
        return nmsItem.asBukkitMirror()
    }

    fun getAttributeValue(objectName: String, items: Array<ItemStack>) =
        items.map { CraftItemStack.asNMSCopy(it) }
            .filter { it.hasTag() && it.tag.hasKeyOfType(objectName, 99) }
            .sumOf { it.tag.getDouble(objectName) }
}
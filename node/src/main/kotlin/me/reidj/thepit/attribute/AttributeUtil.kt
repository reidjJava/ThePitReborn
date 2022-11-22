package me.reidj.thepit.attribute

import me.reidj.thepit.item.ItemManager
import me.reidj.thepit.util.Formatter
import me.reidj.thepit.util.attributeUpdate
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
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

        AttributeType.getAttributeWithNbt(tag).forEach {
            val objectName = it.name.lowercase()
            val pair = tag.getString(objectName).split(":")
            val minimum = pair[0].toDouble()
            val maximum = pair[1].toDouble()
            val result = Random.nextDouble((maximum - minimum) + 1) + minimum
            tag.setDouble(objectName, result)
            newLore.add(getTextWithAttribute(it.title, result))
        }

        val itemStack = nmsItem.asBukkitMirror()

        itemStack.itemMeta = itemStack.itemMeta.apply {
            lore = lore.also { it.addAll(newLore) }
        }

        return itemStack
    }

    fun setNewLoreWithAttributes(itemStack: ItemStack) {
        val tag = CraftItemStack.asNMSCopy(itemStack).tag
        itemStack.itemMeta = itemStack.itemMeta.also { meta ->
            meta.lore = ItemManager[tag.getString("address")]?.lore.apply {
                AttributeType.getAttributeWithNbt(tag).forEach {
                    this?.add(getTextWithAttribute(it.title, tag.getDouble(it.getObjectName())))
                }
            }
        }
    }

    fun updateAllAttributes(player: Player) {
        val armorContents =
            player.inventory.armorContents.toMutableList().apply { add(player.itemInHand) }.toTypedArray()
        AttributeType.values().map { it.name.lowercase() }
            .forEach { player.attributeUpdate(it, getAttributeValue(it, armorContents)) }
    }

    fun getAttributeValue(objectName: String, items: Array<ItemStack>) =
        items.map { CraftItemStack.asNMSCopy(it) }
            .filter { it.hasTag() && it.tag.hasKeyOfType(objectName, 99) }
            .sumOf { it.tag.getDouble(objectName) }

    private fun getTextWithAttribute(title: String, value: Double) = "$title§7: §9${Formatter.toFormat(value)}"
}
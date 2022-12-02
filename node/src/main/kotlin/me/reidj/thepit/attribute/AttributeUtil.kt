package me.reidj.thepit.attribute

import me.reidj.thepit.item.ItemManager
import me.reidj.thepit.util.Formatter
import me.reidj.thepit.util.attributeUpdate
import me.reidj.thepit.util.itemInMainHand
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
            val result = if (maximum == minimum) maximum else Random.nextDouble(minimum, maximum)
            tag.setDouble(objectName, result)
            newLore.add(getTextWithAttribute(it.title, result, it.isPercentage))
        }

        val itemStack = nmsItem.asBukkitMirror()

        itemStack.itemMeta = itemStack.itemMeta.also { meta ->
            meta.lore = meta.lore.apply {
                if (tag.hasKeyOfType("sharpeningLevel", 99)) {
                    add("")
                }
                addAll(newLore)
            }
        }

        newLore.clear()

        return itemStack
    }

    fun setNewLoreWithAttributes(itemStack: ItemStack) {
        val tag = CraftItemStack.asNMSCopy(itemStack).tag
        itemStack.itemMeta = itemStack.itemMeta.also { meta ->
            meta.lore = ItemManager[tag.getString("address")].lore.apply {
                this?.add("")
                AttributeType.getAttributeWithNbt(tag).forEach {
                    this?.add(getTextWithAttribute(it.title, tag.getDouble(it.getObjectName()), it.isPercentage))
                }
            }
        }
    }

    fun updateAllAttributes(player: Player, armorContents: Array<ItemStack>) {
        AttributeType.values()
            .forEach { player.attributeUpdate(it.getObjectName(), getAttributeValue(it, armorContents)) }
    }

    fun getAttributeValue(type: AttributeType, items: Array<ItemStack>) =
        items.map { CraftItemStack.asNMSCopy(it) }
            .filter { it.hasTag() && it.tag.hasKeyOfType(type.getObjectName(), 99) }
            .sumOf { it.tag.getDouble(type.getObjectName()) }

    fun getAllItems(player: Player, current: ItemStack?): Array<ItemStack> {
        val inventory = player.inventory.armorContents.toMutableList()
        if (current != null) {
            inventory.add(current)
        }
        return inventory.toTypedArray()
    }

    fun getAllItems(player: Player) = getAllItems(player, player.itemInMainHand())

    private fun getTextWithAttribute(title: String, value: Double, isPercentage: Boolean) =
        "$title: ${Formatter.toFormat(value)}${if (isPercentage) "%" else ""}"
}
package me.reidj.thepit.attribute

import me.reidj.thepit.item.Item
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import kotlin.random.Random

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object AttributeGenerator {

    fun generateAttribute(item: Item): org.bukkit.inventory.ItemStack {
        val nmsItem = CraftItemStack.asNMSCopy(item.getItem())
        val tag = nmsItem.tag

        for (it in AttributeType.values().map { it.name.lowercase() }) {
            if (!tag.hasKey(it)) {
                continue
            }
            val pair = tag.getString(it).split(":")
            val minimum = pair[0].toDouble()
            val maximum = pair[1].toDouble()
            val result = Random.nextDouble(minimum, maximum)
            tag.setDouble(it, result)
        }
        return nmsItem.asBukkitMirror()
    }
}
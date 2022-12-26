package me.reidj.thepit.item

import dev.implario.bukkit.item.ItemBuilder

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class Bag(override val itemBuilder: ItemBuilder) : Item() {

    override fun init(objectName: String) {
        val sizePath = "${path}$objectName.size"
        val itemsPath = "${path}$objectName.items"
        if (configuration.isString("${path}$objectName.uuidBackpack")) {
            itemBuilder.nbt("uuidBackpack", "")
        }
        if (configuration.isInt(sizePath)) {
            itemBuilder.nbt("size", configuration.getInt(sizePath))
        }
        if (configuration.isString(itemsPath)) {
            itemBuilder.nbt("items", "")
        }
    }
}
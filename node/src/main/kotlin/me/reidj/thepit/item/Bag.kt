package me.reidj.thepit.item

import dev.implario.bukkit.item.ItemBuilder

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class Bag(override val itemBuilder: ItemBuilder) : Item() {

    override fun init(objectName: String) {
        val bagPath = "${path}$objectName.bag"
        if (configuration.isString(bagPath)) {
            itemBuilder.nbt("bag", configuration.getString(bagPath))
        }
    }
}
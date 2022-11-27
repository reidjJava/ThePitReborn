package me.reidj.thepit.item

import dev.implario.bukkit.item.ItemBuilder

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class Cards(override val itemBuilder: ItemBuilder) : Item() {

    override fun init(objectName: String) {
        val dungeonPath = "${path}$objectName.dungeon"
        if (configuration.isString(dungeonPath)) {
            itemBuilder.nbt("dungeon", configuration.getString(dungeonPath))
        }
    }
}
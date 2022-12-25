package me.reidj.thepit.item

import dev.implario.bukkit.item.ItemBuilder

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class Equipment(override val itemBuilder: ItemBuilder) : Item() {

    override fun init(objectName: String) {
        val attributesPath = "${path}$objectName.attributes"
        val sharpeningLevelPath = "${path}$objectName.sharpeningLevel"
        if (configuration.isList(attributesPath)) {
            itemBuilder.nbt("address", objectName)
            configuration.getStringList(attributesPath).forEach { attributes ->
                val attribute = attributes.toString().split(":")
                itemBuilder.nbt(attribute[0], attribute[1] + ":" + attribute[2])
            }
        }
        if (configuration.isInt(sharpeningLevelPath)) {
            itemBuilder.nbt("sharpeningLevel", configuration.getInt(sharpeningLevelPath))
        }
    }
}
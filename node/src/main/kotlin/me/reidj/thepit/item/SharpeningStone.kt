package me.reidj.thepit.item

import dev.implario.bukkit.item.ItemBuilder

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class SharpeningStone(override val itemBuilder: ItemBuilder) : Item() {

    override fun init(objectName: String) {
        val path = "${path}$objectName."
        val sharpeningChancePath = "${path}sharpening_chance"
        val sharpeningPricePath = "${path}sharpening_price"
        if (configuration.isDouble(sharpeningChancePath)) {
            itemBuilder.nbt("sharpening_chance", configuration.getDouble(sharpeningChancePath))
        }
        if (configuration.isDouble(sharpeningPricePath)) {
            itemBuilder.nbt("sharpening_price", configuration.getDouble(sharpeningPricePath))
        }
    }
}
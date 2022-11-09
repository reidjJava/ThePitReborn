package me.reidj.thepit.item

import org.bukkit.inventory.ItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class ItemStack(private val item: ItemStack) : Item {

    override fun getItem() = item
}
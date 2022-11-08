package me.reidj.thepit.item

import org.bukkit.inventory.ItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class ItemStack(private val address: String, private val item: ItemStack) : Item {

    override fun getAddress() = address

    override fun getItem() = item
}
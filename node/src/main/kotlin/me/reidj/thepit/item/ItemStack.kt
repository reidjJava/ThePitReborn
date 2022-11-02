package me.reidj.thepit.item

import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class ItemStack(private val address: String, private val item: ItemStack, private val uuid: UUID) : Item {

    override fun getAddress() = address

    override fun getItem() = item

    override fun getUUID() = uuid
}
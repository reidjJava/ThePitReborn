package me.reidj.thepit.item

import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
interface Item {

    fun getAddress(): String

    fun getItem(): ItemStack
}
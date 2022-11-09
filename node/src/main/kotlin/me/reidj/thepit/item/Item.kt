package me.reidj.thepit.item

import dev.implario.bukkit.item.ItemBuilder
import me.func.atlas.Atlas

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
abstract class Item {

    val configuration = Atlas.find("items")

    val path = "items."

    abstract val itemBuilder: ItemBuilder

    abstract fun init(objectName: String)
}
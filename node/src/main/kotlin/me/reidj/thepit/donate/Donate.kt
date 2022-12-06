package me.reidj.thepit.donate

import me.reidj.thepit.data.Stat
import me.reidj.thepit.player.User

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
interface Donate {

    fun getTitle(): String

    fun getDescription(): String

    fun getTexture(): String

    fun getObjectName(): String

    fun getPrice(): Long

    fun give(user: User)

    fun getCurrent(stat: Stat): Boolean

    fun setCurrent(stat: Stat)
}
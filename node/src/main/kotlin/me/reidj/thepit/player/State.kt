package me.reidj.thepit.player

import me.reidj.thepit.app
import me.reidj.thepit.item.ItemManager

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
interface State {

    companion object {
        val backItem = ItemManager["RUNE"]
    }

    fun enterState(user: User)

    fun playerVisible(): Boolean

    fun leaveState(user: User)

    fun getUsers(): Collection<User> = app.getUsers().filter { it.state == this }
}
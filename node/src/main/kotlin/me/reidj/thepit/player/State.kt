package me.reidj.thepit.player

import me.reidj.thepit.app

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
interface State {

    fun enterState(user: User)

    fun playerVisible(): Boolean

    fun leaveState(user: User)

    fun getUsers(): Collection<User> = app.getUsers().filter { it.state == this }
}
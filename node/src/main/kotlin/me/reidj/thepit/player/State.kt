package me.reidj.thepit.player

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
interface State {

    fun enterState(user: User)

    fun playerVisible(): Boolean

    fun leaveState(user: User)
}
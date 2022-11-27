package me.reidj.thepit.player

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class DefaultState : State {

    override fun enterState(user: User) {
    }

    override fun playerVisible() = true

    override fun leaveState(user: User) {
    }
}
package me.reidj.thepit.player

import me.reidj.thepit.util.discordRpcUpdate

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class DefaultState : State {

    override fun enterState(user: User) {
        user.player.discordRpcUpdate("В мире (ThePit)")
    }

    override fun playerVisible() = true

    override fun leaveState(user: User) {
    }
}
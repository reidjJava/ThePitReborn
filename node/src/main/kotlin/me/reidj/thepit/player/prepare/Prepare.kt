package me.reidj.thepit.player.prepare

import me.reidj.thepit.player.User

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

@FunctionalInterface
interface Prepare {

    fun execute(user: User)
}
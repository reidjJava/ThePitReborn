package me.reidj.thepit.dungeon

import me.func.mod.Anime
import me.reidj.thepit.entity.EntityUtil
import me.reidj.thepit.player.State
import me.reidj.thepit.player.User
import me.reidj.thepit.player.prepare.PreparePlayerBrain

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class Dungeon : State {

    override fun enterState(user: User) {
        val player = user.player

        player.inventory.setItem(9, State.backItem)

        EntityUtil.generateEntities(user)

        Anime.topMessage(user.player, "Вы вошли в подземелье")
        user.dungeon.teleport(user.player)
    }

    override fun leaveState(user: User) {
        Anime.topMessage(user.player, "Вы покинули подземелье")
        PreparePlayerBrain.spawnTeleport(user.player)
    }
}
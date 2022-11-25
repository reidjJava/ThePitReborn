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

        player.inventory.setItem(8, State.backItem)

        EntityUtil.spawn(user)

        Anime.topMessage(user.player, "Вы вошли в подземелье")

        user.dungeon?.teleport(player)
    }

    override fun playerVisible() = false

    override fun leaveState(user: User) {
        val player = user.player

        EntityUtil.clearEntities(user)

        user.dungeon = null

        player.inventory.remove(State.backItem)

        Anime.topMessage(player, "Вы покинули подземелье")
        PreparePlayerBrain.spawnTeleport(player)
    }
}
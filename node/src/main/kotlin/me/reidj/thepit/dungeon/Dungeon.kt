package me.reidj.thepit.dungeon

import me.func.mod.Anime
import me.func.protocol.math.Position
import me.reidj.thepit.app
import me.reidj.thepit.client
import me.reidj.thepit.entity.EntityUtil
import me.reidj.thepit.player.State
import me.reidj.thepit.player.User
import me.reidj.thepit.player.prepare.PreparePlayerBrain
import me.reidj.thepit.util.discordRpcUpdate
import me.reidj.thepit.util.writeLog

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class Dungeon : State {

    override fun enterState(user: User) {
        val player = user.player
        val dungeon = user.dungeon!!
        val dungeonTitle = dungeon.type.title

        EntityUtil.spawn(user)

        Anime.overlayText(player, Position.BOTTOM_RIGHT, "Покинуть подземелье §e[O]")
        Anime.topMessage(user.player, "Вы вошли в подземелье")

        dungeon.teleport(player)
        user.stat.numberVisitsToDungeon++

        player.discordRpcUpdate("Проходит подземелье: $dungeonTitle(ThePit)")

        client().writeLog("${player.name} вошёл в подземелье $dungeonTitle.")
    }

    override fun playerVisible() = false

    override fun leaveState(user: User) {
        val player = user.player
        val dungeon = user.dungeon!!

        if (dungeon.party.isEmpty() || dungeon.party.size == 1) {
            EntityUtil.clearEntities(user, true)
        } else {
            dungeon.party
                .mapNotNull { app.getUser(it) }
                .forEach { it.dungeon?.party?.remove(player.uniqueId) }
            EntityUtil.clearEntities(user, dungeon.party.isEmpty())
        }

        Anime.overlayText(player, Position.BOTTOM_RIGHT, "")

        Anime.topMessage(player, "Вы покинули подземелье")
        PreparePlayerBrain.spawnTeleport(player)

        client().writeLog("${player.name} покинул подземелье ${dungeon.type.title}.")

        user.dungeon = null
    }
}
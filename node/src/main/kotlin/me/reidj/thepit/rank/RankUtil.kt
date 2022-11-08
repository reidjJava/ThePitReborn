package me.reidj.thepit.rank

import me.func.mod.conversation.ModTransfer
import me.reidj.thepit.app
import me.reidj.thepit.player.User
import me.reidj.thepit.util.sendRank
import org.bukkit.Bukkit
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object RankUtil {

    fun createRank(user: User) {
        val rank = getRank(user.stat.rankingPoints)
        if (rank == RankType.NONE) return
        Bukkit.getOnlinePlayers().forEach { sendRank(user.player, rank.name, it) }
    }

    fun showAll(user: User) {
        Bukkit.getOnlinePlayers().mapNotNull { app.getUser(it) }.forEach {
            val rank = getRank(it.stat.rankingPoints)
            if (rank == RankType.NONE) return
            sendRank(it.player, rank.name, user.player)
        }
    }

    fun updateRank(user: User) {
        val rank = getRank(user.stat.rankingPoints)
        if (rank == RankType.NONE) return
        Bukkit.getOnlinePlayers().forEach {
            remove(it.uniqueId)
            sendRank(user.player, rank.name, it)
        }
    }

    fun remove(uuid: UUID) = ModTransfer(uuid.toString()).send("thepit:rank-remove", Bukkit.getOnlinePlayers())

    fun getRank(points: Int) = RankType.values().first { points in it.points }
}
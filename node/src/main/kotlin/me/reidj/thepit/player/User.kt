package me.reidj.thepit.player

import me.func.mod.Anime
import me.reidj.thepit.data.Stat
import me.reidj.thepit.rank.RankUtil
import org.bukkit.entity.Player

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class User(stat: Stat) {

    var stat: Stat

    lateinit var killer: Player
    lateinit var player: Player

    init {
        this.stat = stat
    }

    fun giveMoney(money: Double) { stat.money += money }

    fun giveKill(kill: Int) { stat.kills += kill }

    fun giveDeath(death: Int) { stat.deaths += death }

    fun giveRankingPoints(points: Int) {
        val prevRankingPoints = stat.rankingPoints
        val prevRank = RankUtil.getRank(prevRankingPoints)

        stat.rankingPoints += points

        val rank = RankUtil.getRank(stat.rankingPoints)

        if (rank.ordinal > prevRank.ordinal) {
            rank.reward(this)
            RankUtil.updateRank(this)
            Anime.alert(player, "Поздравляем!", "Вы ранг был повышен\n${prevRank.title} §f➠§l ${rank.title}")
        }
    }
}
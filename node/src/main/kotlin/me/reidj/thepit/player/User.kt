package me.reidj.thepit.player

import me.reidj.thepit.data.Stat
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

    fun isKillerInitialized() = ::killer.isInitialized
}
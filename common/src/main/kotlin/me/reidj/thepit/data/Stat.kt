package me.reidj.thepit.data

import me.reidj.thepit.contract.Contract
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class Stat(
    val uuid: UUID,
    var money: Double,
    var kills: Int,
    var deaths: Int,
    var rankingPoints: Int,
    var rewardStreak: Int,
    var numberVisitsToDungeon: Int,
    var contractLastUpdate: Long,
    var lastEnter: Long,
    var dailyClaimTimestamp: Long,
    var contractsTypes: Set<Contract>,
    val donates: Set<String>,
    var bags: List<Bag>,
    var playerInventory: String,
    var playerEnderChest: String,
    var isTutorialCompleted: Boolean,
): Unique {
    override fun getUUID() = uuid
}

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
    var contractLastUpdate: Long,
    var contractsTypes: Set<Contract>,
    var playerInventory: String,
    var playerEnderChest: String,
)

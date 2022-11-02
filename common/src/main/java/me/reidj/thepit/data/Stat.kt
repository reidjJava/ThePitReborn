package me.reidj.thepit.data

import me.reidj.thepit.contract.Contract
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class Stat(
    var uuid: UUID,
    var money: Double,
    var kills: Int,
    var deaths: Int,
    var contractLastUpdate: Long,
    var contractsTypes: Set<Contract>,
)

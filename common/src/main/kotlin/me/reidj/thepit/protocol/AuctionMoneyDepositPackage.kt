package me.reidj.thepit.protocol

import ru.cristalix.core.network.CorePackage
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class AuctionMoneyDepositPackage(
    val seller: UUID,
    val data: UUID,
    val customerName: String,
    val itemName: String,
    val money: Int
) : CorePackage()
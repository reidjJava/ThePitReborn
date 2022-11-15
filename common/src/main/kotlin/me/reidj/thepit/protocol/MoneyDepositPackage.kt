package me.reidj.thepit.protocol

import ru.cristalix.core.network.CorePackage
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class MoneyDepositPackage(
    val seller: UUID,
    val data: UUID,
    val customer: String,
    val displayName: String,
    val money: Int
    ): CorePackage()

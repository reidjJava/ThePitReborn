package me.reidj.thepit.data

import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class AuctionData(
    val uuid: UUID,
    val seller: UUID,
    val item: String,
    val sellerName: String,
    val price: Int,
    val beganTime: Long
): Unique {
    override fun getUUID() = uuid
}

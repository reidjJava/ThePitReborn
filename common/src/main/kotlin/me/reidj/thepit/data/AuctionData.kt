package me.reidj.thepit.data

import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class AuctionData(val uuid: UUID, val objectName: String, val sellerName: String, val seller: UUID, val price: Int)

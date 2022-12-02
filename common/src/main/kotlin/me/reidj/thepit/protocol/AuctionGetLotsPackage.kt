package me.reidj.thepit.protocol

import me.reidj.thepit.data.AuctionData
import ru.cristalix.core.network.CorePackage

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class AuctionGetLotsPackage : CorePackage() {
    lateinit var auctionData: List<AuctionData>
}

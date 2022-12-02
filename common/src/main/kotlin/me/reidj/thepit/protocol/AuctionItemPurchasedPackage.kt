package me.reidj.thepit.protocol

import ru.cristalix.core.network.CorePackage
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
data class AuctionItemPurchasedPackage(val uuid: UUID): CorePackage() {
    var isBought = false
}

package me.reidj.reborn

import me.reidj.thepit.data.AuctionData

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class AuctionAdapter(dbUrl: String, dbName: String, collection: String): MongoAdapter<AuctionData>(dbUrl, collection, dbName)
package me.reidj.reborn

import kotlinx.coroutines.runBlocking
import me.reidj.thepit.data.AuctionData
import me.reidj.thepit.data.Stat
import me.reidj.thepit.protocol.*
import ru.cristalix.core.CoreApi
import ru.cristalix.core.microservice.MicroServicePlatform
import ru.cristalix.core.microservice.MicroserviceBootstrap
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.permissions.IPermissionService
import ru.cristalix.core.permissions.PermissionService
import java.util.*

fun main() {
    MicroserviceBootstrap.bootstrap(MicroServicePlatform(4))

    val mongoAdapter = UserAdapter(System.getenv("db_url"), System.getenv("db_data"), "test")
    val auctionAdapter = AuctionAdapter(System.getenv("db_url"), System.getenv("db_data"), "auction")

    ISocketClient.get().run {
        capabilities(
            LoadUserPackage::class,
            BulkSaveUserPackage::class,
            SaveUserPackage::class,
            TopPackage::class,
            AuctionPutLotPackage::class,
            AuctionMoneyDepositPackage::class,
            AuctionGetLotsPackage::class,
            AuctionItemPurchasedPackage::class
        )

        CoreApi.get().registerService(IPermissionService::class.java, PermissionService(this))

        addListener(LoadUserPackage::class.java) { realmId, pckg ->
            mongoAdapter.find<Stat>(pckg.uuid).get().also {
                pckg.stat = it
                forward(realmId, pckg)
                println("Loaded on ${realmId.realmName}! Player: ${pckg.uuid}")
            }
        }
        addListener(SaveUserPackage::class.java) { realmId, pckg ->
            mongoAdapter.save(pckg.stat)
            println("Received SaveUserPackage from ${realmId.realmName} for ${pckg.uuid}")

        }
        addListener(BulkSaveUserPackage::class.java) { realmId, pckg ->
            mongoAdapter.save(pckg.packages.map { it.stat })
            println("Received BulkSaveUserPackage from ${realmId.realmName}")
        }
        addListener(TopPackage::class.java) { realmId, pckg ->
            val top = mongoAdapter.getTop(pckg.topType, pckg.limit)
            pckg.entries = top
            forward(realmId, pckg)
            println("Top generated for ${realmId.realmName}")
        }
        addListener(AuctionPutLotPackage::class.java) { _, pckg -> auctionAdapter.save(pckg.auctionData) }
        addListener(AuctionGetLotsPackage::class.java) { realmId, pckg ->
            auctionAdapter.findAll<AuctionData>().get().also {
                pckg.auctionData = it.values.toList()
                forward(realmId, pckg)
            }
        }
        addListener(AuctionItemPurchasedPackage::class.java) { realmId, pckg ->
            auctionAdapter.find<AuctionData>(pckg.uuid).get().also {
                pckg.isBought = it == null
                auctionAdapter.clear(pckg.uuid)
                forward(realmId, pckg)
            }
        }
        addListener(AuctionRemoveItemPackage::class.java) { _, pckg ->
            auctionAdapter.clear(pckg.uuid)
        }
        addListener(AuctionMoneyDepositPackage::class.java) { _, pckg -> write(pckg) }
    }

    runBlocking {
        val command = readLine()
        if (command!!.startsWith("delete")) {
            val args = command.split(" ")
            val uuid = UUID.fromString(args[1])
            mongoAdapter.clear(uuid)
            println("Removed $uuid's data from db...")
        }
    }
}
package me.reidj.reborn

import kotlinx.coroutines.runBlocking
import me.reidj.thepit.protocol.BulkSaveUserPackage
import me.reidj.thepit.protocol.LoadUserPackage
import me.reidj.thepit.protocol.SaveUserPackage
import ru.cristalix.core.CoreApi
import ru.cristalix.core.microservice.MicroServicePlatform
import ru.cristalix.core.microservice.MicroserviceBootstrap
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.permissions.IPermissionService
import ru.cristalix.core.permissions.PermissionService
import java.util.*

fun main() {
    MicroserviceBootstrap.bootstrap(MicroServicePlatform(4))

    val mongoAdapter = MongoAdapter(System.getenv("db_url"), System.getenv("db_data"), "test")

    ISocketClient.get().run {
        capabilities(
            LoadUserPackage::class,
            BulkSaveUserPackage::class,
            SaveUserPackage::class
        )

        CoreApi.get().registerService(IPermissionService::class.java, PermissionService(this))

        addListener(LoadUserPackage::class.java) { realmId, pckg ->
            mongoAdapter.find(pckg.uuid).get().run {
                pckg.stat = this
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
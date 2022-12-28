package me.reidj.discord

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.reidj.thepit.protocol.LogPackage
import ru.cristalix.core.microservice.MicroServicePlatform
import ru.cristalix.core.microservice.MicroserviceBootstrap
import ru.cristalix.core.network.Capability
import ru.cristalix.core.network.ISocketClient
import java.text.SimpleDateFormat
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

private const val TOKEN = "MTA1NjY0NTcxNTYyMDc5NDQ1OQ.GN1VPZ.tfE6ekREwUrQpePmx8dp4OobpHUpe7fGIz6sEs"
private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

suspend fun main() {
    val kord = Kord(TOKEN)
    val rest = RestClient(TOKEN)
    val scope = CoroutineScope(Dispatchers.IO)

    MicroserviceBootstrap.bootstrap(MicroServicePlatform(2))

    ISocketClient.get().also {
        it.registerCapabilities(
            Capability.builder()
                .className(LogPackage::class.java.name)
                .notification(true)
                .build()
        )

        it.addListener(LogPackage::class.java) { realm, pckg ->
            scope.launch {
                rest.channel.createMessage(Snowflake(968266478363213834)) {
                    content = "${dateFormat.format(Date())} | ${realm.realmName} | ${pckg.message}"
                }
            }
        }
    }

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents += Intents.all
    }
}
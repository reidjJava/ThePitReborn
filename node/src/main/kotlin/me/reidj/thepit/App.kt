package me.reidj.thepit

import dev.implario.bukkit.platform.Platforms
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import me.func.mod.Anime
import me.func.mod.Kit
import me.func.mod.conversation.ModLoader
import me.func.mod.util.listener
import me.func.world.MapLoader
import me.func.world.WorldMeta
import me.reidj.thepit.clock.GameTimer
import me.reidj.thepit.contract.ContractManager
import me.reidj.thepit.item.ItemManager
import me.reidj.thepit.listener.ArmorChangeHandler
import me.reidj.thepit.listener.DamageHandler
import me.reidj.thepit.listener.PlayerRegenerationHandler
import me.reidj.thepit.listener.UnusedListener
import me.reidj.thepit.player.PlayerDataManager
import me.reidj.thepit.player.User
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmStatus
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

lateinit var app: App

class App : JavaPlugin() {

    lateinit var playerDataManager: PlayerDataManager
    lateinit var worldMeta: WorldMeta

    override fun onEnable() {
        app = this

        Platforms.set(PlatformDarkPaper())

        Anime.include(Kit.NPC, Kit.EXPERIMENTAL, Kit.STANDARD, Kit.HEALTH_BAR)

        ModLoader.loadAll("mods")

        IRealmService.get().currentRealmInfo.run {
            status = RealmStatus.WAITING_FOR_PLAYERS
            isLobbyServer = true
            readableName = "ThePitReborn"
            groupName = "ThePitReborn"
        }

        worldMeta = MapLoader.load("POTH", "DragonsLore")

        playerDataManager = PlayerDataManager()

        ContractManager()
        ItemManager()

        listener(playerDataManager, DamageHandler(), ArmorChangeHandler(), UnusedListener(), PlayerRegenerationHandler())

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, GameTimer(listOf()), 0, 1)
    }

    fun getUser(player: Player): User? = getUser(player.uniqueId)

    fun getUser(uuid: UUID): User? = playerDataManager.userMap[uuid]
}

fun client() = ISocketClient.get()
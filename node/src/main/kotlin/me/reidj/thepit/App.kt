package me.reidj.thepit

import dev.implario.bukkit.platform.Platforms
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import kotlinx.coroutines.runBlocking
import me.func.mod.Anime
import me.func.mod.Kit
import me.func.mod.conversation.ModLoader
import me.func.mod.util.command
import me.func.mod.util.listener
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.status.MessageStatus
import me.func.sound.Category
import me.func.sound.Music
import me.func.world.MapLoader
import me.func.world.WorldMeta
import me.reidj.thepit.attribute.AttributeUtil
import me.reidj.thepit.auction.AuctionManager
import me.reidj.thepit.clock.GameTimer
import me.reidj.thepit.clock.detail.TopManager
import me.reidj.thepit.command.AdminCommands
import me.reidj.thepit.consumable.ConsumableManager
import me.reidj.thepit.contract.ContractManager
import me.reidj.thepit.item.ItemManager
import me.reidj.thepit.listener.ArmorChangeHandler
import me.reidj.thepit.listener.DamageHandler
import me.reidj.thepit.listener.PlayerRegenerationHandler
import me.reidj.thepit.listener.UnusedListener
import me.reidj.thepit.player.PlayerDataManager
import me.reidj.thepit.player.User
import me.reidj.thepit.protocol.AuctionPutLotPackage
import me.reidj.thepit.protocol.AuctionRemoveItemPackage
import me.reidj.thepit.protocol.MoneyDepositPackage
import me.reidj.thepit.sharpening.SharpeningManager
import me.reidj.thepit.util.systemMessage
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

        client().addListener(AuctionPutLotPackage::class.java) { _, pckg -> AuctionManager[pckg.auctionData] }
        client().addListener(MoneyDepositPackage::class.java) { _, pckg ->
            AuctionManager.remove(pckg.data)

            val user = getUser(pckg.seller) ?: return@addListener

            user.player.systemMessage(
                MessageStatus.FINE,
                GlowColor.GREEN,
                "Ваш лот: ${pckg.displayName} §fбыл куплен игроком §b${pckg.customer}§f."
            )

            user.stat.auctionData.removeIf { it.uuid == pckg.data }
            user.giveMoney(pckg.money.toDouble())
        }
        client().addListener(AuctionRemoveItemPackage::class.java) { _, pckg -> AuctionManager.remove(pckg.uuid) }

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
        SharpeningManager()
        AuctionManager()

        AdminCommands()

        Music.block(Category.VOICE).block(Category.PLAYERS)

        listener(
            playerDataManager,
            DamageHandler(),
            ArmorChangeHandler(),
            UnusedListener(),
            PlayerRegenerationHandler(),
            ConsumableManager()
        )

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, GameTimer(listOf(TopManager())), 0, 1)

        command("item") { player, args ->
            player.inventory.addItem(AttributeUtil.generateAttribute(ItemManager[args[0]]!!))
        }
    }

    override fun onDisable() {
        runBlocking { client().write(playerDataManager.bulkSave(true)) }
        Thread.sleep(1000)
    }

    fun getUser(player: Player): User? = getUser(player.uniqueId)

    fun getUser(uuid: UUID): User? = playerDataManager.userMap[uuid]
}

fun client(): ISocketClient = ISocketClient.get()
package me.reidj.thepit

import clepto.bukkit.B
import dev.implario.bukkit.platform.Platforms
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import me.func.Lock
import me.func.mod.Anime
import me.func.mod.Kit.*
import me.func.mod.conversation.ModLoader
import me.func.mod.util.listener
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.status.MessageStatus
import me.func.world.MapLoader
import me.func.world.WorldMeta
import me.reidj.thepit.auction.AuctionManager
import me.reidj.thepit.bag.Bag
import me.reidj.thepit.barter.BarterManager
import me.reidj.thepit.clock.GameTimer
import me.reidj.thepit.clock.detail.CombatManager
import me.reidj.thepit.clock.detail.PlayerRegeneration
import me.reidj.thepit.clock.detail.TopManager
import me.reidj.thepit.command.AdminCommands
import me.reidj.thepit.consumable.ConsumableManager
import me.reidj.thepit.contract.ContractManager
import me.reidj.thepit.dungeon.DungeonHandler
import me.reidj.thepit.entity.EntityHandler
import me.reidj.thepit.event.EventManager
import me.reidj.thepit.item.ItemManager
import me.reidj.thepit.listener.*
import me.reidj.thepit.lootbox.LootBoxManager
import me.reidj.thepit.npc.NpcManager
import me.reidj.thepit.player.PlayerDataManager
import me.reidj.thepit.player.User
import me.reidj.thepit.protocol.AuctionMoneyDepositPackage
import me.reidj.thepit.sharpening.SharpeningManager
import me.reidj.thepit.util.systemMessage
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.command.ICommandService
import ru.cristalix.core.coupons.BukkitCouponsService
import ru.cristalix.core.coupons.ICouponsService
import ru.cristalix.core.datasync.EntityDataParameters
import ru.cristalix.core.network.Capability
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.party.IPartyService
import ru.cristalix.core.party.PartyService
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
    lateinit var eventManager: EventManager

    override fun onEnable() {
        app = this
        B.plugin = this

        client().registerCapability(
            Capability.builder()
                .className(AuctionMoneyDepositPackage::class.java.name)
                .notification(true)
                .build()
        )

        client().addListener(AuctionMoneyDepositPackage::class.java) { _, pckg ->
            val user = getUser(pckg.seller) ?: return@addListener

            user.player.systemMessage(
                MessageStatus.FINE,
                GlowColor.GREEN,
                "Ваш лот: ${pckg.itemName} §fбыл куплен игроком §b${pckg.customerName}§f."
            )

            user.giveMoney(pckg.money.toDouble())
        }

        CoreApi.get().also {
            it.registerService(ICouponsService::class.java, BukkitCouponsService(client(), ICommandService.get()))
            it.registerService(IPartyService::class.java, PartyService(client()))
        }

        Platforms.set(PlatformDarkPaper())

        Anime.include(NPC, EXPERIMENTAL, DIALOG, STANDARD, LOOTBOX)

        ModLoader.loadAll("mods")

        Lock.realms("TEST")

        IRealmService.get().currentRealmInfo.run {
            status = RealmStatus.WAITING_FOR_PLAYERS
            isLobbyServer = true
            readableName = "ThePitReborn"
            groupName = "ThePitReborn"
        }

        EntityDataParameters.register()

        config.options().copyDefaults(true)
        saveConfig()

        worldMeta = MapLoader.load("ThePit", "ThePitReborn")

        playerDataManager = PlayerDataManager()

        ContractManager()
        ItemManager()
        AuctionManager()
        LootBoxManager()
        BarterManager()
        NpcManager()

        AdminCommands()

        listener(
            playerDataManager,
            DamageHandler(),
            UnusedListener(),
            ConsumableManager(),
            SpawnHandler(),
            DungeonHandler(),
            EntityHandler(),
            HeldItemHandler(),
            ThePitHandler(),
            SharpeningManager(),
            Bag()
        )

        eventManager = EventManager()

        GameTimer(listOf(TopManager(), CombatManager(), PlayerRegeneration(), eventManager)).runTaskTimerAsynchronously(
            app,
            0,
            1
        )
    }

    override fun onDisable() {
        client().write(playerDataManager.bulkSave())
        Thread.sleep(1000)
    }

    fun getUsers() = playerDataManager.getUsers()

    fun getWorld() = app.worldMeta.world

    fun getUser(player: Player): User? = getUser(player.uniqueId)

    fun getUser(uuid: UUID): User? = playerDataManager.userMap[uuid]
}

fun client(): ISocketClient = ISocketClient.get()
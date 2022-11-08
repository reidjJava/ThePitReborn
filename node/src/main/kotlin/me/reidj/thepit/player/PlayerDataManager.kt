package me.reidj.thepit.player

import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import me.func.mod.Anime
import me.func.mod.ui.token.Token
import me.func.mod.ui.token.TokenGroup
import me.func.mod.util.after
import me.func.protocol.data.emoji.Emoji
import me.func.protocol.ui.indicator.Indicators
import me.reidj.thepit.app
import me.reidj.thepit.attribute.AttributeUtil
import me.reidj.thepit.client
import me.reidj.thepit.item.ItemManager
import me.reidj.thepit.player.prepare.Prepare
import me.reidj.thepit.player.prepare.PrepareMods
import me.reidj.thepit.player.prepare.PreparePlayerBrain
import me.reidj.thepit.protocol.BulkSaveUserPackage
import me.reidj.thepit.protocol.LoadUserPackage
import me.reidj.thepit.protocol.SaveUserPackage
import me.reidj.thepit.rank.RankUtil
import me.reidj.thepit.util.Formatter
import me.reidj.thepit.util.ImageType
import me.reidj.thepit.util.coroutine
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.properties.Delegates.notNull

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class PlayerDataManager : Listener {

    val userMap = mutableMapOf<UUID, User>()

    var prepares: MutableSet<Prepare> by notNull()

    private val group = TokenGroup(
        Token.builder()
            .title("Монеты")
            .content { player -> Emoji.COIN + "§6 " + Formatter.toFormat(app.getUser(player)!!.stat.money) }
            .build(),
        Token.builder()
            .title("Рейтинг")
            .content { player -> Emoji.DONATE_WHITE + " " + app.getUser(player)!!.stat.rankingPoints }
            .build()
    )

    init {
        prepares = mutableSetOf(PrepareMods(), PreparePlayerBrain)
    }

    @EventHandler
    fun AsyncPlayerPreLoginEvent.handle() = registerIntent(app).apply {
        coroutine().launch {
            val statPackage = client().writeAndAwaitResponse<LoadUserPackage>(LoadUserPackage(uniqueId)).await()
            var stat = statPackage.stat
            if (stat == null) stat = DefaultElements.createNewUser(uniqueId)
            userMap[uniqueId] = User(stat)
            completeIntent(app)
        }
    }

    @EventHandler
    fun PlayerJoinEvent.handle() {
        val user = app.getUser(player) ?: return

        user.player = player

        after(5) {
            Anime.loadTextures(player, *ImageType.values().map { it.path() }.toTypedArray())

            Anime.hideIndicator(player, Indicators.ARMOR, Indicators.EXP, Indicators.HEALTH, Indicators.HUNGER)

            group.subscribe(player)

            RankUtil.run {
                createRank(user)
                showAll(user)
            }

            prepares.forEach { it.execute(user) }
            player.inventory.addItem(AttributeUtil.generateAttribute(ItemManager.items["TEST2"]!!))
            player.inventory.addItem(AttributeUtil.generateAttribute(ItemManager.items["TEST3"]!!))
            player.inventory.addItem(AttributeUtil.generateAttribute(ItemManager.items["TEST4"]!!))
        }
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        val uuid = player.uniqueId

        userMap.remove(uuid)
        RankUtil.remove(uuid)
    }

    fun bulkSave(remove: Boolean): BulkSaveUserPackage? = BulkSaveUserPackage(Bukkit.getOnlinePlayers().map {
        val uuid = it.uniqueId
        val user = (if (remove) userMap.remove(uuid) else userMap[uuid]) ?: return null
        SaveUserPackage(uuid, user.stat)
    })
}
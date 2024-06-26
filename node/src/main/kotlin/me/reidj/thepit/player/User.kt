package me.reidj.thepit.player

import me.func.mod.Anime
import me.func.mod.util.after
import me.reidj.thepit.app
import me.reidj.thepit.client
import me.reidj.thepit.data.Stat
import me.reidj.thepit.dungeon.DungeonData
import me.reidj.thepit.rank.RankUtil
import me.reidj.thepit.util.hasKeyOfType
import me.reidj.thepit.util.playSound
import me.reidj.thepit.util.writeLog
import net.minecraft.server.v1_12_R1.Packet
import net.minecraft.server.v1_12_R1.PlayerConnection
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.spigotmc.AsyncCatcher
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class User(stat: Stat) {

    var stat: Stat

    lateinit var killer: Player
    lateinit var player: CraftPlayer
    lateinit var connection: PlayerConnection

    private var isArmLock = false

    var backpackInventory = hashMapOf<UUID, Inventory>()

    var state: State = DefaultState()
    var dungeon: DungeonData? = null

    var numberOfBlocksPassed = 0
    var isActive = false

    init {
        this.stat = stat
    }

    fun createBackpack() {
        player.inventory.filterNotNull().forEach { itemStack ->
            val nmsItem = CraftItemStack.asNMSCopy(itemStack)
            val tag = nmsItem.tag
            nmsItem.hasKeyOfType("uuidBackpack", 8) {
                backpackInventory[UUID.fromString(tag.getString("uuidBackpack"))] =
                    (Bukkit.createInventory(player, tag.getInt("size"), itemStack.i18NDisplayName).also {
                        fromBase64(tag.getString("items"), it)
                    })
            }
        }
    }

    fun toBase64(inventory: Inventory): String {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)

        for (slot in 0 until inventory.size) {
            dataOutput.writeObject(inventory.getItem(slot))
        }
        dataOutput.close()
        return Base64Coder.encodeLines(outputStream.toByteArray())
    }

    fun toBase64ItemStack(itemStack: ItemStack): String {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)

        dataOutput.writeObject(itemStack)
        dataOutput.close()

        return Base64Coder.encodeLines(outputStream.toByteArray())
    }

    fun toFromBase64ItemStack(data: String): ItemStack? {
        try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = BukkitObjectInputStream(inputStream)
            val readObject = dataInput.readObject() ?: return null
            return readObject as ItemStack
        } catch (_: Throwable) {
        }
        return null
    }

    fun fromBase64(data: String, inventory: Inventory) {
        try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = BukkitObjectInputStream(inputStream)

            for (slot in 0..41) {
                val readObject = dataInput.readObject() ?: continue
                val itemStack = readObject as ItemStack
                inventory.setItem(slot, itemStack)
            }
            dataInput.close()
        } catch (_: Throwable) {
        }
    }

    fun getRank() = RankUtil.getRank(stat.rankingPoints)

    fun armLock(handler: () -> Unit) {
        if (isArmLock) {
            return
        }
        isArmLock = true
        handler.invoke()
        after(5) { isArmLock = false }
    }

    fun tryPurchase(price: Double, acceptAction: () -> Unit, denyAction: () -> Unit) {
        if (stat.money >= price) {
            giveMoney(-price)
            acceptAction()
        } else {
            player.playSound(Sound.ENTITY_VILLAGER_NO)
            denyAction()
        }
    }

    fun hideFromAll() {
        // Скрытие игроков
        for (current in Bukkit.getOnlinePlayers()) {
            if (current == null || (app.getUser(current)
                    ?: return).dungeon?.party?.contains(stat.uuid) == true
            ) continue
            player.hidePlayer(app, current.player)
            current.hidePlayer(app, player)
        }
    }

    fun showToAllState() {
        for (current in app.getUsers()) {
            if (current.player.hasMetadata("vanish")) {
                continue
            }
            if (current.state == state || current.state::class.java == state::class.java) {
                current.player.showPlayer(app, player)
                player.showPlayer(app, current.player)
            }
        }
    }

    @JvmName("updateState")
    fun setState(state: State) {
        AsyncCatcher.catchOp("Async state change")
        if (this.state != state) {
            this.state.leaveState(this)
        }
        val previousState = this.state
        this.state = state
        state.enterState(this)
        after(1) {
            if (state.playerVisible() && !previousState.playerVisible()) {
                showToAllState()
            } else if (!state.playerVisible()) {
                hideFromAll()
            }
        }
    }

    fun generateStat(): Stat {
        stat.playerInventory = toBase64(player.inventory)
        stat.playerEnderChest = toBase64(player.enderChest)
        return stat
    }

    fun giveMoney(money: Double) {
        stat.money += money
    }

    fun giveMoneyWithBooster(money: Double) {
        giveMoney(money)
    }

    fun giveKill(kill: Int) {
        stat.kills += kill
    }

    fun giveDeath(death: Int) {
        stat.deaths += death
    }

    fun giveRankingPoints(points: Int) {
        val prevRankingPoints = stat.rankingPoints
        val prevRank = RankUtil.getRank(prevRankingPoints)
        val prevRankTitle = prevRank.title

        stat.rankingPoints += points

        client().writeLog("${player.name} получил $points ранговых очков.")

        val rank = RankUtil.getRank(stat.rankingPoints)

        if (rank.ordinal > prevRank.ordinal) {
            val newRankTitle = rank.title
            player.playSound(Sound.ENTITY_PLAYER_LEVELUP)
            rank.reward(this)
            RankUtil.updateRank(this)
            Anime.alert(player, "Поздравляем!", "Ваш ранг был повышен\n$prevRankTitle §f➠§l $newRankTitle")
            client().writeLog("${player.name} повысил ранг! $prevRankTitle-> $newRankTitle.")
        }
    }

    fun sendPacket(packet: Packet<*>) {
        connection.sendPacket(packet)
    }

    fun isKillerInitialized() = this::killer.isInitialized
}
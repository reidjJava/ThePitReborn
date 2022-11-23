package me.reidj.thepit.player

import me.func.mod.Anime
import me.func.mod.util.after
import me.reidj.thepit.app
import me.reidj.thepit.data.Stat
import me.reidj.thepit.dungeon.DungeonData
import me.reidj.thepit.rank.RankUtil
import me.reidj.thepit.util.errorMessage
import me.reidj.thepit.util.playSound
import net.minecraft.server.v1_12_R1.Packet
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo
import net.minecraft.server.v1_12_R1.PlayerConnection
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.spigotmc.AsyncCatcher
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class User(stat: Stat) {

    var stat: Stat

    lateinit var killer: Player
    lateinit var player: CraftPlayer
    lateinit var connection: PlayerConnection

    var state: State? = null
    var dungeon: DungeonData? = null

    var isArmLock = false
    var isActive = false

    init {
        this.stat = stat
    }

    fun toBase64(inventory: Inventory): String {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)

        for (slot in 0..inventory.size) {
            dataOutput.writeObject(inventory.getItem(slot))
        }
        dataOutput.close()
        return Base64Coder.encodeLines(outputStream.toByteArray())
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

    fun armLock(handler: () -> Unit) {
        if (isArmLock) {
            return
        }
        isArmLock = true
        handler.invoke()
        after(5) { isArmLock = false }
    }

    fun tryPurchase(price: Double, acceptAction: () -> Unit, errorMessage: String) {
        if (stat.money >= price) {
            giveMoney(-price)
            acceptAction()
        } else {
            player.playSound(Sound.ENTITY_VILLAGER_NO)
            player.errorMessage(errorMessage)
        }
    }

    fun hideFromAll() {
        // Отправка таба
        val show = PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, player.handle)
        // Скрытие игроков
        for (current in Bukkit.getOnlinePlayers()) {
            if (current == null) continue
            player.hidePlayer(app, current.player)
            sendPacket(
                PacketPlayOutPlayerInfo(
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                    (current as CraftPlayer).handle
                )
            )
            current.hidePlayer(app, player)
            current.handle.playerConnection.sendPacket(show)
        }
    }

    fun showToAllState() {
        for (current in app.getUsers()) {
            if (current.player.hasMetadata("vanish")) {
                continue
            }
            if (current.state == state || current.state!!::class.java == state!!::class.java) {
                current.player.showPlayer(app, player)
                player.showPlayer(app, current.player)
            }
        }
    }

    @JvmName("updateState")
    fun setState(state: State?) {
        AsyncCatcher.catchOp("Async state change")
        if (this.state != null && this.state != state)
            state?.leaveState(this)
        val previousState = this.state
        this.state = state
        state?.enterState(this)
        after(1) {
            if (state?.playerVisible() == true && previousState?.playerVisible() == false) {
                showToAllState()
            } else if (state?.playerVisible() == false) {
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

        stat.rankingPoints += points

        val rank = RankUtil.getRank(stat.rankingPoints)

        if (rank.ordinal > prevRank.ordinal) {
            rank.reward(this)
            RankUtil.updateRank(this)
            Anime.alert(player, "Поздравляем!", "Ваш ранг был повышен\n${prevRank.title} §f➠§l ${rank.title}")
        }
    }

    fun sendPacket(packet: Packet<*>) {
        connection.sendPacket(packet)
    }

    fun isKillerInitialized() = this::killer.isInitialized
}
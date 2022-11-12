package me.reidj.thepit.player

import me.func.mod.Anime
import me.reidj.thepit.data.Stat
import me.reidj.thepit.rank.RankUtil
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
    lateinit var player: Player

    private var state: State? = null

    var isArmLock = false

    init {
        this.stat = stat
    }

    private fun toBase64(inventory: Inventory): String {
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

    fun setState(state: State) {
        AsyncCatcher.catchOp("Async state change")
        if (this.state != null && this.state != state)
            state.leaveState(this)
        this.state = state
        state.enterState(this)
    }

    fun generateStat(): Stat {
        stat.playerInventory = toBase64(player.inventory)
        stat.playerEnderChest = toBase64(player.enderChest)
        return stat
    }

    fun giveMoney(money: Double) {
        stat.money += money
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
}
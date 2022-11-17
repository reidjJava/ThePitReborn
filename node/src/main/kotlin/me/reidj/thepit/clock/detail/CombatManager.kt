package me.reidj.thepit.clock.detail

import me.func.mod.Anime
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.status.MessageStatus
import me.reidj.thepit.clock.ClockInject
import me.reidj.thepit.util.systemMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

private const val COMBAT_COOL_DOWN = 20

class CombatManager : ClockInject {

    companion object {
        private val combatMap = hashMapOf<UUID, AtomicInteger>()
        private val color = GlowColor.RED_LIGHT

        operator fun get(uuid: UUID) = combatMap[uuid]

        fun put(player: Player) {
            combatMap[player.uniqueId] = AtomicInteger(COMBAT_COOL_DOWN)
            Anime.timer(player, "Вы выйдите из боя через", COMBAT_COOL_DOWN, color.red, color.blue, color.green)
        }
    }

    override fun run(tick: Int) {
        if (tick % 20 != 0) {
            return
        }
        Bukkit.getOnlinePlayers().forEach {
            val uuid = it.uniqueId
            val atomicInteger = get(uuid) ?: return@forEach
            val time = atomicInteger.get()

            atomicInteger.getAndDecrement()

            if (time <= 0) {
                combatMap.remove(uuid)
                it.systemMessage(MessageStatus.FINE, GlowColor.GREEN, "Вы вышли из боя.")
            }
        }
    }
}
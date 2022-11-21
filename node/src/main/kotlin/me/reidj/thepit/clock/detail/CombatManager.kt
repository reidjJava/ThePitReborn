package me.reidj.thepit.clock.detail

import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.status.MessageStatus
import me.reidj.thepit.clock.ClockInject
import me.reidj.thepit.util.systemMessage
import me.reidj.thepit.util.timer
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

        operator fun get(uuid: UUID) = combatMap[uuid]

        fun containKey(uuid: UUID) = combatMap.containsKey(uuid)

        fun remove(player: Player) {
            combatMap.remove(player.uniqueId)
            player.timer("Вы выйдите из боя через", 0, false)
        }

        fun put(player: Player) {
            val uuid = player.uniqueId
            val atomicInteger = AtomicInteger(COMBAT_COOL_DOWN)
            if (uuid in combatMap) {
                combatMap.replace(uuid, atomicInteger)
            } else {
                combatMap[uuid] = atomicInteger
            }
            player.timer("Вы выйдите из боя через", COMBAT_COOL_DOWN, true)
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
            it.timer("Вы выйдите из боя через", time, false)

            if (time <= 0) {
                it.systemMessage(MessageStatus.FINE, GlowColor.GREEN, "Вы вышли из боя.")
                combatMap.remove(uuid)
            }
        }
    }
}
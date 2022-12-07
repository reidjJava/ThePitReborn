package me.reidj.thepit.event

import me.func.mod.Anime
import me.reidj.thepit.clock.ClockInject
import org.bukkit.Bukkit

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class EventManager : ClockInject {

    val events: Map<String, Event> = mapOf(
        "golden_fever" to GoldenFever(),
        "dragon_egg" to DragonEgg(),
        "run" to Run(),
    )

    override fun run(tick: Int) {
        if (tick % 20 != 0) {
            return
        }
        events.values.forEach {
            if (it.beforeStart == 0) {
                if (it.secondsLeft == it.duration) {
                    it.atStart?.invoke()
                    Bukkit.getOnlinePlayers().forEach { player ->
                        Anime.topMessage(player, "Начался ивент §b${it.title}")
                        player.sendMessage(it.description)
                    }
                } else {
                    if (it.secondsLeft == 0) {
                        Bukkit.getOnlinePlayers().forEach { player ->
                            Anime.topMessage(player, "Закончился ивент §b${it.title}")
                        }
                        it.atEnd?.invoke()
                        it.beforeStart = it.waitSecond
                        it.secondsLeft = it.duration
                        return
                    }
                }
                it.secondsLeft--
            } else {
                it.beforeStart--
            }
        }
    }
}
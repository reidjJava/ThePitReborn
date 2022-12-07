package me.reidj.thepit.event

import me.reidj.thepit.clock.ClockInject

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class EventManager : ClockInject {

    val events: Map<String, Event> = mapOf(
        "golden_fever" to GoldenFever(),
        "dragon_egg" to DragonEgg(),
    )

    override fun run(tick: Int) {
        if (tick % 20 != 0) {
            return
        }
        events.values.forEach {
            if (it.beforeStart == 0) {
                if (it.secondsLeft == it.duration) {
                    it.atStart?.invoke()
                    println("начался ивент ${it.title}")
                } else {
                    if (it.secondsLeft == 0) {
                        println("ивент закончился ${it.title}")
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
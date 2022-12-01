package me.reidj.thepit.event

import me.reidj.thepit.clock.ClockInject

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class EventManager(private val events: Set<Event>): ClockInject {

    override fun run(tick: Int) {
        if (tick % 20 != 0) {
            return
        }
        events.forEach {
            if (it.beforeStart == 0) {
                if (it.secondsLeft == it.duration) {
                    println("начался ивент ${it.title}")
                } else {
                    if (it.secondsLeft == 0) {
                        println("ивент закончился ${it.title}")
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
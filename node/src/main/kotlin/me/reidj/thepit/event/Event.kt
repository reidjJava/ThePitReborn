package me.reidj.thepit.event

import org.bukkit.event.Event

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
abstract class Event(
    val title: String,
    val description: String,
    val duration: Int,
    val waitSecond: Int,
    val atStart: (() -> Unit)?,
    val atEnd: (() -> Unit)?,
    private val handler: Map<Class<out Event>, (Event) -> Unit>
) {
    // Сколько продлится ивент
    var secondsLeft = duration

    // Через сколько начнётся ивент
    var beforeStart = waitSecond

    fun on(clazz: Class<out Event>, event: Event) {
        if (beforeStart == 0) {
            handler[clazz]?.let { it(event) }
        }
    }
}
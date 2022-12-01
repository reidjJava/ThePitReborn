package me.reidj.thepit.event

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
abstract class Event(val title: String, val duration: Int, val waitSecond: Int) {

    // Сколько продлится ивент
    var secondsLeft = duration
    // Через сколько начнётся ивент
    var beforeStart = waitSecond
}
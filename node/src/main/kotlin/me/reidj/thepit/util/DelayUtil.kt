package me.reidj.thepit.util

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object DelayUtil {

    private val delay = hashMapOf<UUID, Long>()

    fun hasCountdown(uuid: UUID): Boolean {
        val data = delay[uuid]
        return data != null && data > System.currentTimeMillis()
    }

    fun setCountdown(uuid: UUID, value: Int) {
        delay[uuid] = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(value.toLong())
    }

    fun getSecondsLeft(uuid: UUID)= TimeUnit.MILLISECONDS.toSeconds(delay[uuid]!! - System.currentTimeMillis())
}
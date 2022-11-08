package me.reidj.thepit.player

import me.reidj.thepit.data.Stat
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object DefaultElements {

    fun createNewUser(uuid: UUID) = Stat(
        uuid, 0.0, 0, 0, 0, -1L, setOf(), "", ""
    )
}
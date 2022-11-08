package me.reidj.thepit.protocol

import me.reidj.thepit.top.PlayerTopEntry
import ru.cristalix.core.network.CorePackage

data class TopPackage(val topType: String, val limit: Int): CorePackage() {

    lateinit var entries: List<PlayerTopEntry<Any>>
}

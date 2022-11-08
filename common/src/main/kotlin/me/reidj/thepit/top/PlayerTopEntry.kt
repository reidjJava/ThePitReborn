package me.reidj.thepit.top

import me.reidj.thepit.data.Stat


class PlayerTopEntry<V>(stat: Stat, value: V) : TopEntry<Stat, V>(stat, value) {
    var userName: String? = null
    var displayName: String? = null
}
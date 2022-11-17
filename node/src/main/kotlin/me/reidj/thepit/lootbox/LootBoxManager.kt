package me.reidj.thepit.lootbox

import me.reidj.thepit.app

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class LootBoxManager {

    companion object {
        private val lootBoxes = hashMapOf<String, LootBox>()

        fun get(key: String) = lootBoxes[key]
    }

    init {
        app.worldMeta.labels("lootBox").forEach {
            val tag = it.tag.split("\n")
            val lootBox = tag[0]
            lootBoxes[lootBox] = LootBox(
                tag[1],
                it,
                BoxDropType.values().filter { type -> type.lootBox == lootBox }.toSet()
            ).also { type -> type.createBanner() }
        }
    }
}
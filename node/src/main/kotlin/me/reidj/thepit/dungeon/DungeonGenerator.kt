package me.reidj.thepit.dungeon

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class DungeonGenerator {

    companion object {
        private val dungeons = hashMapOf<String, DungeonType>()

        operator fun get(key: String) = dungeons[key]
    }

    init {
        // TODO Перебирать таблички и создавать данж
        dungeons["DUNGEON_TEST"] = DungeonType.TEST
    }
}
package me.reidj.thepit.entity

import me.reidj.thepit.dungeon.DungeonType
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class EntityGenerator {

    companion object {
        private val entities = hashSetOf<Entity>()

        fun all(): MutableCollection<Entity> = Collections.unmodifiableCollection(entities)
    }

    init {
        DungeonType.values().forEach { type -> type.entities.forEach { entities.add(it.key) } }
    }
}
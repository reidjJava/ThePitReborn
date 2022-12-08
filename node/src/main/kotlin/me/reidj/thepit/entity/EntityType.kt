package me.reidj.thepit.entity

import me.reidj.thepit.entity.successor.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
enum class EntityType(val entity: Entity) {
    BALROG(Balrog()),
    CAVE_TROLL(CaveTroll()),
    ORC(Orc()),
    URUKHAI(Urukhai()),
    KOBOLD(Kobold()),
    ;
}
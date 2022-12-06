package me.reidj.thepit.entity

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
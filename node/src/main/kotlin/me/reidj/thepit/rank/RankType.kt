package me.reidj.thepit.rank

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
enum class RankType(val points: IntRange) {
    NONE(0..499),
    BRONZE(500..999),
    SILVER(1000..1999),
    GOLD(2000..3999),
    PLATINUM(4000..5999),
    DIAMOND(6000..7999),
    MASTER(8000..Int.MAX_VALUE),
    ;
}
package me.reidj.thepit.rank

import me.reidj.thepit.player.User

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
enum class RankType(val title: String, val points: IntRange, val reward: (User) -> Unit) {
    NONE("§8Без ранга",0..499, {
        it.giveMoney(50.0)
    }),
    BRONZE("§6Бронза",500..999, {
        it.giveMoney(60.0)
    }),
    SILVER("§7Серебро",1000..1999, {
        it.giveMoney(65.0)
    }),
    GOLD("§eЗолото",2000..3999, {
        it.giveMoney(75.0)
    }),
    PLATINUM("§9Платина",4000..5999, {
        it.giveMoney(150.0)
    }),
    DIAMOND("§bАлмаз",6000..7999, {
        it.giveMoney(250.0)
    }),
    MASTER("§dМастер",8000..Int.MAX_VALUE, {
        it.giveMoney(350.0)
    }),
    ;
}
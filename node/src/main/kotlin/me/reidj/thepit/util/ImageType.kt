package me.reidj.thepit.util

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
enum class ImageType {
    BRONZE,
    SILVER,
    GOLD,
    PLATINUM,
    DIAMOND,
    MASTER,
    ;

    fun path() = "https://storage.c7x.ru/reidj/thepit/${name.lowercase()}.png"
}
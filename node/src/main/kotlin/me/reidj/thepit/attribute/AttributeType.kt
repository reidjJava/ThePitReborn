package me.reidj.thepit.attribute

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
enum class AttributeType(val title: String) {
    HEALTH("§cЗдоровье"),
    REGENERATION("§dРегенерация"),
    DAMAGE("§cУрон"),
    CHANCE_CRITICAL_DAMAGE("§3Шанс критического урона"),
    CRITICAL_DAMAGE_STRENGTH("§3Сила критического урона"),
    MOVE_SPEED("§bСкорость передвижения"),
    VAMPIRISM("§5Похищение крови"),
    ENTITY_PROTECTION("§aЗащита от сущностей"),
    ;
}
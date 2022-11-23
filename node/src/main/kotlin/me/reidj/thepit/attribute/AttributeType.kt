package me.reidj.thepit.attribute

import net.minecraft.server.v1_12_R1.NBTTagCompound

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
enum class AttributeType(val title: String) {
    HEALTH("§c♥ §7Здоровье"),
    REGENERATION("§5⍟ §7Регенерация"),
    DAMAGE("§4⚔ §7Урон"),
    CHANCE_CRITICAL_DAMAGE("§e⚡ §7Шанс критического урона"),
    CRITICAL_DAMAGE_STRENGTH("§3✤ §7Сила критического урона"),
    MOVE_SPEED("§b❖ §7Скорость передвижения"),
    VAMPIRISM("§c☩ §7Похищение крови"),
    ENTITY_PROTECTION("§a⚝ §7Защита от сущностей"),
    ;

    fun getObjectName() = name.lowercase()

    companion object {
        fun getAttributeWithNbt(tag: NBTTagCompound) = AttributeType.values().filter { tag.hasKey(it.name.lowercase()) }
    }
}
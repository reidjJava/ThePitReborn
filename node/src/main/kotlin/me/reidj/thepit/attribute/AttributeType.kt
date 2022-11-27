package me.reidj.thepit.attribute

import net.minecraft.server.v1_12_R1.NBTTagCompound

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
enum class AttributeType(val title: String, val isPercentage: Boolean) {
    HEALTH("§c♥ §7Здоровье", false),
    REGENERATION("§5♨ §7Регенерация", false),
    DAMAGE("§4⚔ §7Урон", false),
    CHANCE_CRITICAL_DAMAGE("§e⚡ §7Шанс крит.урона", true),
    CRITICAL_DAMAGE_STRENGTH("§3✤ §7Сила крит.урона", true),
    MOVE_SPEED("§b❖ §7Скорость передвижения", true),
    VAMPIRISM("§c☩ §7Похищение крови", true),
    ENTITY_PROTECTION("§a⚝ §7Защита от сущностей", true),
    ;

    fun getObjectName() = name.lowercase()

    companion object {
        fun getAttributeWithNbt(tag: NBTTagCompound) = AttributeType.values().filter { tag.hasKey(it.name.lowercase()) }
    }
}
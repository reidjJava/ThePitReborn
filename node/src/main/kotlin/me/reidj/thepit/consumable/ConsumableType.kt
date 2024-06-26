package me.reidj.thepit.consumable

import me.reidj.thepit.player.prepare.PreparePlayerBrain
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
enum class ConsumableType(
    val title: String,
    val description: String,
    val price: Double,
    val overlay: (Player) -> Any
) {
    MAJOR_HEALTH_POTION(
        "Большой флакон жизни",
        "Восстанавливает 3 §c❤ §7Вашего\n§7здоровья.",
        1500.0,
        { PreparePlayerBrain.setHealth(it, 6.0) }),
    LESSER_HEALTH_POTION(
        "Маленький флакон жизни",
        "Восстанавливает 2 §c❤ §7Вашего\n§7здоровья.",
        700.0,
        { PreparePlayerBrain.setHealth(it, 4.0) }),
    MAJOR_POTION_REGENERATION(
        "Большой флакон восстановления",
        "В течении 10 секунд Вам даётся\n§7эффект Регенерация I.",
        1300.0,
        {
            PreparePlayerBrain.addPotionEffect(it, PotionEffectType.REGENERATION, 10, 0)
        }),
    LESSER_POTION_REGENERATION(
        "Маленький флакон восстановления",
        "В течении 5 секунд Вам даётся\n§7эффект Регенерация I.",
        600.0,
        {
            PreparePlayerBrain.addPotionEffect(it, PotionEffectType.REGENERATION, 5, 0)
        }),
    MAJOR_POTION_AGILITY(
        "Большой флакон ловкости",
        "В течении 10 секунд Вам даётся\n§7эффект Скорость I.",
        1200.0,
        {
            PreparePlayerBrain.addPotionEffect(it, PotionEffectType.SPEED, 10, 0)
        }),

    LESSER_POTION_AGILITY(
        "Маленький флакон ловкости",
        "В течении 5 секунд Вам даётся\n§7эффект Скорость I.",
        500.0,
        {
            PreparePlayerBrain.addPotionEffect(it, PotionEffectType.SPEED, 5, 0)
        }),
    DRAGON_BREATH(
        "Драконье дыхание",
        "В течении 10 секунд Вам даётся\n§7эффект Сопротивление урону I.",
        600.0,
        {
            PreparePlayerBrain.addPotionEffect(it, PotionEffectType.DAMAGE_RESISTANCE, 10, 0)
        }
    ),
    SCROLL_OF_DARKNESS(
        "Свиток тьмы",
        "Накладывает эффект Слепоты I в течении 5 секунд\n§7на игроков в радиусе 5 блоков.",
        800.0,
        {
            PreparePlayerBrain.getNearbyPlayers(it, 5.0).forEach { nearby ->
                PreparePlayerBrain.addPotionEffect(nearby, PotionEffectType.BLINDNESS, 5, 0)
            }
        }
    ),
    FLAMING_SPHERE(
        "Пылающая сфера",
        "Все враги в радиусе 3 блоков поджигаются в течении 3 секунд.",
        700.0,
        {
            PreparePlayerBrain.getNearbyPlayers(it, 3.0).forEach { nearby ->
                nearby.fireTicks = 3 * 20
            }
        }
    )
    ;

    fun getObjectName() = name.lowercase()
}
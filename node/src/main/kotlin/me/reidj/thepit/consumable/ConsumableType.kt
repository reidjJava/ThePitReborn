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
        "",
        1500.0,
        { PreparePlayerBrain.setHealth(it, 4.0) }),
    MAJOR_POTION_AGILITY(
        "Большой флакон ловкости",
        "",
        1200.0,
        {
            PreparePlayerBrain.addPotionEffect(it, PotionEffectType.SPEED, 10, 0)
        }),
    MAJOR_POTION_REGENERATION(
        "Большой флакон восстановления",
        "",
        1300.0,
        {
            PreparePlayerBrain.addPotionEffect(it, PotionEffectType.REGENERATION, 10, 0)
        }),
    LESSER_POTION_REGENERATION(
        "Маленький флакон восстановления",
        "",
        500.0,
        {
            PreparePlayerBrain.addPotionEffect(it, PotionEffectType.REGENERATION, 5, 0)
        }),
    LESSER_POTION_AGILITY(
        "Маленький флакон ловкости",
        "",
        400.0,
        {
            PreparePlayerBrain.addPotionEffect(it, PotionEffectType.SPEED, 5, 0)
        }),
    LESSER_HEALTH_POTION(
        "Маленький флакон жизни",
        "",
        600.0,
        { PreparePlayerBrain.setHealth(it, 2.0) }),
    ;

    fun getObjectName() = name.lowercase()
}
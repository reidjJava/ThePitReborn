package me.reidj.thepit.consumable

import dev.implario.bukkit.item.item
import me.reidj.thepit.player.prepare.PreparePlayerBrain
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
enum class ConsumableType(
    val title: String,
    val description: String,
    val item: ItemStack,
    val price: Double,
    val overlay: (Player) -> Any
) {
    MAJOR_HEALTH_POTION(
        "Большой флакон жизни",
        "",
        createItem(
            MAJOR_HEALTH_POTION.title,
            MAJOR_HEALTH_POTION.description,
            MAJOR_HEALTH_POTION.getObjectName()
        ),
        1500.0,
        { PreparePlayerBrain.setHealth(it, 4.0) }),
    MAJOR_POTION_AGILITY(
        "Большой флакон ловкости",
        "",
        createItem(
            MAJOR_POTION_AGILITY.title,
            MAJOR_POTION_AGILITY.description,
            MAJOR_POTION_AGILITY.getObjectName()
        ),
        1200.0,
        {
            PreparePlayerBrain.addPotionEffect(it, PotionEffectType.SPEED, 10, 0)
        }),
    MAJOR_POTION_REGENERATION(
        "Большой флакон восстановления",
        "",
        createItem(
            MAJOR_POTION_REGENERATION.title,
            MAJOR_POTION_REGENERATION.description,
            MAJOR_POTION_REGENERATION.getObjectName()
        ),
        1300.0,
        {
            PreparePlayerBrain.addPotionEffect(it, PotionEffectType.REGENERATION, 10, 0)
        }),
    LESSER_POTION_REGENERATION(
        "Маленький флакон восстановления",
        "",
        createItem(
            LESSER_POTION_REGENERATION.title,
            LESSER_POTION_REGENERATION.description,
            LESSER_POTION_REGENERATION.getObjectName()
        ),
        500.0,
        {
            PreparePlayerBrain.addPotionEffect(it, PotionEffectType.REGENERATION, 5, 0)
        }),
    LESSER_POTION_AGILITY(
        "Маленький флакон ловкости",
        "",
        createItem(
            LESSER_POTION_AGILITY.title,
            LESSER_POTION_AGILITY.description,
            LESSER_POTION_AGILITY.getObjectName()
        ),
        400.0,
        {
            PreparePlayerBrain.addPotionEffect(it, PotionEffectType.SPEED, 5, 0)
        }),
    LESSER_HEALTH_POTION(
        "Маленький флакон жизни",
        "",
        createItem(LESSER_HEALTH_POTION.title, LESSER_HEALTH_POTION.description, LESSER_HEALTH_POTION.getObjectName()),
        600.0,
        { PreparePlayerBrain.setHealth(it, 2.0) }),
    ;

    fun getObjectName() = name.lowercase()
}

private fun createItem(title: String, description: String, tag: String) = item {
    type(Material.CLAY_BALL)
    text(
        """
            $title
            $description
            """.trimIndent()
    )
    amount(1)
    nbt("thepit", tag)
}
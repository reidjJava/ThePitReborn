package me.reidj.thepit.entity.successor

import me.reidj.thepit.entity.Entity
import me.reidj.thepit.entity.ability.Ability
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.math.V3

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class CaveTroll : Entity(EntityType.ZOMBIE) {

    override var damage = 5.0

    override var moveSpeed = 0.3

    override var health = 50.0

    override var attackRange = 20.0

    override var knockBackResistance = 1.0

    override var customName= "Пещерный тролль"

    override var metadata = "cave_troll"

    override var helmet: ItemStack? = null

    override var chestPlate: ItemStack? = null

    override var leggings: ItemStack? = null

    override var boots: ItemStack? = null

    override var itemInHand: ItemStack? = null

    override var scale = V3(1.0, 1.0, 1.0)

    // Шанс дропа 2%
    override var drops = mutableSetOf(
        0.98 to "TEST2"
    )

    override var abilities: MutableSet<Ability> = mutableSetOf()

    override var level = 8

    override var sound = Sound.ENTITY_ZOMBIE_HURT
}
package me.reidj.thepit.entity

import me.reidj.thepit.entity.ability.Ability
import me.reidj.thepit.entity.ability.Nuke
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.math.V3

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class Zombie : Entity(EntityType.ZOMBIE) {

    override var damage = 5.0

    override var moveSpeed = 0.3

    override var health = 50.0

    override var attackRange = 20.0

    override var knockBackResistance = 5.0

    override var customName= "Зомбяк"

    override var metadata = "zombie"

    override var helmet: ItemStack? = null

    override var chestPlate: ItemStack? = null

    override var leggings: ItemStack? = null

    override var boots: ItemStack? = null

    override var itemInHand: ItemStack? = null

    override var scale = V3(1.0, 1.0, 1.0)

    // Шанс дропа 10% и 1%
    override var drops = setOf(
        0.98 to "TEST2",
        0.99 to "PLAM"
    )

    override var abilities: Set<Ability> = setOf(Nuke())
}
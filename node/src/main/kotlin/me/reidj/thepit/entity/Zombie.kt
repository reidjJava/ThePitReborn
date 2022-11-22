package me.reidj.thepit.entity

import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.math.V3

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class Zombie : Entity(EntityType.ZOMBIE) {

    override var damage: Double = 5.0

    override var moveSpeed: Double = 0.5

    override var health: Double = 20.0

    override var attackRange: Double = 5.0

    override var knockBackResistance: Double = 0.0

    override var customName: String = "Зомбяк"

    override var metadata: String = "zombie"

    override var helmet: ItemStack? = null

    override var chestPlate: ItemStack? = null

    override var leggings: ItemStack? = null

    override var boots: ItemStack? = null

    override var scale: V3 = V3(1.0, 1.0, 1.0)
}
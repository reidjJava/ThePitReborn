package me.reidj.thepit.entity

import me.reidj.thepit.app
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import ru.cristalix.core.math.V3
import ru.cristalix.core.util.UtilEntity

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
abstract class Entity(val entityType: EntityType) {

    protected abstract var damage: Double

    protected abstract var moveSpeed: Double

    protected abstract var health: Double

    protected abstract var attackRange: Double

    protected abstract var knockBackResistance: Double

    protected abstract var customName: String

    protected abstract var metadata: String

    protected abstract var helmet: ItemStack?

    protected abstract var chestPlate: ItemStack?

    protected abstract var leggings: ItemStack?

    protected abstract var boots: ItemStack?

    protected abstract var scale: V3

    fun create(entity: LivingEntity) {
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).baseValue = damage
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).baseValue = moveSpeed
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue = health
        entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).baseValue = attackRange
        entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).baseValue = knockBackResistance
        entity.isCustomNameVisible = true
        entity.customName = customName
        entity.equipment.helmet = helmet
        entity.equipment.chestplate = chestPlate
        entity.equipment.leggings = leggings
        entity.equipment.boots = boots
        entity.removeWhenFarAway = false
        entity.canPickupItems = false
        entity.setMetadata("entity", FixedMetadataValue(app, metadata))
        UtilEntity.setScale(entity, scale.x, scale.y, scale.z)
    }
}
package me.reidj.thepit.entity

import me.reidj.thepit.app
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity
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
abstract class Entity(entityType: EntityType, location: Location) {

    var current: CraftEntity = app.worldMeta.world.createEntity(location, entityType.clazz).getBukkitEntity()
    val entity = current as LivingEntity

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

    private fun damage() {
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).baseValue = damage
    }

    private fun moveSpeed() {
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).baseValue = moveSpeed
    }

    private fun health() {
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue = health
    }

    private fun attackRange() {
        entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).baseValue = attackRange
    }

    private fun knockBackResistance() {
        entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).baseValue = knockBackResistance
    }

    private fun name() {
        entity.isCustomNameVisible = true
        entity.customName = customName
    }

    private fun helmet() {
        entity.equipment.helmet = helmet
    }

    private fun chestPlate() {
        entity.equipment.chestplate = chestPlate
    }

    private fun leggings() {
        entity.equipment.leggings = leggings
    }

    private fun boots() {
        entity.equipment.boots = boots
    }

    private fun metadata() {
        entity.setMetadata("entity", FixedMetadataValue(app, metadata))
    }

    private fun scale() {
        UtilEntity.setScale(entity, scale.x, scale.y, scale.z)
    }

    fun create() {
        damage()
        moveSpeed()
        health()
        attackRange()
        knockBackResistance()
        name()
        helmet()
        chestPlate()
        leggings()
        boots()
        metadata()
        scale()
    }
}
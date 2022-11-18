package me.reidj.thepit.entity

import me.reidj.thepit.app
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
abstract class Entity(entityType: EntityType, private val location: Location) {

    val current: CraftEntity = app.worldMeta.world.createEntity(location, entityType.clazz).getBukkitEntity()
    val entity = current as LivingEntity

    protected abstract var damage: Double

    protected abstract var moveSpeed: Double

    protected abstract var attackSpeed: Double

    protected abstract var health: Double

    protected abstract var attackRange: Double

    protected abstract var knockBackResistance: Double

    protected abstract var customName: String

    protected abstract var metadata: String

    protected abstract var helmet: ItemStack?

    protected abstract var chestPlate: ItemStack?

    protected abstract var leggings: ItemStack?

    protected abstract var boots: ItemStack?

    fun damage() {
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).baseValue = damage
    }

    fun moveSpeed() {
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).baseValue = moveSpeed
    }

    fun attackSpeed() {
        entity.getAttribute(Attribute.GENERIC_ATTACK_SPEED).baseValue = attackSpeed
    }

    fun health() {
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue = health
    }

    fun attackRange() {
        entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).baseValue = attackRange
    }

    fun knockBackResistance() {
        entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).baseValue = knockBackResistance
    }

    fun name() {
        entity.isCustomNameVisible = true
        entity.customName = customName
    }

    fun location() {
        entity.teleport(location)
    }

    fun helmet() {
        entity.equipment.helmet = helmet
    }

    fun chestPlate() {
        entity.equipment.chestplate = chestPlate
    }

    fun leggings() {
        entity.equipment.leggings = leggings
    }

    fun boots() {
        entity.equipment.boots = boots
    }

    fun metadata() {
        entity.setMetadata("entity", FixedMetadataValue(app, metadata))
    }
}
package me.reidj.thepit.entity

import me.reidj.thepit.app
import me.reidj.thepit.entity.ability.Ability
import me.reidj.thepit.entity.drop.EntityDrop
import me.reidj.thepit.item.ItemManager
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import ru.cristalix.core.math.V3
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
abstract class Entity(private val entityType: EntityType) {

    lateinit var current: CraftEntity
    lateinit var entity: LivingEntity

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

    protected abstract var itemInHand: ItemStack?

    protected abstract var drops: MutableSet<Pair<Double, String>>

    abstract var abilities: MutableSet<Ability>

    abstract var scale: V3

    @JvmName("getDrops1")
    fun getDrops() = drops.map { EntityDrop(it.first, ItemManager[it.second]) }.toMutableSet()

    fun setTarget(uuid: UUID) {
        EntityUtil.targetPlayer[entity.uniqueId] = uuid
    }

    fun changeDamage(damage: Double) {
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).baseValue = this.damage + damage
    }

    fun changeHealth(health: Double) {
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue = this.health + health
        entity.health = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue
    }

    fun create(player: Player, location: Location) {
        current = app.getWorld().createEntity(location, entityType.clazz).getBukkitEntity().also {
            val id = (Math.random() * Int.MAX_VALUE).toInt()
            it.handle.id = id
            //player.setSkin(id, "hog")
        }
        entity = current as LivingEntity


        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).baseValue = damage
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).baseValue = moveSpeed
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue = health
        entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).baseValue = attackRange
        entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).baseValue = knockBackResistance
        entity.isCustomNameVisible = true
        entity.customName = customName
        entity.health = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue
        entity.equipment.helmet = helmet
        entity.equipment.chestplate = chestPlate
        entity.equipment.leggings = leggings
        entity.equipment.boots = boots
        entity.equipment.itemInMainHand = itemInHand
        entity.removeWhenFarAway = false
        entity.canPickupItems = false
        entity.setMetadata("entity", FixedMetadataValue(app, metadata))
    }
}
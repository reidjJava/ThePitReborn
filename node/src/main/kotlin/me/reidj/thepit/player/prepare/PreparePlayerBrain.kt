package me.reidj.thepit.player.prepare

import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.dailyReward
import me.func.mod.util.after
import me.reidj.thepit.app
import me.reidj.thepit.attribute.AttributeType
import me.reidj.thepit.attribute.AttributeUtil
import me.reidj.thepit.client
import me.reidj.thepit.content.DailyReward
import me.reidj.thepit.player.User
import me.reidj.thepit.util.teleportAndPlayMusic
import me.reidj.thepit.util.writeLog
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.cristalix.core.formatting.Formatting

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object PreparePlayerBrain : Prepare {

    override fun execute(user: User) {
        val player = user.player
        val stat = user.stat
        val now = System.currentTimeMillis()

        spawnTeleport(player)

        after(10) {
            // Обнулить комбо сбора наград если прошло больше суток или комбо > 7
            if (stat.rewardStreak > 0 && now - stat.lastEnter * 10000 > 24 * 60 * 60 * 1000 || stat.rewardStreak > 6) {
                stat.rewardStreak = 0
            }
            if (now - stat.dailyClaimTimestamp * 10000 > 14 * 60 * 60 * 1000) {
                stat.dailyClaimTimestamp = now / 10000
                dailyReward {
                    currentDay = stat.rewardStreak
                    storage = DailyReward.values().map {
                        button {
                            title = it.title
                            item = it.itemStack
                        }
                    }.toMutableList()
                }.open(player)
                val dailyReward = DailyReward.values()[stat.rewardStreak]
                player.sendMessage(Formatting.fine("Ваша ежедневная награда: " + dailyReward.title))
                dailyReward.give(user)
                stat.rewardStreak++
                client().writeLog("${player.name} получил ежедневную награду за ${stat.rewardStreak} день.")
            }
            stat.lastEnter = now / 10000
        }
    }

    fun getSpawnLocation() = app.worldMeta.label("spawn")!!

    fun spawnTeleport(player: Player) = player.teleportAndPlayMusic(getSpawnLocation().clone().also {
        it.yaw = 0F
        it.x += 0.5
        it.z += 0.5
    })

    private fun getMaxHealth(player: Player) = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)


    private fun setMaxHealth(player: Player, maxHealth: Double) {
        getMaxHealth(player).baseValue = 20.0 + maxHealth
    }

    fun setHealth(player: Player, health: Double) {
        player.health = getMaxHealth(player).baseValue.coerceAtMost(player.health + health)
    }

    private fun setMoveSpeed(player: Player, speed: Double) {
        player.walkSpeed = getPercentage(speed).toFloat()
    }

    fun addPotionEffect(player: Player, potionEffectType: PotionEffectType, duration: Int, amplifier: Int) {
        player.removePotionEffect(potionEffectType)
        player.addPotionEffect(PotionEffect(potionEffectType, duration * 20, amplifier))
    }

    fun getNearbyPlayers(player: Player, radius: Double) =
        player.getNearbyEntities(radius, radius, radius).filterIsInstance<Player>()

    fun applyAttributes(player: Player, armorContents: Array<ItemStack>, vararg attributes: AttributeType) {
        if (attributes.any { it == AttributeType.HEALTH }) {
            setMaxHealth(
                player,
                AttributeUtil.getAttributeValue(AttributeType.HEALTH, armorContents)
            )
        }
        if (attributes.any { it == AttributeType.MOVE_SPEED }) {
            setMoveSpeed(
                player,
                AttributeUtil.getAttributeValue(AttributeType.MOVE_SPEED, armorContents)
            )
        }
        AttributeUtil.updateAllAttributes(player, armorContents)
    }

    fun getPercentage(attribute: Double, value: Double = 0.20000000298023224) = value + value * attribute / 100.0
}
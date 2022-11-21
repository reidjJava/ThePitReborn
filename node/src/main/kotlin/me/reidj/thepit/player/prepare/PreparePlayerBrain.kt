package me.reidj.thepit.player.prepare

import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.dailyReward
import me.reidj.thepit.app
import me.reidj.thepit.content.DailyReward
import me.reidj.thepit.player.User
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.cristalix.core.formatting.Formatting

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object PreparePlayerBrain : Prepare {

    override fun execute(user: User) {
        spawnTeleport(user.player)

        val stat = user.stat
        val now = System.currentTimeMillis()

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
            }.open(user.player)
            val dailyReward = DailyReward.values()[stat.rewardStreak]
            user.player.sendMessage(Formatting.fine("Ваша ежедневная награда: " + dailyReward.title))
            dailyReward.give(user)
            stat.rewardStreak++
        }
        stat.lastEnter = now / 10000
    }

    fun getSpawnLocation() = app.worldMeta.label("spawn")!!

    fun spawnTeleport(player: Player) = player.teleport(getSpawnLocation().also {
        it.yaw = 0F
        it.x += 0.5
        it.z += 0.5
    })

    fun setMaxHealth(player: Player, maxHealth: Double) {
        player.maxHealth = 20.0 + maxHealth
    }

    fun setHealth(player: Player, health: Double) {
        player.health = player.maxHealth.coerceAtMost(player.health + health)
    }

    fun setMoveSpeed(player: Player, speed: Double) {
        player.walkSpeed += speed.toFloat()
    }

    fun addPotionEffect(player: Player, potionEffectType: PotionEffectType, duration: Int, amplifier: Int) {
        player.removePotionEffect(potionEffectType)
        player.addPotionEffect(PotionEffect(potionEffectType, duration * 20, amplifier))
    }
}
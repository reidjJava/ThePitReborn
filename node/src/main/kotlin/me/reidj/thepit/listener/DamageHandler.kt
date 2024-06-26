package me.reidj.thepit.listener

import me.func.mod.Anime
import me.func.mod.util.after
import me.reidj.thepit.app
import me.reidj.thepit.attribute.AttributeType.*
import me.reidj.thepit.attribute.AttributeUtil
import me.reidj.thepit.client
import me.reidj.thepit.clock.detail.CombatManager
import me.reidj.thepit.dungeon.Dungeon
import me.reidj.thepit.player.DefaultState
import me.reidj.thepit.player.prepare.PreparePlayerBrain
import me.reidj.thepit.util.killBoardMessage
import me.reidj.thepit.util.writeLog
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.PlayerDeathEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

class DamageHandler : Listener {

    @EventHandler
    fun EntityDamageEvent.handle() {
        cancelled = cause == DamageCause.FALL
    }

    private val minY = PreparePlayerBrain.getSpawnLocation().y - 20

    @EventHandler(priority = EventPriority.MONITOR)
    fun EntityDamageByEntityEvent.handle() {
        if ((damager is Player || damager is Arrow)) {
            val playerDamager =
                if (damager is Projectile) ((damager as Projectile).shooter as Player) else damager as Player
            if (getEntity() is Player) {
                val entity = getEntity() as Player
                if (playerDamager.location.y >= minY || entity == playerDamager || entity.location.y >= minY) {
                    isCancelled = true
                } else {
                    CombatManager.put(playerDamager)
                    CombatManager.put(entity)

                    val armorContents = AttributeUtil.getAllItems(entity)

                    damage -= AttributeUtil.getAttributeValue(PROTECTION, armorContents) * 0.1

                    playerDamager.closeInventory()
                    entity.closeInventory()

                    (app.getUser(entity.uniqueId) ?: return).killer = playerDamager
                }
            }
            val armorContents = AttributeUtil.getAllItems(playerDamager)

            damage += AttributeUtil.getAttributeValue(DAMAGE, armorContents)

            if (Math.random() < AttributeUtil.getAttributeValue(CHANCE_CRITICAL_DAMAGE, armorContents)) {
                damage += AttributeUtil.getAttributeValue(CRITICAL_DAMAGE_STRENGTH, armorContents) * 0.3
            } else if (Math.random() > 0.90) {
                PreparePlayerBrain.setHealth(
                    playerDamager,
                    AttributeUtil.getAttributeValue(VAMPIRISM, armorContents)
                )
            }
        }
    }

    @EventHandler
    fun PlayerDeathEvent.handle() {
        cancelled = true

        dropExp = 0
        drops.clear()

        after(1) {
            PreparePlayerBrain.spawnTeleport(getEntity())
            Anime.title(getEntity(), "§cВЫ ПОГИБЛИ!")

            app.eventManager.events["golden_fever"]?.on(PlayerDeathEvent::class.java, this)

            CombatManager.remove(getEntity())

            val user = app.getUser(getEntity()) ?: return@after

            user.giveDeath(1)

            if (user.state is Dungeon) {
                user.setState(DefaultState())
            }

            if (!user.isKillerInitialized()) {
                return@after
            }

            val killer = app.getUser(user.killer) ?: return@after
            val entityName = getEntity().name

            user.killer.killBoardMessage("㥚 §c$entityName")

            client().writeLog("${user.killer.name} убил игрока $entityName.")

            killer.giveMoneyWithBooster(3.0)
            killer.giveKill(1)

            val userRankOrdinal = user.getRank().ordinal + 1
            val killerRankOrdinal = killer.getRank().ordinal + 1

            killer.giveRankingPoints(
                if (killerRankOrdinal > userRankOrdinal)
                    killerRankOrdinal * (userRankOrdinal * 0.1).toInt()
                else if (userRankOrdinal == killerRankOrdinal)
                    3
                else
                    2 * userRankOrdinal
            )
        }
    }
}
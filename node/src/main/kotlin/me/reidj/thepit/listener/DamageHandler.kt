package me.reidj.thepit.listener

import me.func.mod.Anime
import me.func.mod.util.after
import me.reidj.thepit.app
import me.reidj.thepit.attribute.AttributeType
import me.reidj.thepit.attribute.AttributeUtil
import me.reidj.thepit.clock.detail.CombatManager
import me.reidj.thepit.dungeon.Dungeon
import me.reidj.thepit.player.prepare.PreparePlayerBrain
import me.reidj.thepit.util.killBoardMessage
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
        if ((damager is Player || damager is Arrow) && entity is Player) {
            val playerDamager =
                if (damager is Projectile) ((damager as Projectile).shooter as Player) else damager as Player
            val entity = getEntity() as Player
            if (playerDamager.location.y >= minY || entity == playerDamager || entity.location.y >= minY) {
                cancelled = true
            } else {
                CombatManager.put(playerDamager)
                CombatManager.put(entity)

                playerDamager.closeInventory()
                entity.closeInventory()

                val armorContents = playerDamager.inventory.armorContents

                damage += AttributeUtil.getAttributeValue(AttributeType.DAMAGE.name.lowercase(), armorContents)

                if (Math.random() < AttributeUtil.getAttributeValue(
                        AttributeType.CHANCE_CRITICAL_DAMAGE.name.lowercase(),
                        armorContents
                    )
                ) {
                    damage += AttributeUtil.getAttributeValue(
                        AttributeType.CRITICAL_DAMAGE_STRENGTH.name.lowercase(),
                        armorContents
                    )
                }

                (app.getUser(entity.uniqueId) ?: return).killer = playerDamager
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

            CombatManager.remove(getEntity())

            val user = app.getUser(getEntity()) ?: return@after

            user.giveDeath(1)

            if (user.state is Dungeon) {
                user.state!!.leaveState(user)
                user.setState(null)
            }

            if (!user.isKillerInitialized()) {
                return@after
            }

            val killer = app.getUser(user.killer) ?: return@after

            user.killer.killBoardMessage("㥚 §c${getEntity().name}")

            killer.giveMoneyWithBooster(3.0)
            killer.giveKill(1)
        }
    }
}
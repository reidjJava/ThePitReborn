package me.reidj.thepit.listener

import me.func.mod.Anime
import me.func.mod.util.after
import me.reidj.thepit.app
import me.reidj.thepit.player.prepare.PreparePlayerBrain
import me.reidj.thepit.util.killBoardMessage
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
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

    @EventHandler
    fun EntityDamageByEntityEvent.handle() {
        if ((damager is Player || damager is Arrow) && entity is Player) {
            val playerDamager =
                if (damager is Projectile) ((damager as Projectile).shooter as Player) else damager as Player
            val user = app.getUser(entity.uniqueId) ?: return

            user.killer = playerDamager
        }
    }

    @EventHandler
    fun PlayerDeathEvent.handle() {
        cancelled = true

        dropExp = 0
        drops.clear()

        getEntity().run {
            after(1) {
                PreparePlayerBrain.spawnTeleport(this)
                Anime.title(this, "§cВЫ ПОГИБЛИ!")

                val user = app.getUser(this) ?: return@after

                user.giveDeath(1)

                user.killer.killBoardMessage("㥚 §c${getEntity().name}")

                val killer = app.getUser(user.killer) ?: return@after

                killer.giveMoney(3.0)
                killer.giveKill(1)
            }
        }
    }
}
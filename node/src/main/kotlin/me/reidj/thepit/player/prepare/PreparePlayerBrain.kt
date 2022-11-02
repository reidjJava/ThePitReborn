package me.reidj.thepit.player.prepare

import me.reidj.thepit.app
import me.reidj.thepit.player.User
import org.bukkit.entity.Player

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object PreparePlayerBrain : Prepare {

    override fun execute(user: User) {
        spawnTeleport(user.player)
    }

    fun getSpawnLocation() = app.worldMeta.label("spawn")!!

    fun spawnTeleport(player: Player) = player.teleport(getSpawnLocation().also { it.yaw = 180F })
}
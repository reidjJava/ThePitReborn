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

    fun spawnTeleport(player: Player) = player.teleport(app.worldMeta.label("spawn").also { it!!.yaw = 180F })
}
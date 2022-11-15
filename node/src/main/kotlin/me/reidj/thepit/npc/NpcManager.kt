package me.reidj.thepit.npc

import me.func.mod.world.Npc
import me.func.mod.world.Npc.location
import me.func.mod.world.Npc.onClick
import me.func.protocol.world.npc.NpcBehaviour
import me.reidj.thepit.app

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class NpcManager {

    init {
        app.worldMeta.labels("simplenpc").forEach {
            val data = app.config.getConfigurationSection("npc." + it.tag.split("\\s+"))
            val skin = data.getString("skin")
            Npc.npc {
                name = data.getString("title")
                behaviour = NpcBehaviour.STARE_AT_PLAYER
                pitch = it.pitch
                yaw = it.yaw
                skinUrl = skin
                skinDigest = skin.substring(skin.length - 10)
                location(it.also {
                    it.x += 0.5
                    it.z += 0.5
                })
                onClick { event ->
                    val player = event.player
                    val user = app.getUser(player) ?: return@onClick
                    if (user.armLock()) {
                        return@onClick
                    }
                    player.performCommand(data.getString("command"))
                }
            }
        }
    }
}
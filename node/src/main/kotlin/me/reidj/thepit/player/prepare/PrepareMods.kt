package me.reidj.thepit.player.prepare

import me.func.mod.conversation.ModLoader
import me.func.mod.reactive.ReactivePlace
import me.func.mod.ui.Glow
import me.func.mod.util.after
import me.func.protocol.data.color.GlowColor
import me.reidj.thepit.app
import me.reidj.thepit.player.User
import org.bukkit.util.Vector

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class PrepareMods : Prepare {

    private val vector = Vector(0.0, 1.0, 4.0).multiply(1)

    override fun execute(user: User) {
        ModLoader.send("mod-bundle-1.0-SNAPSHOT.jar", user.player)

        ReactivePlace.builder()
            .rgb(GlowColor.BLUE_MIDDLE)
            .radius(1.5)
            .x(124.5)
            .y(231.0)
            .z(78.5)
            .onEntire { player ->
                val entireUser = app.getUser(player) ?: return@onEntire

                if (entireUser.isActive) return@onEntire

                entireUser.isActive = true

                Glow.animate(player, 1.5, GlowColor.BLUE_MIDDLE, 2.0)

                player.velocity = vector

                after(5 * 20) { entireUser.isActive = false }
            }.build().apply { isConstant = true }
            .send(user.player)
    }
}
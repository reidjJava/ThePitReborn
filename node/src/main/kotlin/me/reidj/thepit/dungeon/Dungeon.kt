package me.reidj.thepit.dungeon

import me.func.mod.Anime
import me.reidj.thepit.app
import me.reidj.thepit.entity.EntityUtil
import me.reidj.thepit.entity.Zombie
import me.reidj.thepit.player.State
import me.reidj.thepit.player.User
import me.reidj.thepit.player.prepare.PreparePlayerBrain
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class Dungeon : State, Listener {

    override fun enterState(user: User) {
        val player = user.player

        player.inventory.setItem(9, State.backItem)

        EntityUtil.spawn(user)

        Anime.topMessage(user.player, "Вы вошли в подземелье")
        user.dungeon.teleport(user.player)
    }

    override fun leaveState(user: User) {
        Anime.topMessage(user.player, "Вы покинули подземелье")
        PreparePlayerBrain.spawnTeleport(user.player)
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (item == null) {
            return
        }
        val user = app.getUser(player) ?: return
        val nmsItem = CraftItemStack.asNMSCopy(item)
        val tag = nmsItem.tag

        if (nmsItem.hasTag() && tag.hasKeyOfType("dungeon", 8)) {
            val dungeonName = tag.getString("dungeon")
            val label = app.worldMeta.labels("dungeon").first { it.tag.split(" ")[0] == dungeonName }
            val itemInHand = player.itemInHand
            val locations = app.worldMeta.labels("$dungeonName-mob")
            val mobCount = locations.size

            itemInHand.setAmount(itemInHand.getAmount() - 1)

            user.dungeon = DungeonData(
                label,
                hashMapOf(Zombie() to mobCount),
                locations.toMutableList(),
                hashSetOf(player.uniqueId)
            )
            user.setState(Dungeon())
        }
    }
}
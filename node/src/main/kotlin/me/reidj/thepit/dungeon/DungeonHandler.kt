package me.reidj.thepit.dungeon

import me.func.mod.ui.Alert
import me.func.mod.ui.Alert.replace
import me.func.mod.ui.Alert.send
import me.func.mod.util.command
import me.func.protocol.data.color.GlowColor
import me.func.protocol.ui.alert.NotificationData
import me.reidj.thepit.app
import me.reidj.thepit.entity.Zombie
import me.reidj.thepit.player.User
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import ru.cristalix.core.party.IPartyService

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class DungeonHandler : Listener {

    init {
        Alert.put(
            "dungeon",
            NotificationData(
                null,
                "notify",
                "%nick% предложил войти в подземелье.",
                GlowColor.GREEN.toRGB(),
                GlowColor.GREEN_DARK.toRGB(),
                30000,
                listOf(
                    Alert.button("Принять", "/dungeonInviteAccept", GlowColor.GREEN)
                ),
                null
            )
        )
        command("dungeonInviteAccept") { player, _ ->
            dungeonTeleport((app.getUser(player) ?: return@command))
        }
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
            val uuid = player.uniqueId
            val label = app.worldMeta.labels("dungeon").first { it.tag.split(" ")[0] == dungeonName }
            val labelTag = label.tag.split(" ")
            val locations = app.worldMeta.labels("$dungeonName-mob")
            val partySnapshot = IPartyService.get().getPartyByMember(uuid).get()
            val dungeonData = DungeonData(
                label.clone().also {
                    it.y += 1.0
                    it.yaw = labelTag[1].toFloat()
                },
                mutableListOf(Zombie()),
                locations.toMutableList(),
                uuid
            )
            if (partySnapshot.isPresent) {
                val party = partySnapshot.get()
                party.members.mapNotNull { Bukkit.getPlayer(it) }.forEach {
                    val member = app.getUser(it) ?: return@forEach
                    Alert.find("dungeon")
                        .replace("%nick%", player.name)
                        .send(it)
                    member.dungeon = dungeonData.apply { this.party = party.members }
                }
            } else {
                user.dungeon = dungeonData.apply { this.party.add(uuid) }
                dungeonTeleport(user)
            }
        }
    }

    private fun dungeonTeleport(user: User) {
        val dungeon = user.dungeon!!
        if (user.stat.uuid == dungeon.leader) {
            val itemInHand = Bukkit.getPlayer(dungeon.leader).inventory.itemInMainHand
            itemInHand.setAmount(itemInHand.getAmount() - 1)
        }
        user.setState(Dungeon())
    }
}
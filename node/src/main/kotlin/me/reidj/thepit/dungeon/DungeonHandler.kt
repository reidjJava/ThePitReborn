package me.reidj.thepit.dungeon

import me.func.mod.ui.Alert
import me.func.mod.ui.Alert.replace
import me.func.mod.ui.Alert.send
import me.func.mod.util.command
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.status.MessageStatus
import me.func.protocol.ui.alert.NotificationData
import me.reidj.thepit.app
import me.reidj.thepit.entity.CaveTroll
import me.reidj.thepit.entity.Orc
import me.reidj.thepit.entity.Urukhai
import me.reidj.thepit.player.User
import me.reidj.thepit.util.systemMessage
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import ru.cristalix.core.party.IPartyService
import java.util.*

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
            val user = app.getUser(player) ?: return@command
            if (user.dungeon != null) {
                dungeonTeleport(user)
            }
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

        if (user.state is Dungeon) {
            return
        }

        if (nmsItem.hasTag() && tag.hasKeyOfType("dungeon", 8)) {
            val dungeonName = tag.getString("dungeon")
            val uuid = player.uniqueId
            val label = app.worldMeta.labels("dungeon").first { it.tag.split(" ")[0] == dungeonName }
            val labelTag = label.tag.split(" ")
            val locations = app.worldMeta.labels("$dungeonName-mob")
            val partySnapshot = IPartyService.get().getPartyByMember(uuid).get()
            val dungeonData = DungeonData(
                UUID.randomUUID(),
                label.clone().also {
                    it.y += 1.0
                    it.yaw = labelTag[1].toFloat()
                },
                mutableListOf(Orc(), CaveTroll(), Orc(), Urukhai()),
                locations.toMutableList(),
                uuid
            )
            if (partySnapshot.isPresent) {
                val party = partySnapshot.get()

                if (party.members.size > 4) {
                    player.systemMessage(MessageStatus.ERROR, GlowColor.RED, "Недопустимое количество участников пати!")
                    return
                }

                party.members.mapNotNull { Bukkit.getPlayer(it) }.forEach {
                    val member = app.getUser(it)

                    if (member?.dungeon != null) {
                        return@forEach
                    }

                    dungeonData.party = party.members
                    member?.dungeon = dungeonData

                    if (member != user) {
                        Alert.find("dungeon")
                            .replace("%nick%", player.name)
                            .send(it)
                    } else {
                        dungeonTeleport(member)
                    }
                }
            } else {
                user.dungeon = dungeonData
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
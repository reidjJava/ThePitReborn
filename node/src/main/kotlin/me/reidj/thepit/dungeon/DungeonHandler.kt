package me.reidj.thepit.dungeon

import me.func.mod.Anime
import me.func.mod.ui.Alert
import me.func.mod.ui.Alert.replace
import me.func.mod.ui.Alert.send
import me.func.mod.ui.menu.confirmation.Confirmation
import me.func.mod.util.command
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.status.MessageStatus
import me.func.protocol.ui.alert.NotificationData
import me.reidj.thepit.app
import me.reidj.thepit.player.DefaultState
import me.reidj.thepit.player.User
import me.reidj.thepit.util.itemInMainHand
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
                "%nick% предложил войти в подземелье\n%dungeonName%.",
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
            if (user.dungeon != null && !checkNumberVisits(user)) {
                dungeonTeleport(user)
            }
        }

        Anime.createReader("thepit:key-press") { player, _ ->
            if ((app.getUser(player) ?: return@createReader).state is Dungeon)
                Confirmation("Вы действительно хотите", "покинуть подземелье?") {
                    (app.getUser(it) ?: return@Confirmation).setState(DefaultState())
                }.open(player)
        }
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        val user = app.getUser(player) ?: return
        val nmsItem = CraftItemStack.asNMSCopy(item)
        val tag = nmsItem.tag

        if (user.state is Dungeon) {
            return
        }

        if (nmsItem.hasTag() && tag.hasKeyOfType("dungeon", 8)) {
            val dungeonName = tag.getString("dungeon")
            val uuid = player.uniqueId
            val dungeon = DungeonType.values().find { dungeonName == it.tag } ?: return
            val mobLocations = app.worldMeta.labels("$dungeonName-mob")
            val dungeonData = DungeonData(
                UUID.randomUUID(),
                dungeon.location,
                dungeon.entities,
                mobLocations,
                uuid
            )
            val partySnapshot = IPartyService.get().getPartyByMember(uuid).get()

            if (partySnapshot.isPresent) {
                val party = partySnapshot.get()

                if (party.members.size > 4) {
                    player.systemMessage(
                        MessageStatus.ERROR,
                        GlowColor.RED,
                        "Недопустимое количество участников пати!"
                    )
                    return
                } else if (checkNumberVisits(user)) {
                    return
                }

                party.members.mapNotNull { Bukkit.getPlayer(it) }.forEach {
                    val member = app.getUser(it)

                    dungeonData.party = party.members
                    member?.dungeon = dungeonData

                    if (member != user) {
                        Alert.find("dungeon")
                            .replace("%nick%", player.name)
                            .replace("%dungeonName%", dungeon.title)
                            .send(it)
                    } else {
                        dungeonTeleport(member)
                    }
                }
            } else {
                user.dungeon = dungeonData.also { it.party.add(uuid) }
                dungeonTeleport(user)
            }
        }
    }

    private fun checkNumberVisits(user: User): Boolean {
        if (user.stat.numberVisitsToDungeon == 5) {
            user.player.systemMessage(
                MessageStatus.ERROR,
                GlowColor.RED,
                "У вас максимальное количество входов в подземелье!"
            )
            return true
        }
        return false
    }

    private fun dungeonTeleport(user: User) {
        val dungeon = user.dungeon!!
        if (user.stat.uuid == dungeon.leader) {
            val itemInHand = Bukkit.getPlayer(dungeon.leader).itemInMainHand()
            itemInHand.setAmount(itemInHand.getAmount() - 1)
        }
        user.setState(Dungeon())
    }
}
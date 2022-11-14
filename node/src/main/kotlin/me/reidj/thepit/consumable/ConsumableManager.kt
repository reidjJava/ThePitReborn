package me.reidj.thepit.consumable

import dev.implario.bukkit.item.item
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.selection
import me.func.mod.util.command
import me.func.protocol.data.emoji.Emoji
import me.reidj.thepit.app
import me.reidj.thepit.sound.SoundType
import me.reidj.thepit.util.Formatter
import me.reidj.thepit.util.errorMessage
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class ConsumableManager : Listener {

    private val menu = selection {
        title = "Флаконы"
        vault = Emoji.COIN
        columns = 2
    }

    init {
        command("consumable") { player, _ ->
            generateButtons(player)
            menu.open(player)
        }
    }

    private fun generateButtons(player: Player) {
        menu.money = Formatter.toMoneyFormat((app.getUser(player) ?: return).stat.money)
        menu.storage = ConsumableType.values().map {
            button {
                title = it.title
                description = "§7" + it.description
                price = it.price.toLong()
                item = item {
                    type(Material.CLAY_BALL)
                    nbt("thepit", it.getObjectName())
                }
                onClick { player, _, _ ->
                    val user = app.getUser(player) ?: return@onClick

                    val consumableAmount = player.inventory.map { CraftItemStack.asNMSCopy(it) }.filter { itemStack ->
                        itemStack.hasTag() && itemStack.tag.hasKeyOfType("consumable", 8) }

                    if (consumableAmount.count() >= 2 || consumableAmount.any { it.asBukkitMirror().getAmount() >= 5 }) {
                        player.errorMessage("У вас максимальное количество флаконов!")
                        return@onClick
                    }

                    if (user.stat.money >= it.price) {
                        user.giveMoney(-it.price)
                        player.inventory.addItem(item {
                            type(Material.CLAY_BALL)
                            text(
                                """
                            §6${it.title}
                            §7${it.description}
                            
                            §7§oЩёлкните ПКМ, чтобы выпить.
                            """.trimIndent()
                            )
                            amount(1)
                            nbt("thepit", it.getObjectName())
                            nbt("consumable", it.getObjectName())
                        })
                        generateButtons(player)
                    } else {
                        player.errorMessage("Недостаточно средств!")
                    }
                }
            }
        }.toMutableList()
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (item == null) {
            return
        }

        val nmsItem = CraftItemStack.asNMSCopy(item)
        val tag = nmsItem.tag ?: return

        if (tag.hasKeyOfType("consumable", 8)) {
            val itemInHand = player.itemInHand
            itemInHand.setAmount(itemInHand.getAmount() - 1)
            ConsumableType.values()
                .find { tag.getString("consumable") == it.getObjectName() }?.overlay?.let { it(player) }
            SoundType.USE_CONSUMABLE.send(player)
        }
    }
}
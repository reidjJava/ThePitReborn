package me.reidj.thepit.consumable

import dev.implario.bukkit.item.item
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.selection
import me.func.mod.util.command
import me.func.protocol.data.emoji.Emoji
import me.func.protocol.ui.dialog.*
import me.reidj.thepit.app
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
        title = "Расходка"
        vault = Emoji.COIN
        columns = 2
    }

    private val guideDialog = Dialog(
        Entrypoint(
            "consumablePageOne",
            "Франк",
            Screen(
                "Приветствую вас в нашем клубе самоубийц-неудачников! Нет,",
                "серьезно, местные же иногда такое вытворяют, что действительно",
                "кажется, будто они самоубийцы. Однако раз за разом они",
                "возвращаются ко мне пропустить стопку-другую, либо приобрести",
                "мои волшебные Зелья и Свитки, а значит, все-таки самоубийцы-неудачники.",
                "А что насчёт тебя? Хочешь поторговаться?"
            ).buttons(
                Button("Да, давай поторгуем.").actions(
                    Action(Actions.COMMAND).command("/consumableMenu"),
                    Action(Actions.CLOSE)
                ),
                Button("Расскажи про другие ресурсы, которые здесь в ходу.").actions(
                    Action(Actions.COMMAND).command("/consumablePageTwo"),
                    Action(Actions.CLOSE)
                )
            )
        ),
        Entrypoint(
            "consumablePageTwo",
            "Франк",
            Screen(
                "Подземелья есть подземелья. В подземелье много всякого",
                "ценного находят, и далеко не только сокровище, но и что-то более невиданное...",
                "Советую заглянуть к Умбре, он пускай и отшибленный на всю голову,",
                "но он единственный, кто может предложить хорошую работу."
            ).buttons(Button("Закрыть").actions(Action(Actions.CLOSE)))
        ),
    )

    init {
        command("consumable") { player, _ ->
            me.func.mod.ui.dialog.Dialog.dialog(player, guideDialog, "consumablePageOne")
        }
        command("consumablePageTwo") { player, _ ->
            me.func.mod.ui.dialog.Dialog.dialog(player, guideDialog, "consumablePageTwo")
        }
        command("consumableMenu") { player, _ ->
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

                    if (user.armLock()) {
                        return@onClick
                    }

                    val consumableAmount = player.inventory.map { CraftItemStack.asNMSCopy(it) }.filter { itemStack ->
                        itemStack.hasTag() && itemStack.tag.hasKeyOfType("consumable", 8)
                    }

                    if (consumableAmount.count() >= 2 || consumableAmount.any {
                            it.asBukkitMirror().getAmount() >= 5
                        }) {
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
        }
    }
}
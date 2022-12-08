package me.reidj.thepit.consumable

import dev.implario.bukkit.item.item
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.selection
import me.func.mod.util.command
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.emoji.Emoji
import me.func.protocol.data.status.MessageStatus
import me.func.protocol.ui.dialog.*
import me.reidj.thepit.app
import me.reidj.thepit.util.*
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import ru.cristalix.core.formatting.Formatting

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
                    user.armLock {
                        val consumableAmount =
                            player.inventory.map { CraftItemStack.asNMSCopy(it) }.filter { itemStack ->
                                itemStack.hasTag() && itemStack.tag.hasKeyOfType("consumable", 8)
                            }

                        if (consumableAmount.sumOf { it.asBukkitMirror().getAmount() } == 10) {
                            player.errorMessage("У вас максимальное количество флаконов!")
                            player.playSound(Sound.BLOCK_ANVIL_BREAK)
                            return@armLock
                        }

                        user.tryPurchase(it.price, {
                            player.playSound(Sound.ENTITY_PLAYER_LEVELUP)
                            player.inventory.addItem(item {
                                type(Material.CLAY_BALL)
                                text(
                                    """
                            §6${it.title}
                            §7${it.description}
                            
                            §7§oЩёлкните ПКМ, чтобы использовать.
                            """.trimIndent()
                                )
                                amount(1)
                                nbt("thepit", it.getObjectName())
                                nbt("consumable", it.getObjectName())
                            })
                            generateButtons(player)
                        }, "Недостаточно средств!")
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
        val uuid = player.uniqueId

        if (tag.hasKeyOfType("consumable", 8)) {
            if (!DelayUtil.hasCountdown(uuid)) {
                val itemInHand = player.itemInMainHand()
                itemInHand.setAmount(itemInHand.getAmount() - 1)
                ConsumableType.values()
                    .find { tag.getString("consumable") == it.getObjectName() }?.overlay?.let { it(player) }
                DelayUtil.setCountdown(uuid, 5)
            } else {
                val seconds = DelayUtil.getSecondsLeft(uuid)
                player.systemMessage(
                    MessageStatus.ERROR,
                    GlowColor.RED,
                    "До следующего использования ${
                        Formatting.countPluralRu(
                            seconds.toInt(),
                            "секунда",
                            "секунды",
                            "секунд"
                        )
                    }"
                )
            }
        }
    }

    @EventHandler
    fun InventoryClickEvent.handle() {
        val player = whoClicked as Player
        if (player.openInventory.type == InventoryType.ENDER_CHEST) {
            val nmsItem = CraftItemStack.asNMSCopy(currentItem)
            val tag = nmsItem.tag
            if (nmsItem.hasTag() && tag.hasKeyOfType("consumable", 8)) {
                isCancelled = true
                player.updateInventory()
            }
        }
    }
}
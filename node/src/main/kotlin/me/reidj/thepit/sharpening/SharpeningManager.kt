package me.reidj.thepit.sharpening

import me.func.mod.Anime
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.selection
import me.func.mod.util.command
import me.func.protocol.data.emoji.Emoji
import me.reidj.thepit.app
import me.reidj.thepit.attribute.AttributeType
import me.reidj.thepit.util.Formatter
import me.reidj.thepit.util.errorMessage
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class SharpeningManager {

    private val menu = selection {
        title = "Улучшение предметов"
        info = "Затачивая вещь Вы с определённым шансом улучшаете все её характеристики"
        rows = 3
        columns = 1
        vault = Emoji.COIN
    }

    init {
        command("sharpening") { sender, _ ->
            val itemInHand = sender.itemInHand
            val itemMeta = itemInHand.itemMeta
            val sharpening = sender.inventory.filterNotNull()
                .find { CraftItemStack.asNMSCopy(it).tag.hasKeyOfType("sharpening_chance", 99) }
                ?: return@command
            val sharpeningTag = CraftItemStack.asNMSCopy(sharpening).tag
            val attributes = AttributeType.values().map { it.name.lowercase() }
            val chance = sharpeningTag.getDouble("sharpening_chance")
            val price = sharpeningTag.getDouble("sharpening_price")

            if (itemInHand == null || itemMeta == null || itemMeta.displayName == null) {
                sender.errorMessage("Вы не можете заточить этот предмет!")
                return@command
            }

            menu.storage.clear()
            menu.storage.add(
                button {
                    title = itemMeta.displayName
                    item = itemInHand
                    hint = "Заточить"
                    description = """
                            §7Вы можете улучшить характеристики предмета на 1 единицу.
                            §cШанс разрушить точильный камень ${(chance * 100).toInt()}%
                            §6Стоимость заточки ${Formatter.toMoneyFormat(price)} §f${Emoji.COIN}
                        """.trimIndent()
                    onClick { player, _, _ ->
                        val user = app.getUser(player) ?: return@onClick
                        val nmsItem = CraftItemStack.asNMSCopy(player.itemInHand)
                        val tag = nmsItem.tag

                        for (objectName in attributes) {
                            if (!nmsItem.hasTag() && !tag.hasKeyOfType(objectName, 99)) {
                                return@onClick
                            }
                        }

                        if (user.stat.money >= price) {
                            user.giveMoney(-price)
                            Anime.close(player)
                            if (Math.random() < CraftItemStack.asNMSCopy(sharpening).tag.getDouble("sharpening_chance")) {
                                player.errorMessage("Точильный камень был разрушен")
                            } else {
                                Anime.topMessage(player, "§aПредмет был заточен")
                                attributes.filter { tag.hasKeyOfType(it, 99) }
                                    .forEach { tag.setDouble(it, tag.getDouble(it) + 1.0) }
                                player.itemInHand.setAmount(0)
                                player.inventory.addItem(nmsItem.asBukkitMirror())
                            }
                            sharpening.setAmount(sharpening.getAmount() - 1)
                        } else {
                            player.errorMessage("Недостаточно средств")
                        }
                    }
                }
            )
            openMenu(sender)
        }
    }

    private fun openMenu(player: Player) {
        menu.open(player)
    }
}
package me.reidj.thepit.sharpening

import me.func.mod.Anime
import me.func.mod.ui.Glow
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.selection
import me.func.mod.util.command
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.emoji.Emoji
import me.func.protocol.ui.dialog.*
import me.reidj.thepit.app
import me.reidj.thepit.attribute.AttributeType
import me.reidj.thepit.attribute.AttributeUtil
import me.reidj.thepit.item.ItemManager
import me.reidj.thepit.util.Formatter
import me.reidj.thepit.util.errorMessage
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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

    private val guideDialog = Dialog(
        Entrypoint(
            "sharpeningPageOne",
            "Заточка",
            Screen("Чем могу помочь?").buttons(
                Button("Заточить предмет").actions(
                    Action(Actions.COMMAND).command("/sharpeningMenu"),
                    Action(Actions.CLOSE)
                ),
                Button("Что это такое?").actions(
                    Action(Actions.COMMAND).command("/sharpeningPageTwo"),
                    Action(Actions.CLOSE)
                )
            )
        ),
        Entrypoint(
            "sharpeningPageTwo",
            "Заточка",
            Screen(
                "Используя камни определенного качества,",
                "можно заточить вещи и оружие, повысив характеристики предмета.",
                "Каждая заточка имеет определенный шанс неудачи.",
                "Чтобы повысить шансы на успешную заточку, есть особые точильные камни,",
                "которые можно получить в игровом магазине."
            ).buttons(Button("Закрыть").actions(Action(Actions.CLOSE)))
        )
    )

    init {
        command("sharpening") { player, _ -> me.func.mod.ui.dialog.Dialog.dialog(player, guideDialog, "sharpeningPageOne") }
        command("sharpeningPageTwo") { player, _ -> me.func.mod.ui.dialog.Dialog.dialog(player, guideDialog, "sharpeningPageTwo") }
        command("sharpeningMenu") { player, _ ->
            val itemInHand = player.itemInHand
            val itemMeta = itemInHand.itemMeta
            if (itemInHand == null || itemMeta == null || itemMeta.displayName == null) {
                player.errorMessage("Вы не можете заточить этот предмет!")
                return@command
            } else if (findSharpeningStone(player) == null) {
                player.errorMessage("У вас нету точильных камней!")
                return@command
            }
            generateButtons(player)
            menu.open(player)
        }
    }

    private fun generateButtons(player: Player) {
        val itemInHand = player.itemInHand
        val itemMeta = itemInHand.itemMeta
        val sharpening = findSharpeningStone(player)!!
        val sharpeningTag = CraftItemStack.asNMSCopy(sharpening).tag
        val chance = sharpeningTag.getDouble("sharpening_chance")
        val price = sharpeningTag.getDouble("sharpening_price")

        menu.money = Formatter.toMoneyFormat((app.getUser(player) ?: return).stat.money)
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
                    val sharpeningLevel = tag.getInt("sharpeningLevel")

                    if (user.armLock()) {
                        return@onClick
                    }

                    if (sharpeningLevel == 10) {
                        player.errorMessage("У вас максимальный уровень заточки!")
                        return@onClick
                    }

                    if (AttributeType.getAttributeWithNbt(tag).isEmpty()) {
                        return@onClick
                    }

                    if (user.stat.money >= price) {
                        user.giveMoney(-price)
                        Anime.close(player)
                        if (Math.random() < CraftItemStack.asNMSCopy(sharpening).tag.getDouble("sharpening_chance")) {
                            player.errorMessage("Точильный камень был разрушен")
                        } else {
                            Anime.topMessage(player, "§aПредмет был заточен")
                            Glow.animate(player, 1.0, GlowColor.GREEN)

                            AttributeType.getAttributeWithNbt(tag).forEach {
                                tag.setDouble(it.getObjectName(), tag.getDouble(it.getObjectName()) + 1.0)
                                tag.setInt("sharpeningLevel", sharpeningLevel + 1)
                            }

                            player.itemInHand.setAmount(0)

                            val itemStack = nmsItem.asBukkitMirror()

                            setNewNameWithSharpening(itemStack)
                            AttributeUtil.setNewLoreWithAttributes(itemStack)

                            player.inventory.addItem(itemStack)
                        }
                        sharpening.setAmount(sharpening.getAmount() - 1)
                    } else {
                        player.errorMessage("Недостаточно средств")
                    }
                }
            }
        )
    }

    private fun setNewNameWithSharpening(itemStack: ItemStack) {
        val tag = CraftItemStack.asNMSCopy(itemStack).tag
        val sharpeningLevel = tag.getInt("sharpeningLevel")
        itemStack.itemMeta = itemStack.itemMeta.also { meta ->
            meta.displayName =
                ItemManager[tag.getString("address")]?.itemMeta?.displayName + if (sharpeningLevel == 0) "" else " +$sharpeningLevel"
        }
    }

    private fun findSharpeningStone(player: Player) = player.inventory.filterNotNull()
        .find {
            val nmsItem = CraftItemStack.asNMSCopy(it)
            nmsItem.hasTag() && nmsItem.tag.hasKeyOfType("sharpening_chance", 99)
        }
}
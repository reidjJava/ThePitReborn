package me.reidj.thepit.sharpening

import me.func.mod.Anime
import me.func.mod.ui.Glow
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.selection
import me.func.mod.util.after
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
import me.reidj.thepit.util.playSound
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class SharpeningManager : Listener {

    private val menu = selection {
        title = "Улучшение предметов"
        rows = 3
        columns = 1
        vault = Emoji.COIN
    }

    private val guideDialog = Dialog(
        Entrypoint(
            "sharpeningPageOne",
            "Дальгриг",
            Screen("Чем могу помочь?").buttons(
                Button("Заточка").actions(
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
            "Дальгриг",
            Screen(
                "Используя камни определенного качества,",
                "можно заточить предмет, повысив его характеристики.",
                "С каждой успешной заточкой, уровень предмета повышается на +1,",
                "а при неудаче - заточка ломается, не повышая уровень предмета.",
                "Максимальный уровень заточки у каждого предмета - 10."
            ).buttons(Button("Закрыть").actions(Action(Actions.CLOSE)))
        )
    )

    init {
        command("sharpening") { player, _ ->
            val user = (app.getUser(player) ?: return@command)

            if (user.isArmorEquipment) {
                return@command
            }

            user.isArmorEquipment = true

            me.func.mod.ui.dialog.Dialog.dialog(
                player,
                guideDialog,
                "sharpeningPageOne"
            )

            after(5) { user.isArmorEquipment = false }
        }
        command("sharpeningPageTwo") { player, _ ->
            me.func.mod.ui.dialog.Dialog.dialog(
                player,
                guideDialog,
                "sharpeningPageTwo"
            )
        }
        command("sharpeningMenu") { player, _ ->
            val itemInHand = player.itemInHand
            val itemMeta = itemInHand.itemMeta
            if (itemInHand == null || itemMeta == null || itemMeta.displayName == null) {
                player.errorMessage("Вы не можете заточить этот предмет!")
                player.playSound(Sound.BLOCK_ANVIL_BREAK)
                return@command
            } else if (findSharpeningStone(player) == null) {
                player.playSound(Sound.BLOCK_ANVIL_BREAK)
                player.errorMessage("У вас нету точильных камней!")
                return@command
            }
            generateButtons(player)
            menu.open(player)
        }
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        isCancelled = (app.getUser(player) ?: return).isArmorEquipment
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
                this.price = price.toLong()
                hint = "Заточить"
                description = """
                            §7Вы можете улучшить характеристики предмета на 1 единицу.
                            §cШанс разрушить точильный камень ${(chance * 100).toInt()}%
                        """.trimIndent()
                onClick { player, _, _ ->
                    val user = app.getUser(player) ?: return@onClick
                    val nmsItem = CraftItemStack.asNMSCopy(player.itemInHand)
                    val tag = nmsItem.tag
                    val sharpeningLevel = tag.getInt("sharpeningLevel")

                    user.armLock {
                        if (sharpeningLevel == 10) {
                            player.errorMessage("У вас максимальный уровень заточки!")
                            player.playSound(Sound.BLOCK_ANVIL_BREAK)
                            return@armLock
                        }

                        if (AttributeType.getAttributeWithNbt(tag).isEmpty()) {
                            return@armLock
                        }

                        user.tryPurchase(price, {
                            Anime.close(player)
                            if (Math.random() < chance && chance < 1.0) {
                                player.errorMessage("Точильный камень был разрушен")
                                player.playSound(Sound.BLOCK_ANVIL_DESTROY)
                            } else {
                                Anime.topMessage(player, "§aПредмет был заточен")
                                Glow.animate(player, 1.0, GlowColor.GREEN)

                                player.playSound(Sound.BLOCK_ANVIL_USE)

                                AttributeType.getAttributeWithNbt(tag).forEach {
                                    tag.setDouble(it.getObjectName(), tag.getDouble(it.getObjectName()) + 0.2)
                                    tag.setInt("sharpeningLevel", sharpeningLevel + 1)
                                }

                                player.itemInHand.setAmount(0)

                                val itemStack = nmsItem.asBukkitMirror()

                                setNewNameWithSharpening(itemStack)
                                AttributeUtil.setNewLoreWithAttributes(itemStack)

                                player.inventory.addItem(itemStack)
                            }
                            sharpening.setAmount(sharpening.getAmount() - 1)
                        }, "Недостаточно средств")
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
                ItemManager[tag.getString("address")].itemMeta?.displayName + if (sharpeningLevel == 0) "" else " §c+$sharpeningLevel"
        }
    }

    private fun findSharpeningStone(player: Player) = player.inventory.filterNotNull()
        .find {
            val nmsItem = CraftItemStack.asNMSCopy(it)
            nmsItem.hasTag() && nmsItem.tag.hasKeyOfType("sharpening_chance", 99)
        }
}
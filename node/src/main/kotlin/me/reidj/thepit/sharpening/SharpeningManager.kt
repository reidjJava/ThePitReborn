package me.reidj.thepit.sharpening

import me.func.mod.Anime
import me.func.mod.ui.Glow
import me.func.protocol.data.color.GlowColor
import me.reidj.thepit.app
import me.reidj.thepit.attribute.AttributeType
import me.reidj.thepit.attribute.AttributeUtil
import me.reidj.thepit.client
import me.reidj.thepit.item.ItemManager
import me.reidj.thepit.util.errorMessageOnChat
import me.reidj.thepit.util.hasKeyOfType
import me.reidj.thepit.util.playSound
import me.reidj.thepit.util.writeLog
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class SharpeningManager : Listener {

    @EventHandler(priority = EventPriority.LOW)
    fun InventoryClickEvent.handle() {
        if (action == InventoryAction.SWAP_WITH_CURSOR) {
            val player = whoClicked as Player
            val user = app.getUser(player) ?: return
            val sharpeningStone = CraftItemStack.asNMSCopy(cursor) ?: return
            val sharpeningTag = sharpeningStone.tag ?: return
            val sharpenedItem = CraftItemStack.asNMSCopy(currentItem) ?: return
            val sharpenedTag = sharpenedItem.tag ?: return
            val chance = sharpeningTag.getDouble("sharpening_chance")
            val price = sharpeningTag.getDouble("sharpening_price")
            val sharpeningLevel = sharpenedTag.getInt("sharpeningLevel")

            if (AttributeType.getAttributeWithNbt(sharpenedTag).isEmpty()) {
                isCancelled = true
                return
            }

            sharpeningStone.hasKeyOfType("sharpening_chance", 99) {
                if (sharpeningLevel == 10) {
                    player.errorMessageOnChat("У вас максимальный уровень заточки!")
                    player.playSound(Sound.BLOCK_ANVIL_BREAK)
                    isCancelled = true
                    return@hasKeyOfType
                }
                user.tryPurchase(price, {
                    if (Math.random() < chance && chance < 100.0) {
                        player.errorMessageOnChat("Точильный камень был разрушен.")
                        player.playSound(Sound.BLOCK_ANVIL_DESTROY)
                    } else {
                        Anime.topMessage(player, "§aПредмет был заточен.")
                        Glow.animate(player, 1.0, GlowColor.GREEN)

                        player.playSound(Sound.BLOCK_ANVIL_USE)

                        AttributeType.getAttributeWithNbt(sharpenedTag).forEach {
                            sharpenedTag.setDouble(it.getObjectName(), sharpenedTag.getDouble(it.getObjectName()) + 0.2)
                            sharpenedTag.setInt("sharpeningLevel", sharpeningLevel + 1)
                        }

                        val itemStack = sharpenedItem.asBukkitMirror()

                        setNewNameWithSharpening(itemStack)
                        AttributeUtil.setNewLoreWithAttributes(itemStack)

                        player.inventory.setItem(slot, itemStack)

                        client().writeLog(
                            "${player.name} заточил предмет ${itemStack.i18NDisplayName}! $sharpeningLevel -> ${
                                sharpenedTag.getInt(
                                    "sharpeningLevel"
                                )
                            }."
                        )
                    }
                    cursor.setAmount(cursor.getAmount() - 1)
                    isCancelled = true
                }, {
                    isCancelled = true
                    player.errorMessageOnChat("Недостаточно средств.")
                })
            }
        }
    }

    private fun setNewNameWithSharpening(itemStack: ItemStack) {
        val tag = CraftItemStack.asNMSCopy(itemStack).tag
        val sharpeningLevel = tag.getInt("sharpeningLevel")
        itemStack.itemMeta = itemStack.itemMeta.also { meta ->
            meta.displayName =
                ItemManager[tag.getString("address")].itemMeta?.displayName + if (sharpeningLevel == 0) "" else " §c+$sharpeningLevel"
        }
    }
}
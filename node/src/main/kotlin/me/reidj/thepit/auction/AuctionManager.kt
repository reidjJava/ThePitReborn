package me.reidj.thepit.auction

import me.func.mod.Anime
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.confirmation.Confirmation
import me.func.mod.ui.menu.selection
import me.func.mod.util.command
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.emoji.Emoji
import me.func.protocol.data.status.MessageStatus
import me.reidj.thepit.app
import me.reidj.thepit.client
import me.reidj.thepit.data.AuctionData
import me.reidj.thepit.item.ItemManager
import me.reidj.thepit.player.User
import me.reidj.thepit.protocol.AuctionPutLotPackage
import me.reidj.thepit.protocol.AuctionRemoveItemPackage
import me.reidj.thepit.protocol.MoneyDepositPackage
import me.reidj.thepit.util.*
import me.reidj.thepit.util.Formatter
import org.apache.commons.lang.math.NumberUtils
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import java.util.*

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class AuctionManager {

    companion object {
        private val auctionData = hashSetOf<AuctionData>()

        operator fun get(data: AuctionData) = auctionData.add(data)

        fun remove(uuid: UUID) = auctionData.removeIf { it.uuid == uuid }
    }

    private val auction = selection {
        title = "Аукцион"
        vault = Emoji.COIN
        hint = "Купить"
        rows = 4
        columns = 2
    }
    private val myItems = selection {
        title = "Мои предметы"
        vault = Emoji.COIN
        hint = "Снять лот"
        rows = 4
        columns = 2
    }
    private val mainGui = selection {
        title = "Аукцион"
        hint = "Открыть"
        rows = 2
        columns = 2
        buttons(
            button {
                title = "Торговать"
                onClick { player, _, _ ->
                    generateAuctionButtons(app.getUser(player) ?: return@onClick)
                    auction.open(player)
                }
            },
            button {
                title = "Мои предметы"
                texture = "minecraft:textures/items/minecart_chest.png"
                onClick { player, _, _ ->
                    generateMyItemsButtons(app.getUser(player) ?: return@onClick)
                    myItems.open(player)
                }
            }
        )
    }

    init {
        command("ah") { player, _ -> mainGui.open(player) }
        command("sell") { player, args ->
            val user = app.getUser(player) ?: return@command
            val itemInHand = player.itemInMainHand()
            val nmsItem = CraftItemStack.asNMSCopy(itemInHand)
            val tag = nmsItem.tag

            if (args.isEmpty()) {
                player.systemMessage(MessageStatus.ERROR, GlowColor.RED, "Использование §b/sell §6[Цена].")
                return@command
            } else if (!nmsItem.hasTag() || !tag.hasKeyOfType("isTrade", 99) || !tag.hasKeyOfType("address", 99)) {
                player.systemMessage(MessageStatus.ERROR, GlowColor.RED, "Вы не можете продать этот предмет!")
                return@command
            } else if (!NumberUtils.isNumber(args[0])) {
                player.systemMessage(MessageStatus.ERROR, GlowColor.RED, "Вы использовали недопустимый символ!")
                return@command
            }

            val price = args[0].toInt()

            if (price < 10 || price > 300000000) {
                player.systemMessage(
                    MessageStatus.ERROR,
                    GlowColor.RED,
                    "Значение слишком маленькое или слишком большое!"
                )
                return@command
            }

            val auctionData =
                AuctionData(UUID.randomUUID(), tag.getString("address"), player.displayName, player.uniqueId, price)

            user.stat.auctionData.add(auctionData)

            client().write(AuctionPutLotPackage(auctionData))

            itemInHand.setAmount(0)
            player.systemMessage(MessageStatus.FINE, GlowColor.GREEN, "Лот был создан.")
        }
    }

    private fun generateAuctionButtons(user: User) {
        auction.money = Formatter.toMoneyFormat(user.stat.money)
        auction.storage = auctionData.filter { user.stat.uuid != it.seller }.map {
            val itemStack = ItemManager[it.objectName] ?: return
            val displayName = itemStack.i18NDisplayName
            button {
                title = displayName
                description = "Продавец §b${it.sellerName}"
                price = it.price.toLong()
                item = itemStack
                hover(itemStack.itemMeta.lore)
                onClick { player, _, _ ->
                    Confirmation("Купить", displayName, "за ${it.price} ${Emoji.COIN}") { confirmPlayer ->
                        val confirmUser = app.getUser(confirmPlayer) ?: return@Confirmation
                        confirmUser.armLock {
                            confirmUser.tryPurchase(it.price.toDouble(), {
                                if (it in auctionData) {
                                    client().write(
                                        MoneyDepositPackage(
                                            it.seller,
                                            it.uuid,
                                            confirmPlayer.displayName,
                                            displayName,
                                            it.price
                                        )
                                    )

                                    auctionData.remove(it)

                                    confirmUser.giveMoney(-it.price.toDouble())

                                    confirmPlayer.inventory.addItem(itemStack)
                                } else {
                                    confirmPlayer.errorMessage("Этого лота не существует!")
                                    player.playSound(Sound.BLOCK_ANVIL_BREAK)
                                }
                            }, "Недостаточно средств")
                        }
                    }.open(player)
                }
            }
        }.toMutableList()
    }

    private fun generateMyItemsButtons(user: User) {
        myItems.storage = user.stat.auctionData.map {
            val itemStack = ItemManager[it.objectName] ?: return
            button {
                title = itemStack.i18NDisplayName
                item = itemStack
                price = it.price.toLong()
                hover(itemStack.itemMeta.lore)
                onClick { player, _, _ ->
                    val clickUser = app.getUser(player) ?: return@onClick
                    clickUser.armLock {
                        if (it !in clickUser.stat.auctionData) {
                            Anime.close(player)
                            return@armLock
                        }
                        client().write(AuctionRemoveItemPackage(it.uuid))
                        clickUser.stat.auctionData.remove(it)
                        player.inventory.addItem(itemStack)
                        Anime.close(player)
                    }
                }
            }
        }.toMutableList()
    }
}
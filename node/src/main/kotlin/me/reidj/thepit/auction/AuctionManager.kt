package me.reidj.thepit.auction

import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
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
import me.reidj.thepit.player.User
import me.reidj.thepit.protocol.*
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
                }
            },
            button {
                title = "Мои предметы"
                texture = "minecraft:textures/items/minecart_chest.png"
                onClick { player, _, _ -> generateMyItemsButtons(app.getUser(player) ?: return@onClick) }
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
            } else if (!nmsItem.hasTag() || !tag.hasKeyOfType("address", 8)) {
                player.systemMessage(MessageStatus.ERROR, GlowColor.RED, "Вы не можете продать этот предмет!")
                return@command
            } else if (!NumberUtils.isNumber(args[0])) {
                player.systemMessage(MessageStatus.ERROR, GlowColor.RED, "Вы использовали недопустимый символ!")
                return@command
            }

            val price = args[0].toInt()
            val now = System.currentTimeMillis() / 10000

            if (price < 10 || price > 300000000) {
                player.systemMessage(
                    MessageStatus.ERROR,
                    GlowColor.RED,
                    "Значение слишком маленькое или слишком большое!"
                )
                return@command
            }

            val auctionData =
                AuctionData(
                    UUID.randomUUID(),
                    player.uniqueId,
                    user.toBase64ItemStack(itemInHand),
                    player.displayName,
                    price,
                    now
                )

            client().write(AuctionPutLotPackage(auctionData))

            itemInHand.setAmount(0)
            player.systemMessage(MessageStatus.FINE, GlowColor.GREEN, "Лот был создан.")
        }
    }

    private fun generateAuctionButtons(user: User) {
        auction.money = Formatter.toMoneyFormat(user.stat.money)
        coroutine().launch {
            val auctionData =
                client().writeAndAwaitResponse<AuctionGetLotsPackage>(AuctionGetLotsPackage()).await().auctionData
            auction.storage = auctionData.filter { user.stat.uuid != it.seller }.map {
                val itemStack = user.toFromBase64ItemStack(it.item) ?: return@launch
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
                                    coroutine().launch click@{
                                        val response = client().writeAndAwaitResponse<AuctionItemPurchasedPackage>(
                                            AuctionItemPurchasedPackage(it.uuid)
                                        ).await()
                                        if (response.isBought) {
                                            Anime.close(confirmPlayer)
                                            confirmPlayer.errorMessage("Этого лота не существует!")
                                            confirmPlayer.playSound(Sound.BLOCK_ANVIL_BREAK)
                                            return@click
                                        }
                                        client().write(
                                            AuctionMoneyDepositPackage(
                                                it.seller,
                                                it.uuid,
                                                confirmPlayer.displayName,
                                                displayName,
                                                it.price
                                            )
                                        )
                                        client().write(AuctionRemoveItemPackage(it.uuid))

                                        confirmUser.giveMoney(-it.price.toDouble())

                                        confirmPlayer.inventory.addItem(itemStack)
                                    }
                                }, "Недостаточно средств")
                            }
                        }.open(player)
                    }
                }
            }.toMutableList()
            auction.open(user.player)
        }
    }

    private fun generateMyItemsButtons(user: User) {
        coroutine().launch {
            val auctionData =
                client().writeAndAwaitResponse<AuctionGetLotsPackage>(AuctionGetLotsPackage()).await().auctionData
            myItems.storage = auctionData.filter { it.seller == user.stat.uuid }.map {
                val itemStack = user.toFromBase64ItemStack(it.item) ?: return@launch
                button {
                    title = itemStack.i18NDisplayName
                    item = itemStack
                    price = it.price.toLong()
                    hover(itemStack.itemMeta.lore)
                    onClick { player, _, _ ->
                        val clickUser = app.getUser(player) ?: return@onClick
                        clickUser.armLock {
                            coroutine().launch click@{
                                val response = client().writeAndAwaitResponse<AuctionItemPurchasedPackage>(
                                    AuctionItemPurchasedPackage(it.uuid)
                                ).await()
                                if (response.isBought) {
                                    Anime.close(player)
                                    return@click
                                }
                                Anime.close(player)
                                player.inventory.addItem(itemStack)
                            }
                        }
                    }
                }
            }.toMutableList()
            myItems.open(user.player)
        }
    }
}
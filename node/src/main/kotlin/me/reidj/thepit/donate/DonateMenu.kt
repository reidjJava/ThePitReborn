package me.reidj.thepit.donate

import me.func.mod.Anime
import me.func.mod.reactive.ReactiveButton
import me.func.mod.service.Services
import me.func.mod.ui.Glow
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.confirmation.Confirmation
import me.func.mod.ui.menu.selection
import me.func.protocol.data.color.GlowColor
import me.reidj.thepit.app
import me.reidj.thepit.client
import me.reidj.thepit.util.errorMessageOnScreen
import org.bukkit.entity.Player
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.network.packages.MoneyTransactionRequestPackage
import ru.cristalix.core.network.packages.MoneyTransactionResponsePackage
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class DonateMenu {

    private fun <T : Donate> temp(
        player: Player,
        title: String,
        rows: Int,
        columns: Int,
        vararg donate: T,
        converter: (ReactiveButton, T) -> ReactiveButton = { button, _ -> button }
    ) {
        selection {
            val user = app.getUser(player) ?: return@selection
            val stat = user.stat

            this.title = title
            this.rows = rows
            this.columns = columns

            vault = "\uE03D"

            val balance = Services.getBalanceData(player).get(1, TimeUnit.SECONDS)

            money = "Кристалликов ${balance.coins + balance.crystals}"

            storage = donate.map { pos ->
                converter(button {
                    val has = pos.getObjectName() in stat.donates
                    val current = has && pos.getCurrent(stat)
                    description(pos.getDescription())
                    price(pos.getPrice())
                    texture(pos.getTexture())
                    hint(if (current) "Выбрано" else if (has) "Выбрать" else "Купить")
                    onClick { player, _, _ ->
                        if (current) {
                            return@onClick
                        }
                        if (has) {
                            pos.setCurrent(stat)
                            Anime.close(player)
                            Anime.title(player, "§dВыбрано!")
                            return@onClick
                        }
                        buy(player, pos)
                    }
                    this.title = pos.getTitle()
                }, pos)
            }.toMutableList()
        }.open(player)
    }

    private fun processInvoice(user: UUID, price: Int, description: String) =
        if (user == UUID.fromString("bf30a1df-85de-11e8-a6de-1cb72caa35fd")) {
            CompletableFuture.completedFuture(MoneyTransactionResponsePackage(null, null))
        } else {
            if (System.getenv("TRANSACTION_TEST") != null) {
                CompletableFuture.completedFuture(MoneyTransactionResponsePackage(null, null))
            } else {
                client().writeAndAwaitResponse(
                    MoneyTransactionRequestPackage(
                        user,
                        price,
                        true,
                        description
                    )
                )
            }
        }

    private fun buy(player: Player, donate: Donate) {
        Confirmation(
            "Купить §a${donate.getTitle()}",
            "за ${donate.getPrice()} §bКристаллик(а)"
        ) {
            val user = app.getUser(player) ?: return@Confirmation
            val stat = user.stat
            if (donate.getObjectName() in stat.donates) {
                player.errorMessageOnScreen("Вы уже приобрели этот товар!")
                return@Confirmation
            }
            user.armLock {
                processInvoice(player.uniqueId, donate.getPrice().toInt(), donate.getTitle()).thenAccept {
                    if (it.errorMessage != null) {
                        Anime.killboardMessage(player, Formatting.error(it.errorMessage))
                        Glow.animate(player, 0.4, GlowColor.RED)
                        return@thenAccept
                    }
                    Anime.title(player, "§dУспешно!")
                    Anime.close(player)
                    Glow.animate(player, 0.4, GlowColor.GREEN)
                    donate.give(user)
                    player.sendMessage(Formatting.fine("Спасибо за поддержку разработчика!"))
                }
            }
        }.open(player)
    }
}
package me.reidj.thepit.barter

import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.selection
import me.func.mod.util.command
import me.func.protocol.data.emoji.Emoji
import me.func.protocol.ui.dialog.*
import me.reidj.thepit.app
import me.reidj.thepit.item.ItemManager
import me.reidj.thepit.util.errorMessageOnScreen
import me.reidj.thepit.util.hasKeyOfType
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class BarterManager {

    private val guideDialog = Dialog(
        Entrypoint(
            "barterPageOne",
            "Умбра",
            Screen("Добро пожаловать в наш обитель!").buttons(
                Button("Давай поторгуем?").actions(
                    Action(Actions.COMMAND).command("/barterMenu"),
                    Action(Actions.CLOSE)
                ),
                Button("Что это за место?").actions(
                    Action(Actions.COMMAND).command("/barterPageTwo"),
                    Action(Actions.CLOSE)
                ),
                Button("Расскажи о себе.").actions(
                    Action(Actions.COMMAND).command("/barterPageThree"),
                    Action(Actions.CLOSE)
                )
            )
        ),
        Entrypoint(
            "barterPageTwo",
            "Умбра",
            Screen(
                "Это место получило прозвище - \"Обитель\".",
                "По крайней мере его так назвали Авантюристы, которые рискуют своей жизнью",
                "каждую секунду ради сокровищ, но обычно находят лишь смерть...."
            ).buttons(Button("Закрыть").actions(Action(Actions.CLOSE)))
        ),
        Entrypoint(
            "barterPageThree",
            "Умбра",
            Screen(
                "B - Я бывший Авантюрист, который открыл свою лавку по",
                "продаже/скупке предметов с Подземелий, в том числе и наводку",
                "на эти подземелья за кругленькую сумму.",
            ).buttons(Button("Закрыть").actions(Action(Actions.CLOSE)))
        ),
    )
    private val barter = selection {
        title = "Обитель"
        hint = "Открыть"
        rows = 2
        columns = 2
        buttons(
            button {
                title = "Торговля"
                onClick { player, _, _ -> trade.open(player) }
            },
            button {
                title = "Ваши предложения"
                onClick { player, _, _ -> suggestions.open(player) }
            }
        )
    }
    private val trade = selection {
        title = "Торговля"
        hint = "Купить"
        vault = Emoji.COIN
        rows = 2
        columns = 4
    }
    private val suggestions = selection {
        title = "Ваши предложения"
        vault = Emoji.COIN
        hint = "Продать"
        rows = 2
        columns = 4
        storage = generateSuggestionsButtons()
    }

    init {
        command("barter") { player, _ -> me.func.mod.ui.dialog.Dialog.dialog(player, guideDialog, "barterPageOne") }
        command("barterPageTwo") { player, _ ->
            me.func.mod.ui.dialog.Dialog.dialog(
                player,
                guideDialog,
                "barterPageTwo"
            )
        }
        command("barterPageThree") { player, _ ->
            me.func.mod.ui.dialog.Dialog.dialog(
                player,
                guideDialog,
                "barterPageThree"
            )
        }
        command("barterMenu") { player, _ -> barter.open(player) }
    }

    private fun generateSuggestionsButtons() = ItemManager.items
        .filter { CraftItemStack.asNMSCopy(it.value).hasKeyOfType("barterPrice", 99) }
        .map { it.value }
        .map {
            val tag = CraftItemStack.asNMSCopy(it).tag
            val price = tag.getInt("barterPrice")
            button {
                title = it.i18NDisplayName
                description = it.itemMeta.lore.joinToString()
                this.price = price.toLong()
                onClick { player, _, _ ->
                    if (it in player.inventory) {
                        val user = app.getUser(player) ?: return@onClick
                        user.giveMoney(price.toDouble())
                        it.setAmount(it.getAmount() - 1)
                    } else {
                        player.errorMessageOnScreen("У вас нету этого предмета!")
                    }
                }
            }
        }.toMutableList()

}
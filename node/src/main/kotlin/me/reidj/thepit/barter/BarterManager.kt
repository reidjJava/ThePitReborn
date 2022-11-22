package me.reidj.thepit.barter

import me.func.mod.util.command
import me.func.protocol.ui.dialog.*

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
                "По крайней мере его так назвали Авантюристы, чьё добро я скупаю с подземелий."
            ).buttons(Button("Закрыть").actions(Action(Actions.CLOSE)))
        ),
        Entrypoint(
            "barterPageThree",
            "Бартер",
            Screen(
                "B - Я бывший Авантюрист, который открыл свою лавку по",
                "продаже/скупке предметов с Подземелий, в том числе и наводку",
                "на эти подземелья за кругленькую сумму.",
            ).buttons(Button("Закрыть").actions(Action(Actions.CLOSE)))
        ),
    )

    init {
        command("barter") { player, _ -> me.func.mod.ui.dialog.Dialog.dialog(player, guideDialog, "barterPageOne") }
        command("barterPageTwo") { player, _ -> me.func.mod.ui.dialog.Dialog.dialog(player, guideDialog, "barterPageTwo") }
        command("barterPageThree") { player, _ ->  me.func.mod.ui.dialog.Dialog.dialog(player, guideDialog, "barterPageThree")}
    }
}
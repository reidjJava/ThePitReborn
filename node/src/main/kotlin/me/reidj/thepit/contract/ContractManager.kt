package me.reidj.thepit.contract

import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.selection
import me.func.mod.util.command
import me.reidj.thepit.app
import me.reidj.thepit.player.User

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

private const val UPDATE_TIME = 24

class ContractManager {

    private val menu = selection {
        title = "Контракты"
        hint = ""
    }

    companion object {
        fun update(user: User, value: Int, type: ContractType): Array<String>? {
            user.stat.contractsTypes.filter { it.type == type }.forEach {
                it.now += value
                if (it.now == it.condition) {
                    user.giveMoney(it.money)
                    return arrayOf("Контракт выполнен!", "Награда: §6${it.money} §fмонет")
                }
            }
            return null
        }
    }

    init {
        command("contracts") { player, _ ->
            val stat = (app.getUser(player) ?: return@command).stat
            val now = System.currentTimeMillis()

            // Если прошло 24 часа, то обновляем контракты
            if (now - stat.contractLastUpdate * 10000 > UPDATE_TIME * 60 * 60 * 1000) {
                stat.contractsTypes = ContractGenerator.generate()
                stat.contractLastUpdate = now / 10000
            }
            menu.storage = stat.contractsTypes.filter { it.now < it.condition }.map { contract ->
                button {
                    title = contract.title
                    description = contract.description
                }
            }.toMutableList()
            menu.open(player)
        }
    }
}
package me.reidj.thepit.player.prepare

import clepto.bukkit.Cycle
import com.destroystokyo.paper.Title
import me.func.protocol.data.emoji.Emoji
import me.reidj.thepit.app
import me.reidj.thepit.player.DefaultState
import me.reidj.thepit.player.State
import me.reidj.thepit.player.User
import me.reidj.thepit.util.resetLabelRotation

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class PrepareGuide : State, Prepare {

    private var dots = app.worldMeta.getLabels("guide")
    private val titles = arrayListOf<Title>()

    init {
        arrayOf(
            "Добро пожаловатьnextна режим ThePit!",
            "У Франка Вы можетеnextприобрести расходные предметы.",
            "У Умбры Вы найдёте весьмаnextинтересный товар. Обязательно загляните к нему!",
            "Не хватает ${Emoji.COIN}?nextТогда посмотрите контракты у Кюри!",
            "Удачи!"
        ).map {
            if ("next" in it) {
                val separated = it.split("next")
                Title(separated[0], separated[1])
            } else {
                Title(it)
            }
        }.forEach { titles.add(it) }

        dots.forEach { resetLabelRotation(it, 1) }
        dots = dots.sortedBy { it.tag.split(" ")[0] }
    }

    override fun execute(user: User) {
        if (!user.stat.isTutorialCompleted) {
            user.setState(PrepareGuide())
        }
    }

    override fun enterState(user: User) {
        val player = user.player
        Cycle.run(5 * 20, titles.size) {
            if (!player.isOnline) {
                Cycle.exit()
                return@run
            }
            if (it >= titles.size) {
                user.setState(DefaultState())
                Cycle.exit()
                return@run
            }
            player.sendTitle(titles[it])
            player.teleport(dots[it].toCenterLocation())
        }
    }

    override fun leaveState(user: User) {
        user.stat.isTutorialCompleted = true
    }

    override fun playerVisible() = false
}
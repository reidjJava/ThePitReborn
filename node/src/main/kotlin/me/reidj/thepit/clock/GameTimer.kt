package me.reidj.thepit.clock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.reidj.thepit.app
import me.reidj.thepit.attribute.AttributeUtil
import me.reidj.thepit.client
import org.bukkit.Bukkit

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/

@FunctionalInterface
interface ClockInject {
    fun run(tick: Int)
}

// Осторожно, если оно будет маленьким, счетчик не дойдет до Incomeble
private const val AUTO_SAVE_PERIOD = 20 * 60L * 10

class GameTimer(private val injects: List<ClockInject>) : () -> Unit {

    private var tick = 0

    private val scope = CoroutineScope(Dispatchers.Default)
    private val mutex = Mutex()

    override fun invoke() {
        if (mutex.isLocked) return
        scope.launch {
            mutex.withLock {
                savePlayers()
                injects.forEach { it.run(tick) }
            }
        }
    }

    private fun savePlayers() {
        if (tick % AUTO_SAVE_PERIOD == 0L) {
            tick = 1
            client().write(app.playerDataManager.bulkSave())
        } else {
            tick++
            if (tick % 20 == 0) {
                Bukkit.getOnlinePlayers().forEach {
                    AttributeUtil.updateAllAttributes(it, AttributeUtil.getAllItems(it))
                }
            }
        }
    }
}
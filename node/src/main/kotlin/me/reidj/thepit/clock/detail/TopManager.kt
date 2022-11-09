package me.reidj.thepit.clock.detail

import com.google.common.collect.Maps
import me.reidj.thepit.app
import me.reidj.thepit.client
import me.reidj.thepit.clock.ClockInject
import me.reidj.thepit.protocol.TopPackage
import me.reidj.thepit.top.TopEntry
import org.bukkit.Location
import ru.cristalix.boards.bukkitapi.Board
import ru.cristalix.boards.bukkitapi.Boards
import ru.cristalix.core.GlobalSerializers
import java.text.DecimalFormat
import java.util.*

private const val DATA_COUNT = 10
private const val UPDATE_SECONDS = 30

class TopManager : ClockInject {

    private val tops = Maps.newConcurrentMap<String, List<TopEntry<String, String>>>()
    private val boards = Maps.newConcurrentMap<String, Board>()

    private val topDataFormat = DecimalFormat("###,###,###")

    private fun newBoard(title: String, fieldName: String, x: Double, y: Double, z: Double, yaw: Float) =
        Boards.newBoard().also {
            it.addColumn("#", 20.0)
            it.addColumn("Игрок", 110.0)
            it.addColumn(fieldName, 80.0)
            it.title = title
            it.location = Location(app.worldMeta.world, x, y, z, yaw, 0F)
        }.also(Boards::addBoard)

    private fun updateData() {
        for (field in boards.keys) {
            client().writeAndAwaitResponse<TopPackage>(TopPackage(field, DATA_COUNT)).thenAcceptAsync { topPackage ->
                tops[field] = topPackage.entries.map {
                    TopEntry(if (it.displayName == null) "ERROR" else it.displayName!!, topDataFormat.format(it.value))
                }
            }
        }
    }

    override fun run(tick: Int) {
        if (tick % (20 * UPDATE_SECONDS) != 0)
            return
        updateData()
        val data = GlobalSerializers.toJson(tops)
        if ("{}" == data || data == null) return
        boards.forEach { (field, top) ->
            top.clearContent()
            var counter = 0
            if (tops[field] == null) return@forEach
            tops[field]!!.forEach {
                counter++
                top.addContent(
                    UUID.randomUUID(),
                    "" + counter,
                    it.key,
                    it.value
                )
            }
            top.updateContent()
        }
    }
}
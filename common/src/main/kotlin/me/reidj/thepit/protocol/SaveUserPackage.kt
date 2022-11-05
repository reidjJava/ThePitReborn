package me.reidj.thepit.protocol

import me.reidj.thepit.data.Stat
import ru.cristalix.core.network.CorePackage
import java.util.*

/**
 * @project : tower
 * @author : Рейдж
 **/
data class SaveUserPackage(val uuid: UUID, val stat: Stat): CorePackage()

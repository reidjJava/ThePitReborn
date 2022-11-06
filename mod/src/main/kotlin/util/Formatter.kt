package util

import java.text.DecimalFormat

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object Formatter {

    private val doubleFormat = DecimalFormat("#,###.##")

    fun toFormat(double: Double): String = doubleFormat.format(double)
}
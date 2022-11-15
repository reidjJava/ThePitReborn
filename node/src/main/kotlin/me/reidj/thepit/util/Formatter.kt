package me.reidj.thepit.util

import java.text.DecimalFormat

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object Formatter {

    private val doubleFormat = DecimalFormat("#,###.##")
    private val moneyFormat = DecimalFormat("###,###,###,###,###,###.##")

    fun toFormat(double: Double): String = doubleFormat.format(double)

    fun toMoneyFormat(money: Double): String = moneyFormat.format(money)
}
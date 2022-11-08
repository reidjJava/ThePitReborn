package me.reidj.thepit.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

fun coroutine() = CoroutineScope(Dispatchers.IO)
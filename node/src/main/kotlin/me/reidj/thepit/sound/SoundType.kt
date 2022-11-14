package me.reidj.thepit.sound

import me.func.sound.Category
import me.func.sound.Sound
import org.bukkit.entity.Player

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

private const val STORAGE = "https://storage.c7x.dev/reidj/thepit/"

enum class SoundType {
    USE_POTION,
    ;

    fun send(player: Player) = Sound("${STORAGE}${name.lowercase()}.ogg")
        .category(Category.BLOCKS)
        .pitch(1.0f)
        .volume(1.5f)
        .repeating(false)
        .send(player)
}
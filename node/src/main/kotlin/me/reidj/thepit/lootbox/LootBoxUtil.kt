package me.reidj.thepit.lootbox

import me.func.mod.Anime
import me.func.mod.conversation.data.LootDrop
import org.bukkit.entity.Player

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
object LootBoxUtil {

    fun openLootBox(lootBox: String, player: Player) {
        for (type in LootBoxManager.get(lootBox)!!.drops) {
            if (Math.random() > type.chance) {
                Anime.openLootBox(player, LootDrop(type.itemStack, type.title, type.rare))
                break
            }
        }
    }
}
package me.reidj.thepit.clock.detail

import me.reidj.thepit.attribute.AttributeType
import me.reidj.thepit.attribute.AttributeUtil
import me.reidj.thepit.clock.ClockInject
import me.reidj.thepit.player.prepare.PreparePlayerBrain
import org.bukkit.Bukkit

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class PlayerRegeneration : ClockInject {

    override fun run(tick: Int) {
        if (tick % 400 == 0) {
            Bukkit.getOnlinePlayers()
                .filter { it.inventory.armorContents.isNotEmpty() }
                .forEach {
                    val armorContents = AttributeUtil.getAllItems(it)
                    PreparePlayerBrain.setHealth(
                        it, AttributeUtil.getAttributeValue(
                            AttributeType.REGENERATION,
                            armorContents
                        )
                    )
                    AttributeUtil.updateAllAttributes(it, armorContents)
                }
        }
    }
}
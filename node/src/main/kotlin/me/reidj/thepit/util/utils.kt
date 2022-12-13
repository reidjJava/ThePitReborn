package me.reidj.thepit.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.func.mod.Anime
import me.func.mod.ui.Glow
import me.func.mod.util.after
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.status.MessageStatus
import me.func.world.Label
import me.reidj.thepit.app
import me.reidj.thepit.player.User
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomSoundEffect
import net.minecraft.server.v1_12_R1.SoundCategory
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_12_R1.CraftSound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.formatting.Formatting

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/

private val barrier: ItemStack by lazy { ItemStack(Material.BARRIER) }

fun coroutine() = CoroutineScope(Dispatchers.IO)

fun Player.errorMessageOnScreen(subTitle: String) {
    Glow.animate(this, 2.0, GlowColor.RED)
    Anime.itemTitle(this, barrier, "Ошибка", subTitle, 2.0)
    Anime.close(this)
}

fun Player.errorMessageOnChat(message: String) {
    Glow.animate(this, 2.0, GlowColor.RED)
    sendMessage(Formatting.error("Ошибка! $message"))
}

fun Location.getBlockAt() = app.getWorld().getBlockAt(this.clone().also {
    it.x += 0.5
    it.z += 0.5
})

fun Player.systemMessage(messageStatus: MessageStatus, color: GlowColor, text: String) {
    Glow.animate(this, 2.0, color)
    Anime.systemMessage(this, messageStatus, text)
}

fun Player.playSound(sound: Sound) = playSound(location, sound, 1F, 1F)

fun Player.topInventory(): Inventory = openInventory.topInventory

fun Player.itemInMainHand(): ItemStack = inventory.itemInMainHand

fun Player.itemInOffHand(): ItemStack = inventory.itemInOffHand

fun Player.teleportAndPlayMusic(location: Location) {
    after(5) { teleport(location) }
    playSound(Sound.ENTITY_SHULKER_TELEPORT)
}

fun User.playSound(sound: Sound, x: Double, y: Double, z: Double) {
    sendPacket(
        PacketPlayOutCustomSoundEffect(
            CraftSound.getSound(sound),
            SoundCategory.MASTER,
            x,
            y,
            z,
            1f,
            1f
        )
    )
}

fun ItemStack.isWeapon(): Boolean {
    val name = getType().name
    if (name.endsWith("AXE") || name.endsWith("SWORD")) {
        return true
    }
    return false
}

fun resetLabelRotation(input: Label, characterOffset: Int): Label {
    var offSet = characterOffset
    val ss = input.tag.split(" ")
    input.setYaw(ss[characterOffset].toFloat())
    input.setPitch(ss[++offSet].toFloat())
    return input
}

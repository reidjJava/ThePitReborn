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
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_12_R1.CraftSound
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
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

fun Location.getBlockAt(): Block = app.getWorld().getBlockAt(this.clone().also {
    it.x += 0.5
    it.z += 0.5
})

fun Action.rightClick() = this == Action.RIGHT_CLICK_AIR || this == Action.RIGHT_CLICK_BLOCK

fun Player.systemMessage(messageStatus: MessageStatus, color: GlowColor, text: String) {
    Glow.animate(this, 2.0, color)
    Anime.systemMessage(this, messageStatus, text)
}

fun Player.playSound(sound: Sound) = playSound(location, sound, 1F, 1F)

fun Player.itemInMainHand(): ItemStack = inventory.itemInMainHand

fun Player.setItemInMainHand(itemInHand: ItemStack) {
    inventory.itemInMainHand = itemInHand
}

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

fun net.minecraft.server.v1_12_R1.ItemStack.hasKeyOfType(s: String, i: Int, acceptAction: () -> Unit) {
    if (hasTag() && tag.hasKeyOfType(s, i)) {
        acceptAction()
    }
}

fun net.minecraft.server.v1_12_R1.ItemStack.hasKeyOfType(s: String, i: Int): Boolean {
    return hasTag() && tag.hasKeyOfType(s, i)
}

fun ItemStack.isWeapon(): Boolean {
    val name = getType().name
    val nmsItem = CraftItemStack.asNMSCopy(this)
    val tag = nmsItem.tag

    if (name.endsWith("AXE") || name.endsWith("SWORD") || nmsItem.hasTag() && tag.hasKeyOfType("isArtefact", 3)) {
        return true
    }
    return false
}

fun Label.resetLabelRotation(characterOffset: Int): Label {
    var offSet = characterOffset
    val ss = tag.split(" ")
    setYaw(ss[characterOffset].toFloat())
    setPitch(ss[++offSet].toFloat())
    return this
}

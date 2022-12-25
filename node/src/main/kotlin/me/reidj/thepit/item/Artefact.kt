package me.reidj.thepit.item

import dev.implario.bukkit.item.ItemBuilder

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class Artefact(override val itemBuilder: ItemBuilder) : Item() {

    override fun init(objectName: String) {
        val artefactPath = "${path}$objectName.isArtefact"
        if (configuration.isInt(artefactPath)) {
            itemBuilder.nbt("isArtefact", configuration.getInt(artefactPath))
        }
    }
}
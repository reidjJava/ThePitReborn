package mob

import dev.xdark.clientapi.entity.EntityPlayer
import dev.xdark.clientapi.event.entity.EntityDespawn
import dev.xdark.clientapi.event.entity.EntitySpawn
import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readUtf8

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class SkinManager {

    private val entitySkins = hashMapOf<Int, String>()

    init {
        mod.registerChannel("thepit:mob-skin") {
            entitySkins[readInt()] = readUtf8()
        }

        mod.registerHandler<EntitySpawn> {
            if (entity is EntityPlayer) return@registerHandler
            val id = entity.entityId
            if (id in entitySkins) {
                entity.renderTexture =
                    ResourceLocation.of("minecraft", "mcpatcher/cit/thepit/mobs/${entitySkins[id]}.png")
            }
        }

        mod.registerHandler<EntityDespawn> {
            if (entity is EntityPlayer) return@registerHandler
            entitySkins.remove(entity.entityId)
        }
    }
}
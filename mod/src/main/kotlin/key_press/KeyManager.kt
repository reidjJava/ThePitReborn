package key_press

import dev.xdark.clientapi.event.input.KeyPress
import io.netty.buffer.Unpooled
import org.lwjgl.input.Keyboard
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine

/**
 * @project : ThePitReborn
 * @author : Рейдж
 **/
class KeyManager {

    init {
        mod.registerHandler<KeyPress> {
            if (key == Keyboard.KEY_O) {
                UIEngine.clientApi.clientConnection().sendPayload("thepit:key-press", Unpooled.EMPTY_BUFFER)
            }
        }
    }
}
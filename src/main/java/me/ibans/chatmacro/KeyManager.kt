package me.ibans.chatmacro

import com.google.gson.Gson
import me.ibans.chatmacro.util.ForgeUtils
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.io.File

object KeyManager {

    const val CATEGORY = "chatmacro.macro"
    private const val PROFILE_EXTENSION = ".profile"

    val keybindings = mutableListOf<KeyBinding>()

    @SubscribeEvent
    fun onKeyInput(ev: TickEvent.ClientTickEvent) {
        keybindings.forEach {
            if (it.isPressed || it.isKeyDown) {
                ForgeUtils.sendChatMessage(it.keyDescription)
            }
        }
    }

    fun saveKeybindProfile(name: String = "current.cfg", custom: Boolean): Boolean {
        val keys = mutableListOf<StoredKey>()
        keybindings.forEach {
            keys.add(StoredKey(it.keyCode, it.keyDescription))
        }

        val json = Gson().toJson(keys)
        val file = if (custom) {
            File(ChatMacro.saveDirectory + name + PROFILE_EXTENSION)
        } else {
            File(ChatMacro.saveDirectory + name)
        }
        file.writeText(json)

        return true
    }

    fun loadKeybindProfile(name: String) {
        val file = File(ChatMacro.saveDirectory + name)
        val json = file.readText()
        val data = Gson().fromJson(json, Array<StoredKey>::class.java)

        keybindings.clear()

        data.forEach {
            keybindings.add(KeyBinding(it.message, it.key, CATEGORY))
        }
    }

}

data class StoredKey(val key: Int, val message: String)
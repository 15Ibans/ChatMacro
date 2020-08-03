package me.ibans.chatmacro

import com.google.gson.Gson
import me.ibans.chatmacro.util.ForgeUtils
import me.ibans.chatmacro.util.sendChatMessage
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import java.io.File

object KeyManager {

    private const val PROFILE_EXTENSION = ".profile"

    val keybindings = mutableMapOf<Int, KeyInfo>()

    @SubscribeEvent
    fun onKeyInput(ev: TickEvent.ClientTickEvent) {
        keybindings.forEach {
            if (ForgeUtils.minecraft.inGameHasFocus && it.value.spammable && Keyboard.isKeyDown(it.key)) {
                sendChatMessage(it.value.message)
            } else if (ForgeUtils.minecraft.inGameHasFocus && !it.value.spammable && Keyboard.isKeyDown(it.key) && !it.value.isPressed) {
                it.value.isPressed = true
                sendChatMessage(it.value.message)
            } else if (ForgeUtils.minecraft.inGameHasFocus && !it.value.spammable
                    && !Keyboard.getEventKeyState()
                    && Keyboard.getEventKey() == it.key
                    && it.value.isPressed) {
                it.value.isPressed = false
            }
        }
    }

    fun saveKeybindProfile(name: String = "current.cfg", custom: Boolean): Boolean {
        val keys = mutableListOf<StoredKey>()
        keybindings.forEach {
            keys.add(StoredKey(it.key, it.value.spammable, it.value.message))
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
            keybindings[it.key] = KeyInfo(it.spammable, it.message)
        }
    }

}

data class StoredKey(val key: Int, val spammable: Boolean, val message: String)

data class KeyInfo(val spammable: Boolean, val message: String, var isPressed: Boolean = false)
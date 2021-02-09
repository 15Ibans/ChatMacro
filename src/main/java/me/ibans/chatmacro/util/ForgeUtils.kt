package me.ibans.chatmacro.util

import me.ibans.chatmacro.ChatVariableManager
import me.ibans.chatmacro.util.ForgeUtils.format
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText

object ForgeUtils {

    val minecraft: Minecraft = Minecraft.getMinecraft()
    private val pattern = "(&)([0123456789abcdefklmnor])".toPattern()

    val String.format: String
        get() {
            val matcher = pattern.matcher(this)
            return matcher.replaceAll("§$2")
        }

}

fun sendChatMessage(message: String) {
    var toSend = message

    ChatVariableManager.builtInVars.forEach {
        toSend = toSend.replace(it.key, it.value.invoke() ?: "")
    }
    ChatVariableManager.assignableVars.forEach {
        toSend = toSend.replace("{${it.key}}", it.value)
    }
    
    ForgeUtils.minecraft.thePlayer.sendChatMessage(toSend)
}

fun messagePlayer(message: String) {
    ForgeUtils.minecraft.thePlayer.addChatMessage(ChatComponentText(message.format))
}
package me.ibans.chatmacro.util

import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText

object ForgeUtils {

    private val minecraft: Minecraft = Minecraft.getMinecraft()
    private val pattern = "(&)([0123456789abcdefklmnor])".toPattern()

    val String.format: String
        get() {
            val matcher = pattern.matcher(this)
            return matcher.replaceAll("§$2")
        }

    fun sendChatMessage(message: String) {
        minecraft.thePlayer.sendChatMessage(message)
    }

    fun messagePlayer(message: String) {
        minecraft.thePlayer.addChatMessage(ChatComponentText(message.format))
    }

}
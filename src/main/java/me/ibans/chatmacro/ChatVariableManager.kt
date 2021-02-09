package me.ibans.chatmacro

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import me.ibans.chatmacro.util.ForgeUtils
import java.io.File

object ChatVariableManager {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    val builtInVars = mapOf("{n}" to { nearestPlayer }, "{r}" to { randomPlayer })
    var assignableVars = mutableMapOf<String, String>()

    private val nearestPlayer: String?
        get() {
            val player = ForgeUtils.minecraft.theWorld.getClosestPlayerToEntity(ForgeUtils.minecraft.thePlayer, -1.0) ?: return null
            return player.displayNameString
        }

    private val randomPlayer: String?
        get() {
            val user = ForgeUtils.minecraft.thePlayer
            val otherPlayers = ForgeUtils.minecraft.thePlayer.sendQueue.playerInfoMap.filter { it.gameProfile.name != user.gameProfile.name }
            return if (otherPlayers.isNotEmpty()) otherPlayers.random().gameProfile.name else null
        }

    fun loadChatVariables() {
        val savedData = File(ChatMacro.saveDirectory + "chatvariables")
        val type = object : TypeToken<Map<String, String>>() {}.type
        val data = if (savedData.exists()) gson.fromJson(savedData.readText(), type) else emptyMap<String, String>()
        assignableVars = data.toMutableMap()
    }

    fun saveChatVariables() {
        val json = gson.toJson(assignableVars)
        File(ChatMacro.saveDirectory + "chatvariables").writeText(json)
    }

}
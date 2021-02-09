package me.ibans.chatmacro

import me.ibans.chatmacro.command.ChatVarCommand
import me.ibans.chatmacro.command.KeyCodeCommand
import me.ibans.chatmacro.command.MacroCommand
import me.ibans.chatmacro.util.FileUtils
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.io.File

@Mod(modid = ChatMacro.MODID, version = ChatMacro.VERSION)
class ChatMacro {

    companion object {
        const val MODID = "chatmacro"
        const val VERSION = "1.0"

        var saveDirectory: String? = null
    }

    @Mod.EventHandler
    fun preInit(ev: FMLPreInitializationEvent) {
        saveDirectory = ev.modConfigurationDirectory.absolutePath + "/$MODID/"
        FileUtils.createFolderIfNotExists(File(saveDirectory ?: throw Exception("Save directory is null")))

        if (File(saveDirectory + "current.cfg").exists()) {
            KeyManager.loadKeybindProfile("current.cfg")
        }
        ChatVariableManager.loadChatVariables()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        ClientCommandHandler.instance.registerCommand(MacroCommand())
        ClientCommandHandler.instance.registerCommand(KeyCodeCommand())
        ClientCommandHandler.instance.registerCommand(ChatVarCommand())

        MinecraftForge.EVENT_BUS.register(KeyManager)
        MinecraftForge.EVENT_BUS.register(KeyCodeCommand.Companion)
    }


}
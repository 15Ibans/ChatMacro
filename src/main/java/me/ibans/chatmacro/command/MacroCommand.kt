package me.ibans.chatmacro.command

import me.ibans.chatmacro.ChatMacro
import me.ibans.chatmacro.KeyInfo
import me.ibans.chatmacro.KeyManager
import me.ibans.chatmacro.util.ChatUtil
import me.ibans.chatmacro.util.TabCompletion
import me.ibans.chatmacro.util.messagePlayer
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommand
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.util.BlockPos
import org.lwjgl.input.Keyboard
import java.io.File

class MacroCommand : CommandBase(), ICommand {

    private val usages = listOf("add", "remove", "saveprofile", "loadprofile", "listprofiles", "list", "clear")
    private val wrongUsage = "/macro <${usages.joinToString(", ")}>"

    private val loadedMacros: () -> List<String> = {
        KeyManager.keybindings.keys.map { Keyboard.getKeyName(it) }.toList()
    }

    private val savedProfiles: () -> List<String> = {
        File(ChatMacro.saveDirectory ?: throw Exception("Save directory is null"))
                .walk()
                .filter { it.absolutePath.endsWith(".profile") }
                .map { it.name }
                .map { it.removeSuffix(".profile") }
                .toList()
    }

    private val tabCompletions = TabCompletion(mapOf(
            "@" to { usages },
            "remove" to loadedMacros,
            "loadprofile" to savedProfiles,
            "add *" to { listOf("true", "false") }
    ))

    override fun getCommandName(): String {
        return "macro"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun getCommandUsage(sender: ICommandSender?): String? {
        return null
    }

    override fun addTabCompletionOptions(sender: ICommandSender?, args: Array<String>, pos: BlockPos?): List<String>? {
        return tabCompletions.getTabCompletion(args)
    }

    override fun processCommand(sender: ICommandSender?, args: Array<String>) {
        if (sender == null) return

        if (args.isNullOrEmpty()) {
            throw WrongUsageException(wrongUsage)
        }

        when (args[0].toLowerCase()) {
            "add" -> {
                if (args.size < 4) throw WrongUsageException("/macro add <keycode> <message>")
                addMacro(args)
            }
            "remove" -> {
                if (args.size < 2) throw WrongUsageException("/macro remove <keycode>")
                removeMacro(args)
            }
            "saveprofile" -> {
                if (args.size < 2) throw WrongUsageException("/macro saveprofile <profile name>")
                val profileName = ChatUtil.argsToString(args, 1) ?: return
                KeyManager.saveKeybindProfile(profileName, true)
                messagePlayer("&aSaved profile &e$profileName")
            }
            "loadprofile" -> {
                if (args.size < 2) throw WrongUsageException("/macro loadprofile <profile name>")
                val profileName = ChatUtil.argsToString(args, 1).plus(".profile")
                KeyManager.loadKeybindProfile(profileName)
                messagePlayer("&aLoaded profile &e${profileName.removeSuffix(".profile")}")
            }
            "listprofiles" -> {
                val files = savedProfiles.invoke()
                if (files.isEmpty()) {
                    return messagePlayer("&cThere are currently no saved macro profiles")
                } else {
                    messagePlayer("&aThe following macro profiles are currently saved: ")
                    files.forEach { 
                        messagePlayer("&e- $it")
                    }
                }
            }
            "list" -> {
                listMacros()
            }
            "clear" -> {
                KeyManager.keybindings.clear()
                KeyManager.saveKeybindProfile(custom = false)
                messagePlayer("&aCleared all loaded macros")
            }
            else -> throw WrongUsageException(wrongUsage)
        }

    }

    private fun addMacro(args: Array<String>) {
        val keycode = Keyboard.getKeyIndex(args[1].toUpperCase())
        val isSpammable = args[2].toBoolean()
        if (keycode == Keyboard.KEY_NONE) return messagePlayer("&cEnter a valid key (find key name using /keycode).")

        val message = ChatUtil.argsToString(args, 3) ?: return

        val exists = KeyManager.keybindings.containsKey(keycode)

        KeyManager.keybindings.remove(keycode)
        KeyManager.keybindings[keycode] = KeyInfo(isSpammable, message)

        KeyManager.saveKeybindProfile(custom = false)

        if (exists) {
            messagePlayer("&eRemapped keybind &a${Keyboard.getKeyName(keycode)} &eto another message")
        } else {
            messagePlayer("&eAdded keybind using key &a${Keyboard.getKeyName(keycode)}")
        }
    }

    private fun removeMacro(args: Array<String>) {
        val keycode = Keyboard.getKeyIndex(args[1].toUpperCase())

        KeyManager.keybindings.remove(keycode)
        KeyManager.saveKeybindProfile(custom = false)

        messagePlayer("&eRemoved any keybinds using key &a${Keyboard.getKeyName(keycode)}")
    }

    private fun listMacros() {
        if (KeyManager.keybindings.isEmpty()) {
            messagePlayer("&cYou have no assigned macros")
        } else {
            messagePlayer("&eThe following keybinds are currently loaded:")
            KeyManager.keybindings.forEach {
                messagePlayer("${Keyboard.getKeyName(it.key)}: &a${it.value.message} &d(Spammable: ${it.value.spammable})")
            }

        }
    }


}
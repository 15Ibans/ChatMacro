package me.ibans.chatmacro.command

import me.ibans.chatmacro.KeyManager
import me.ibans.chatmacro.util.ChatUtil
import me.ibans.chatmacro.util.ForgeUtils
import net.minecraft.client.settings.KeyBinding
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommand
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import org.lwjgl.input.Keyboard

class MacroCommand : CommandBase(), ICommand {

    private val wrongUsage = "/macro <add, delete, list, saveprofile, loadprofile>"

    override fun getCommandName(): String {
        return "macro"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun getCommandUsage(sender: ICommandSender?): String? {
        return null
    }

    override fun processCommand(sender: ICommandSender?, args: Array<String>) {
        if (sender == null) return

        if (args.isNullOrEmpty()) {
            throw WrongUsageException(wrongUsage)
        }

        when (args[0]) {
            "add" -> {
                if (args.size < 3) throw WrongUsageException("/macro add <keycode> <message>")
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
                ForgeUtils.messagePlayer("&aSaved profile &e$profileName")
            }
            "loadprofile" -> {
                if (args.size < 2) throw WrongUsageException("/macro loadprofile <profile name>")
                val profileName = ChatUtil.argsToString(args, 1).plus(".profile")
                KeyManager.loadKeybindProfile(profileName)
                ForgeUtils.messagePlayer("&aLoaded profile &e$profileName")
            }
            "list" -> {
                listMacros()
            }
            "clear" -> {
                KeyManager.keybindings.clear()
                ForgeUtils.messagePlayer("&aCleared all loaded macros")
            }
            else -> throw WrongUsageException(wrongUsage)
        }

    }

    private fun addMacro(args: Array<String>) {
        val keycode = Keyboard.getKeyIndex(args[1])
        if (keycode == Keyboard.KEY_NONE) return ForgeUtils.messagePlayer("&cEnter a valid key (find key name using /keycode).")
        val message = ChatUtil.argsToString(args, 2)

        val keybind = KeyBinding(message, keycode, KeyManager.CATEGORY)

        val exists = KeyManager.keybindings.any { it.keyCode == keycode }

        KeyManager.keybindings.removeAll { it.keyCode == keycode }
        KeyManager.keybindings.add(keybind)

        KeyManager.saveKeybindProfile(custom = false)

        if (exists) {
            ForgeUtils.messagePlayer("&eRemapped keybind &a${Keyboard.getKeyName(keycode)} &eto another message")
        } else {
            ForgeUtils.messagePlayer("&eAdded keybind using key &a${Keyboard.getKeyName(keycode)}")
        }
    }

    private fun removeMacro(args: Array<String>) {
        val keycode = Keyboard.getKeyIndex(args[1])

        KeyManager.keybindings.removeAll { it.keyCode == keycode }

        ForgeUtils.messagePlayer("&eRemoved any keybinds using key &a${Keyboard.getKeyName(keycode)}")
    }

    private fun listMacros() {
        if (KeyManager.keybindings.isEmpty()) {
            ForgeUtils.messagePlayer("&cYou have no assigned macros")
        } else {
            ForgeUtils.messagePlayer("&eThe following keybinds are currently loaded:")
            KeyManager.keybindings.forEach {
                ForgeUtils.messagePlayer("${Keyboard.getKeyName(it.keyCode)}: &a${it.keyDescription}")
            }

        }
    }


}
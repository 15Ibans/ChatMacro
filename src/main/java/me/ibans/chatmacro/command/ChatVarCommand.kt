package me.ibans.chatmacro.command

import me.ibans.chatmacro.ChatVariableManager
import me.ibans.chatmacro.util.ChatUtil
import me.ibans.chatmacro.util.TabCompletion
import me.ibans.chatmacro.util.messagePlayer
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommand
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.util.BlockPos

class ChatVarCommand : CommandBase(), ICommand {

    private val wrongUsage = "/chatvar <assign, remove, list>"

    private val tabCompletions = TabCompletion(mapOf(
            "@" to { listOf("assign", "remove", "list") },
            "assign" to { ChatVariableManager.assignableVars.keys.toList() },
            "remove" to { ChatVariableManager.assignableVars.keys.toList() }
    ))

    override fun getCommandName(): String {
        return "chatvar"
    }

    override fun getCommandUsage(sender: ICommandSender?): String? {
        return null
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun addTabCompletionOptions(sender: ICommandSender?, args: Array<String>, pos: BlockPos?): List<String> {
        return tabCompletions.getTabCompletion(args)
    }

    override fun processCommand(sender: ICommandSender?, args: Array<String>) {
        if (sender == null) return

        if (args.isNullOrEmpty()) {
            throw WrongUsageException(wrongUsage)
        }

        when (args[0].toLowerCase()) {
            "assign" -> {
                if (args.size < 3) throw WrongUsageException("/chatvar assign <variableName> <text>")
                addChatVariable(args)
            }
            "remove" -> {
                if (args.size < 2) throw WrongUsageException("/chatvar remove <variableName>")
                removeChatVariable(args)
            }
            "list" -> {
                val assignedVars = ChatVariableManager.assignableVars
                if (assignedVars.keys.isEmpty()) {
                    return messagePlayer("&eThere are currently no registered chat variables")
                }
                messagePlayer("&eThe following chat variables are currently registered:")
                assignedVars.forEach { (name, value) ->
                    messagePlayer("&a$name &9-> &a$value")
                }
            }
        }
    }

    private fun addChatVariable(args: Array<String>) {
        var exists = false

        val varName = args[1]
        if (varName == "r" || varName == "n") {
            return messagePlayer("&cYou can't use the built in variables &er&c or &en")
        }
        val varValue = ChatUtil.argsToString(args, 2) ?: return

        if (ChatVariableManager.assignableVars.containsKey(varName)) {
            exists = true
        }

        ChatVariableManager.assignableVars[varName] = varValue
        ChatVariableManager.saveChatVariables()
        if (!exists) {
            messagePlayer("&aSuccessfully registered variable &e$varName &awith value &e$varValue")
        } else {
            messagePlayer("&aVariable &e$varName &ahas been reassigned to &e$varValue")
        }
    }

    private fun removeChatVariable(args: Array<String>) {
        val varName = args[1]
        ChatVariableManager.assignableVars.remove(varName)
        messagePlayer("&aRemoved any registered variable with the name $varName")
    }
}
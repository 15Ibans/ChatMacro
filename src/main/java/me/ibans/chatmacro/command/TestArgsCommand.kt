package me.ibans.chatmacro.command

import me.ibans.chatmacro.util.ChatUtil
import me.ibans.chatmacro.util.messagePlayer
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommand
import net.minecraft.command.ICommandSender

class TestArgsCommand : CommandBase(), ICommand {
    override fun getCommandName(): String {
        return "testargs"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "bruh moment"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun processCommand(sender: ICommandSender?, args: Array<String>) {
        ChatUtil.argsToString(args, 0)?.let { messagePlayer(it) }
    }


}
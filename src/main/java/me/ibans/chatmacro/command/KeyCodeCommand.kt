package me.ibans.chatmacro.command

import me.ibans.chatmacro.util.ForgeUtils
import me.ibans.chatmacro.util.messagePlayer
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommand
import net.minecraft.command.ICommandSender
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import org.lwjgl.input.Keyboard

class KeyCodeCommand : CommandBase(), ICommand {

    companion object {
        private var isGettingKeyCode = false

        @SubscribeEvent
        fun onKeyPress(ev: InputEvent.KeyInputEvent) {
            if (Keyboard.getEventKeyState() && isGettingKeyCode) {
                isGettingKeyCode = false
                val key = Keyboard.getEventKey()
                messagePlayer("&eGot key code: &a${Keyboard.getKeyName(key)}")
            }
        }
    }

    override fun getCommandName(): String {
        return "keycode"
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun getCommandUsage(sender: ICommandSender?): String? {
        return null
    }

    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
        messagePlayer("&eYou will receive the key code of the next key you press.")
        isGettingKeyCode = true
    }


}
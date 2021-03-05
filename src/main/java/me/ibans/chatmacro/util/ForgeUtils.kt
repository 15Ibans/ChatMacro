package me.ibans.chatmacro.util

import me.ibans.chatmacro.ChatVariableManager
import me.ibans.chatmacro.util.ForgeUtils.format
import me.ibans.chatmacro.util.ForgeUtils.minecraft
import net.minecraft.client.Minecraft
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent

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
    
    minecraft.thePlayer.sendChatMessage(toSend)
}

fun messagePlayer(message: String) {
    minecraft.thePlayer.addChatMessage(ChatComponentText(message.format))
}

fun messagePlayer(apply: ComponentBuilder.() -> Unit) {
    messagePlayer(ComponentBuilder().apply(apply))
}

fun messagePlayer(chatComponent: IChatComponent) {
    minecraft.thePlayer.addChatComponentMessage(chatComponent)
}

fun messagePlayer(builder: ComponentBuilder) {
    minecraft.thePlayer.addChatComponentMessage(builder.construct())
}

open class ComponentBuilder {
    private val builders = mutableListOf<BaseComponentBuilder>()

    open operator fun String.unaryPlus(): BaseComponentBuilder {
        val componentBuilder = BaseComponentBuilder().apply {
            text = format
        }
        builders.add(componentBuilder)
        return componentBuilder
    }

    infix fun BaseComponentBuilder.hoverMessage(message: String): BaseComponentBuilder {
        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText(message.format))
        return this
    }

    infix fun BaseComponentBuilder.clickCommand(command: String): BaseComponentBuilder {
        clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
        return this
    }

    infix fun BaseComponentBuilder.suggestCommand(command: String): BaseComponentBuilder {
        clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)
        return this
    }

    infix fun BaseComponentBuilder.openUrl(url: String): BaseComponentBuilder {
        clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, url)
        return this
    }

    fun construct(): IChatComponent {
        val main = ChatComponentText("")
        builders.forEach { builder ->
            main.appendSibling(builder.build())
        }
        return main
    }

    fun addBuilder(builder: BaseComponentBuilder) {
        builders.add(builder)
    }

}

class BaseComponentBuilder {
    var text = ""
    var hoverEvent: HoverEvent? = null
    var clickEvent: ClickEvent? = null

    fun build(): IChatComponent {
        val text = ChatComponentText(text)
        text.forEach {
            it.chatStyle.chatHoverEvent = hoverEvent
            it.chatStyle.chatClickEvent = clickEvent
        }
        return text
    }

}
package me.ibans.chatmacro.util

import net.minecraft.command.CommandBase
import kotlin.reflect.KFunction

object ChatUtil {

    fun argsToString(args: Array<String>, startPos: Int): String? {
        var string = ""
        for (i in startPos until args.size) {
            string += args[i] + " "
        }
        string = string.trim { it <= ' ' }
        return string
    }

    fun argsToString(args: List<String>, startPos: Int): String? {
        var string = ""
        for (i in startPos until args.size) {
            string += args[i] + " "
        }
        string = string.trim { it <= ' ' }
        return string
    }

}

class TabCompletion(private val completionInfo: Map<String, () -> List<String>>, val command: String)  {

    fun getTabCompletion(args: Array<String>): List<String> {
        if (args.size <= 1) {
            val completionList = completionInfo[completionInfo.keys.find { it == command }]?.invoke()?.sorted() ?: emptyList()
            return CommandBase.getListOfStringsMatchingLastWord(args, *completionList.toTypedArray())
        }
        val argsList = args.toMutableList()
        argsList.remove(argsList.last())
        val string = ChatUtil.argsToString(argsList.toTypedArray(), 0) ?: return emptyList()
        val completionList = completionInfo[completionInfo.keys.find { it.removePrefix("$command ") == string || it == processWildcard(argsList)}]?.invoke()?.sorted()?.toTypedArray() ?: emptyArray()
        return if (completionList.isEmpty()) emptyList() else CommandBase.getListOfStringsMatchingLastWord(args, *completionList)
    }

    fun processWildcard(args: List<String>): String? {
        println(args)
        completionInfo.keys.map { it.removePrefix("$command ").split(" ") }
                .filter { it.size == args.size && it.contains("*")}
                .forEach {
                    it.forEachIndexed list@{ i, s ->
                        if (s != args[i] && s != "*") return@list
                        if ((s == args[i] || s == "*") && i == args.size - 1) {
                            val appended = listOf(command) + it
                            return ChatUtil.argsToString(appended, 0)
                        }
                    }
                }
        return null
    }
}
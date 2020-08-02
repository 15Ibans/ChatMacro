package me.ibans.chatmacro.util

object ChatUtil {

    fun argsToString(args: Array<String>, startPos: Int): String? {
        var string = ""
        for (i in startPos until args.size) {
            string += args[i] + " "
        }
        string = string.trim { it <= ' ' }
        return string
    }

}
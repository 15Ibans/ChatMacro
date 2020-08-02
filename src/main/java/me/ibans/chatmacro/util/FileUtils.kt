package me.ibans.chatmacro.util

import java.io.File

object FileUtils {

    fun createFolderIfNotExists(directory: File) {
        if (!directory.exists()) {
            println("$directory does not exist, creating it now")
            directory.mkdir()
        }
    }

}
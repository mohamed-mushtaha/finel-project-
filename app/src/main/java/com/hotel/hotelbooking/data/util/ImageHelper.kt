package com.hotel.hotelbooking.data.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageHelper {

    private const val DIR = "images"

    fun saveToInternal(context: Context, uri: Uri, fileName: String): String? {
        return try {
            val dir = File(context.filesDir, DIR).also { it.mkdirs() }
            val file = File(dir, "$fileName.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun delete(path: String) {
        val file = File(path)
        if (file.exists()) file.delete()
    }

    fun exists(path: String?): Boolean = !path.isNullOrBlank() && File(path).exists()
}

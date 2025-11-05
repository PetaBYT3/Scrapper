package com.xliiicxiv.scrapper.extension

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import java.io.OutputStream

fun saveFile(
    context: Context,
    path: String,
    fileName: String
): OutputStream? {
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.RELATIVE_PATH, path)
    }

    try {
        val uri = resolver.insert(
            MediaStore.Files.getContentUri("external"),
            contentValues
        )

        return if (uri != null) {
            resolver.openOutputStream(uri)
        } else {
            null
        }
    } catch (e: Exception) {
        return null
    }
}
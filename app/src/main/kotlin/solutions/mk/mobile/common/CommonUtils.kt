package solutions.mk.mobile.common

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import solutions.mk.mobile.config.ApplicationConfig
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@Throws(IOException::class)
fun copy(source: InputStream, target: OutputStream) {
    val buf = ByteArray(get<ApplicationConfig>().copyFileByteBufferSize)
    var length: Int
    while (source.read(buf).also { length = it } > 0) {
        target.write(buf, 0, length)
    }
}

@SuppressLint("Range")
fun takeFullFileName(contentUri: Uri): String =
    getAndroid<ContentResolver>().query(contentUri)?.let {
        it.use { cursor ->
            cursor.moveToFirst()
            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    } ?: ""

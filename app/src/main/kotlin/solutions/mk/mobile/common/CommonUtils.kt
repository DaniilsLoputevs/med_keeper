package solutions.mk.mobile.common

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import solutions.mk.mobile.config.ApplicationConfig
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.random.Random

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


/* Android extensions */


/**
 * This function must call before you calling [android.view.View.setOnClickListener].
 *
 * IMPORTANT! - property by lazy  - it's not work!!!
 *
 * @see [SoF][<https://stackoverflow.com/questions/64476827/how-to-resolve-the-error-lifecycleowners-must-call-register-before-they-are-sta>]
 */
fun <CONTRACT, LAUNCH_PARAM, CALLBACK_PARAM> AppCompatActivity.registryAction(
    activityContract: CONTRACT,
    activityCallback: ActivityResultCallback<CALLBACK_PARAM>
): ActivityResultLauncher<LAUNCH_PARAM>
        where CONTRACT : ActivityResultContract<LAUNCH_PARAM, CALLBACK_PARAM> =
    activityResultRegistry.register(Random.nextDouble().toString(), this, activityContract, activityCallback)

fun ContentResolver.query(
    uri: Uri,
    projection: Array<String?>? = null,
    selection: String? = null,
    selectionArgs: Array<String?>? = null,
    sortOrder: String? = null
) = this.query(uri, projection, selection, selectionArgs, sortOrder)

fun <R> PdfDocument.use(block: (PdfDocument) -> R): R =
    try {
        val rsl = block(this)
        this.close()
        rsl
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(getAndroid(), "Something wrong: $e", Toast.LENGTH_LONG).show()
        throw e
    }

fun textWatcherOnTextChanged(block: (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit): TextWatcher =
    object : TextWatcher by EmptyTextWatcher() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int): Unit =
            block(s, start, before, count)
    }

class EmptyTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable?) {}
}


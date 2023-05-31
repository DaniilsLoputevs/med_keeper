package solutions.mk.mobile.common

import android.content.ContentResolver
import android.content.Context
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import kotlin.random.Random

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

/**
 * Get localized value for String Resource by resourceId from file with string value.
 * Examples: ./res/values/string.xml, ./res/values-ru/string.xml
 * Use it as private field injection.
 * ```
 * private val strFinalFilenameSuffix by strResource(R.string.activity__import_from_device__final_filename_suffix)
 * ```
 * @param resourceId - example: R.string.validation__required_field
 */
fun strResource(resourceId: Int): Lazy<String> = lazy { getStrResource(resourceId) }

/**
 * Get localized value for String Resource by resourceId from file with string value.
 * Examples: ./res/values/string.xml, ./res/values-ru/string.xml
 * @param resourceId example: `R.string.validation__required_field`
 */
fun getStrResource(resourceId: Int): String =
    try {
        get<Context>().getString(resourceId)
    } catch (e: Throwable) {
        e.printStackTrace()
        Log.e("getStringResource", e.toString())
        ""
    }


fun <T> T.addTextWatcherTextChanged(block: (s: CharSequence, start: Int, before: Int, count: Int) -> Unit): T where T : TextView =
    this.also { it.addTextChangedListener(textWatcherOnTextChanged(block)) }

fun <T> T.addTextWatcherAfterTextChanged(block: (s: Editable) -> Unit): T where T : TextView =
    this.also { it.addTextChangedListener(textWatcherOnAfterTextChanged(block)) }


fun textWatcherOnTextChanged(block: (s: CharSequence, start: Int, before: Int, count: Int) -> Unit): TextWatcher =
    object : EmptyTextWatcher() {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int): Unit =
            block(s, start, before, count)
    }

fun textWatcherOnAfterTextChanged(block: (s: Editable) -> Unit): TextWatcher =
    object : EmptyTextWatcher() {
        override fun afterTextChanged(s: Editable): Unit = block(s)
    }


abstract class EmptyTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {}
}


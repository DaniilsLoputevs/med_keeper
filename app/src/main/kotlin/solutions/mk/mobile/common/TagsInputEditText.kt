package solutions.mk.mobile.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import solutions.mk.mobile.R
import java.text.MessageFormat
import java.util.*


/**
 * Copy from YT: https://www.youtube.com/watch?v=FxDc1AdjXqs
 * Source (Java): https://github.com/Everyday-Programmer/Tags-input-Layout-Android/blob/main/app/src/main/java/com/example/tagstextinput/TagsInputEditText.java
 */
class TagsInputEditText(context: Context, attributeSet: AttributeSet?) :
    TextInputEditText(context, attributeSet) {
    var textWatcher: TextWatcher? = null
    var lastString: String? = null
    var separator = " "

    init {
        init()
    }

    private fun init() {
        movementMethod = LinkMovementMethod.getInstance()
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val thisString = editable.toString()
                if (thisString.isNotEmpty() && thisString != lastString) {
                    format()
                }
            }
        }
        addTextChangedListener(textWatcher)
    }

    private fun format() {
        val stringBuilder = SpannableStringBuilder()
        val fullString = Objects.requireNonNull(text).toString()
        val strings = fullString.split(separator.toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        for (i in strings.indices) {
            val s = strings[i]
            stringBuilder.append(s)
            if (fullString[fullString.length - 1] != separator[0] && i == strings.size - 1) {
                break
            }
            val bitmapDrawable = convertViewToDrawable(createTokenView(s)) as BitmapDrawable
            bitmapDrawable.setBounds(0, 0, bitmapDrawable.intrinsicWidth, bitmapDrawable.intrinsicHeight)
            val startIdx = stringBuilder.length - s.length
            val endIdx = stringBuilder.length
            val span: ClickableSpan = ClickableSpan(startIdx, endIdx)
            stringBuilder.setSpan(span, Math.max(endIdx - 2, startIdx), endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            stringBuilder.setSpan(ImageSpan(bitmapDrawable), startIdx, endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (i < strings.size - 1) {
                stringBuilder.append(separator)
            } else if (fullString[fullString.length - 1] == separator[0]) {
                stringBuilder.append(separator)
            }
        }
        lastString = stringBuilder.toString()
        setText(stringBuilder)
        setSelection(stringBuilder.length)
    }

    fun createTokenView(text: String?): View {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.HORIZONTAL
        val view: View = LayoutInflater.from(context).inflate(R.layout.token_layout, layout, false)
        val chip = view.findViewById<Chip>(R.id.chip)
        chip.text = text
        layout.addView(chip)
        return layout
    }

    fun convertViewToDrawable(view: View): Any {
        val spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        view.measure(spec, spec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.translate(-view.scrollX.toFloat(), -view.scrollY.toFloat())
        view.draw(canvas)
        view.isDrawingCacheEnabled = true
        val cache = view.drawingCache
        val viewBitmap = cache.copy(Bitmap.Config.ARGB_8888, true)
        view.destroyDrawingCache()
        return BitmapDrawable(context.resources, viewBitmap)
    }

    private inner class ClickableSpan(var startIdx: Int, var endIdx: Int) : android.text.style.ClickableSpan() {
        override fun onClick(view: View) {
            val s = Objects.requireNonNull(text).toString()
            val s1 = s.substring(0, startIdx)
            val s2 = s.substring(Math.min(endIdx + 1, s.length - 1))
            this@TagsInputEditText.setText(MessageFormat.format("{0}{1}", s1, s2))
        }
    }
}
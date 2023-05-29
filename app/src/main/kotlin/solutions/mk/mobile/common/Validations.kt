package solutions.mk.mobile.common

import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout


// todo - move to String resource for i18n
const val requiredFieldMsg = "required field!"

/**
 * Validation for Layout and TextInput
 */
fun Pair<TextInputLayout, AppCompatEditText>.addValidationRequiredField() =
    this.also { (layout, editText) ->
        editText.addTextWatcherTextChanged { text, _, _, _ ->
            println("change - text = $text")
            layout.error = if (text.isBlank()) requiredFieldMsg else null
        }
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) return@setOnFocusChangeListener
            val text = editText.text ?: ""
            println("focus - text = $text")
            layout.error = if (text.isBlank()) requiredFieldMsg else null
        }
    }

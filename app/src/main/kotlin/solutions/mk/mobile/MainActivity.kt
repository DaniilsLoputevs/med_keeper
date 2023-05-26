package solutions.mk.mobile

import android.net.Uri
import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.koin.android.ext.android.inject
import solutions.mk.mobile.common.getAndroid
import solutions.mk.mobile.common.registryAction
import solutions.mk.mobile.common.textWatcherOnTextChanged
import solutions.mk.mobile.config.KoinConfig
import solutions.mk.mobile.persist.dao.*
import solutions.mk.mobile.persist.sqlBlocking
import solutions.mk.mobile.service.RecordFileService

// todo - take out to other activity(select and add file)
class MainActivity : AppCompatActivity() {
    private val recordFileService: RecordFileService by inject()
    private val recordRepo: RecordRepo by inject()
    private val groupRepo: GroupRepo by inject()
    private val recordAndGroupRelationRepo: RecordAndGroupRelationRepo by inject()

    // todo - some type of preview of selected images
    //          show grid of cards with uploaded images
    //          https://codelabs.developers.google.com/codelabs/mdc-102-kotlin#4
    private val selectImagesButton: MaterialButton by lazy { findViewById(R.id.selectImages) }

    // todo - show to user result filename with extension + replace space chars
    //          field: "doc file"
    //          show : "doc_file.pdf"
    private val recordFileNameInput: TextInputEditText by lazy { findViewById(R.id.recordFileName) }
    private val recordDescriptionInput: TextInputEditText by lazy { findViewById(R.id.recordDescription) }

    // todo - show to user result groups
    //          field: "aaa, bbb,ccc" - whole string
    //          show : [aaa,bbb,ccc] - String array
    private val recordGroupInput: TextInputEditText by lazy { findViewById(R.id.recordGroups) }
    private val submitSelectButton: MaterialButton by lazy { findViewById(R.id.submitButton) }


    private val imagesUris: MutableLiveData<List<Uri>> = MutableLiveData(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        KoinConfig(this).startKoin()
        try {
            super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    } catch (e: Exception) {
        e.printStackTrace()
        Thread.sleep(1500)
        throw e
    }

        // todo - https://stackoverflow.com/questions/11535011/edittext-field-is-required-before-moving-on-to-another-activity
        //        last answer
        recordFileNameInput.addTextChangedListener(requiredFieldTextWatcher())
        recordGroupInput.addTextChangedListener(requiredFieldTextWatcher())

        val selectImagesAction = registryAction(GetMultipleContents()) { imagesUris.value = it }
        selectImagesButton.setOnClickListener { selectImagesAction.launch("image/*") } // todo - "*/*" select any file type
        submitSelectButton.setOnClickListener(::submitSelectImages)
    }


    /** @see [View.OnClickListener] */
    private fun submitSelectImages(view: View) {
        val imageUris = imagesUris.value ?: emptyList()
        val groups = recordGroupInput.text.toString().split(",").map(::GroupEntity)
        val fileNameWithoutExtension = recordFileNameInput.text.toString()
        val description = recordDescriptionInput.text.toString()

        // validation
        var isFail = false
        if (imageUris.isEmpty()) {
            selectImagesButton.error = "This field is required!"
            isFail = true
        }
        if (groups.isEmpty()) {
            recordFileNameInput.error = "This field is required!"
            isFail = true
        }
        if (fileNameWithoutExtension.isBlank()) {
            recordFileNameInput.error = "This field is required!"
            isFail = true
        }
        if (isFail) return


        val recordFile = recordFileService.saveRecordFilePdfFromImages(fileNameWithoutExtension, imageUris)
        // todo - make async + show loading spinner while waiting for sql finish.
        sqlBlocking {
            recordRepo.insertAll(RecordEntity(recordFile.name, description))
            groupRepo.insertAll(groups)
            val relations = groups.map { RecordAndGroupRelation(fileName = recordFile.name, groupName = it.name) }
            recordAndGroupRelationRepo.insertAll(relations)

            println("records       = ${sqlBlocking { recordRepo.getAll() }}")
            println("imageUris[0]  = ${imageUris[0]}}")
            println("groups        = ${sqlBlocking { groupRepo.getAll() }}")
            println("relations     = ${sqlBlocking { recordAndGroupRelationRepo.getAll() }}")
            Toast.makeText(getAndroid(), "file saved successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requiredFieldTextWatcher(): TextWatcher =
        textWatcherOnTextChanged { text, _, _, _ ->
            // todo - move to String resource for i18n
            if (text.isNullOrBlank()) recordFileNameInput.error = "This field is required!"
        }

}
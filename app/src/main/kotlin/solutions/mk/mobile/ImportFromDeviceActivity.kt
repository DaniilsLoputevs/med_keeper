package solutions.mk.mobile

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.android.ext.android.inject
import solutions.mk.mobile.common.*
import solutions.mk.mobile.components.RecordGroupsInputEditText
import solutions.mk.mobile.persist.dao.*
import solutions.mk.mobile.persist.sqlBlocking
import solutions.mk.mobile.service.RecordFileService

/**
 * Activity: Select one or more img file from external store (device memory) and convert it to one PDF file.
 * Finally save Records based on this PDF file.
 * TODO - expected extension way - add photo to list selected files.
 * TODO - expected extension way - show previews of selected images as a grid of photos.
 * TODO - expected extension way - split to 2 parts(probably ActivityFragments) 1 - select list images, 2 - select solo file(PDF, WORD, mp4, etc...)
 */
class ImportFromDeviceActivity : AppCompatActivity() {
    private val recordFileService: RecordFileService by inject()
    private val recordRepo: RecordRepo by inject()
    private val groupRepo: GroupRepo by inject()
    private val recordAndGroupRelationRepo: RecordAndGroupRelationRepo by inject()

    private val strRecordSaveSuccessfully by strResource(R.string.activity__import_from_device__record_saved_successfully)
    private val strFinalFilenameSuffix by strResource(R.string.activity__import_from_device__final_filename_suffix)
    private val strThisFilenameIsAlreadyInUse by strResource(R.string.validation__this_filename_is_already_in_use)

    /**
     * TODO - (for activity/fragment about Select Images)
     *          show grid of cards with uploaded images
     *          https://codelabs.developers.google.com/codelabs/mdc-102-kotlin#4
     * TODO - (middle priority) validations:
     *          Required field
     */
    private val selectImagesButton: MaterialButton by lazy { findViewById(R.id.selectImages_button) }
    private val selectedImagesUris: MutableLiveData<List<Uri>> = MutableLiveData(emptyList())

    private val recordFileNameLayout: TextInputLayout by lazy { findViewById(R.id.recordFileName_layout) }
    private val recordFileNameInput: TextInputEditText by lazy { findViewById(R.id.recordFileName_input) }

    // TODO - ? make it a TextArea?
    private val recordDescriptionInput: TextInputEditText by lazy { findViewById(R.id.recordDescription_input) }

    /**
     * TODO - Split packages by ENTER not by SPACE
     *          NOW:  package split == ' ' - SPACE
     *          NEED: package split == System.lineSeparator() - ENTER
     *          REASON: ' ' == SPACE - this char can be use in package name.
     *          User can create a new package(tag name == packageName) while adding file.
     *          P.s. It will fix bug/feature that after press ENTER new tags move to new line and next new tags
     *              has strange behavior for user.
     * TODO - (low priority) ? Maybe highlight package-tags what exists already?
     */
    private val recordGroupsLayout: TextInputLayout by lazy { findViewById(R.id.recordGroups_layout) }
    private val recordGroupsInput: RecordGroupsInputEditText by lazy { findViewById(R.id.recordGroups_input) }

    private val submitButton: MaterialButton by lazy { findViewById(R.id.submit_button) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_from_device)

        initActivityUI()
    }

    private fun initActivityUI() {
        // todo - "*/*" select any file type
        val selectImagesAction = registryAction(GetMultipleContents()) { selectedImagesUris.value = it }
        selectImagesButton.setOnClickListener { selectImagesAction.launch("image/*") }

        // todo - https://stackoverflow.com/questions/11535011/edittext-field-is-required-before-moving-on-to-another-activity
        //          last answer
        (recordFileNameLayout to recordFileNameInput).addValidationRequiredField()
        recordFileNameInput.addTextWatcherAfterTextChanged(::updateHelpTextResultFileName)

        (recordGroupsLayout to recordGroupsInput).addValidationRequiredField()

        submitButton.setOnClickListener(::submitSelectImages)

        Log.i(this.javaClass.simpleName, "# initActivityUI() - END")
    }


    /** @see [View.OnClickListener] */
    private fun submitSelectImages(view: View) {
        val imageUris = selectedImagesUris.value ?: emptyList()
        val groups = recordGroupsInput.groupsFromText.map(::GroupEntity)
        val fileNameWithoutExtension = recordFileNameInput.text.toString()
        val description = recordDescriptionInput.text.toString()

        // validation
        var isFail = false
        if (imageUris.isEmpty()) {
            selectImagesButton.error = requiredFieldMsg
            isFail = true
        }
        if (groups.isEmpty()) {
            recordGroupsLayout.error = requiredFieldMsg
            isFail = true
        }
        if (fileNameWithoutExtension.isBlank()) {
            recordFileNameLayout.error = requiredFieldMsg
            isFail = true
        }
        "$fileNameWithoutExtension.pdf".also { fullFilename ->
            if (recordFileService.isExistsFileWithName(fullFilename) ||
                sqlBlocking { recordRepo.containsByName(fullFilename) }
            ) {
                recordFileNameLayout.error = strThisFilenameIsAlreadyInUse
                isFail = true
            }
        }

        if (isFail) return


        val pdfFilename = recordFileService.saveRecordFilePdfFromImages(fileNameWithoutExtension, imageUris).name
        sqlBlocking {
            recordRepo.insertAll(RecordEntity(pdfFilename, description))
            groups.forEach { groupRepo.insertIfNotExists(it) }
            val relations = groups.map { RecordAndGroupRelation(fileName = pdfFilename, groupName = it.name) }
            recordAndGroupRelationRepo.insertAll(relations)
        }
        sqlBlocking {
            println("records       = ${sqlBlocking { recordRepo.getAll() }}")
            println("imageUris[0]  = ${imageUris[0]}}")
            println("groups        = ${sqlBlocking { groupRepo.getAll() }}")
            println("relations     = ${sqlBlocking { recordAndGroupRelationRepo.getAll() }}")
        }

        Toast.makeText(this, strRecordSaveSuccessfully, Toast.LENGTH_SHORT).show()
        switchActivityTo<MainActivity>()
    }

    private fun updateHelpTextResultFileName(s: Editable) {
        if (s.isNotBlank()) recordFileNameLayout.helperText = "$s.pdf - $strFinalFilenameSuffix"
    }

}
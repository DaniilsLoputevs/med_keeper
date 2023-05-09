package solutions.mk.mobile

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import solutions.mk.mobile.common.Global
import solutions.mk.mobile.common.registryAction
import solutions.mk.mobile.service.RecordFileService


class MainActivity : AppCompatActivity() {

    // TODO : Inject!
    private val recordFileService: RecordFileService = Global.recordFileService

    private val pickImageTV: TextView by lazy { findViewById(R.id.imageTextView) }
    private val imageView: ImageView by lazy { findViewById(R.id.imageView) }
    private val pdfTextView: TextView by lazy { findViewById(R.id.selectedPdf) }

    private lateinit var selectOneImageAction: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Global.init(this)

        selectOneImageAction = registryAction(ActivityResultContracts.GetMultipleContents(), ::selectImagesCallback)
        pickImageTV.setOnClickListener { selectOneImageAction.launch("image/*") } // todo - "*/*" select any file type
    }

    private fun selectImagesCallback(uris: List<Uri>) {
        imageView.setImageURI(uris[0])
        val fileName = "test-record-file-01" // todo - получать от юзера
        recordFileService.saveRecordFile(fileName, uris)
    }

}
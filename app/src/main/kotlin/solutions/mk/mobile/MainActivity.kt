package solutions.mk.mobile

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import solutions.mk.mobile.common.registryAction
import solutions.mk.mobile.config.KoinConfig
import solutions.mk.mobile.service.RecordFileService


class MainActivity : AppCompatActivity() {
    private val recordFileService: RecordFileService by inject()

    private val pickImageTV: TextView by lazy { findViewById(R.id.imageTextView) }
    private val imageView: ImageView by lazy { findViewById(R.id.imageView) }
    private val pdfTextView: TextView by lazy { findViewById(R.id.selectedPdf) }

    private lateinit var selectOneImageAction: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        KoinConfig.startKoin(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectOneImageAction = registryAction(ActivityResultContracts.GetMultipleContents(), ::selectImagesCallback)
        pickImageTV.setOnClickListener { selectOneImageAction.launch("image/*") } // todo - "*/*" select any file type
    }

    private fun selectImagesCallback(uris: List<Uri>) {
        imageView.setImageURI(uris[0])
        val fileName = "test-record-file-01" // todo - получать от юзера
        recordFileService.saveRecordFile(fileName, uris)
    }

}
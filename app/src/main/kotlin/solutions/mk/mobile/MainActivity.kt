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
import solutions.mk.mobile.persist.dao.RecordEntity
import solutions.mk.mobile.persist.dao.RecordRepo
import solutions.mk.mobile.persist.sql
import solutions.mk.mobile.service.RecordFileService
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val recordFileService: RecordFileService by inject()
    private val recordRepo: RecordRepo by inject()

    private val pickImageTV: TextView by lazy { findViewById(R.id.imageTextView) }
    private val imageView: ImageView by lazy { findViewById(R.id.imageView) }
    private val pdfTextView: TextView by lazy { findViewById(R.id.selectedPdf) }

    private lateinit var selectOneImageAction: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        KoinConfig(this).startKoin()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectOneImageAction = registryAction(ActivityResultContracts.GetMultipleContents(), ::selectImagesCallback)
        pickImageTV.setOnClickListener { selectOneImageAction.launch("image/*") } // todo - "*/*" select any file type
    }

    private fun selectImagesCallback(uris: List<Uri>) {
        imageView.setImageURI(uris[0])

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val fileName = "test-record-file-$currentDate" // todo - получать от юзера

        val recordFile = recordFileService.saveRecordFile(fileName, uris)
        sql {
            recordRepo.insertAll(RecordEntity(recordFile.name, "stub-desc", "stub-issuer"))
        }

        println("records = ${sql { recordRepo.getAll() }}")
    }

}
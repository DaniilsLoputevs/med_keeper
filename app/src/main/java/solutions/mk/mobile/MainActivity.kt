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


class MainActivity : AppCompatActivity() {
    private val pickImageTV: TextView by lazy { findViewById(R.id.imageTextView) }
    private val imageView: ImageView by lazy { findViewById(R.id.imageView) }
    private val pdfTextView: TextView by lazy { findViewById(R.id.selectedPdf) }

    private lateinit var selectOneImageAction: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Global.init(this)

        selectOneImageAction = registryAction(ActivityResultContracts.GetContent(), ::selectOneImageCallback)
//        pickImageTV.setOnClickListener { selectOneImageAction.launch("*/*") } // todo - select any file type
        pickImageTV.setOnClickListener { selectOneImageAction.launch("image/*") }
    }

    private fun selectOneImageCallback(uri: Uri) {
        imageView.setImageURI(uri)
        Global.recordFileService.saveRecordFile(uri)
    }

}
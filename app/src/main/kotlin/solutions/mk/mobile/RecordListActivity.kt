package solutions.mk.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import solutions.mk.mobile.common.get
import solutions.mk.mobile.common.getAndroid
import solutions.mk.mobile.components.AnyAdapter
import solutions.mk.mobile.persist.dao.*
import solutions.mk.mobile.persist.sqlBlocking
import solutions.mk.mobile.service.RecordFileService
import tools.PrintTable


class RecordListActivity : AppCompatActivity() {
    private val recordFileService: RecordFileService by inject()

    private val recordsListView: ListView by lazy { findViewById(R.id.act_record_list__lv__records) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_list)

        initActivityUI()
        Log.i(this.javaClass.simpleName, "# onCreate() - END")
    }

    private fun initActivityUI() {
        val groupName = intent.getStringExtra(EXTRA__RECORD_GROUP_NAME) ?: ""
        val records = sqlBlocking { get<RecordRepo>().getAllByGroupName(groupName) }
//        val records = sqlBlocking { get<RecordRepo>().getAll() }

        PrintTable.of(records)
            .name("RecordRepo")
            .columnElemIndex()
            .column("fileName") { it.fileName }
            .column("description") { it.description }
            .print()
        PrintTable.of(sqlBlocking { get<RecordAndGroupRelationRepo>().getAll() })
            .name("RecordAndGroupRelationRepo")
            .columnElemIndex()
            .column("fileName") { it.fileName }
            .column("groupName") { it.groupName }
            .print()
        PrintTable.of(sqlBlocking { get<GroupRepo>().getAll() })
            .name("GroupRepo")
            .columnElemIndex()
            .column("name") { it.name }
            .print()

        println("records = " + records.map { it.fileName })
        with(recordsListView) {
            adapter = AnyAdapter(records) { record, _, _, _ -> recordToView(record) }
            setOnItemClickListener { _, _, position, _ ->
                Toast.makeText(getAndroid(), "show Record - coming soon!", Toast.LENGTH_SHORT).show()
                openFileToUSer(records[position].fileName)
            }
        }
        Log.i(this.javaClass.simpleName, "# initActivityUI() - END")
    }

    private fun recordToView(record: RecordEntity): View =
        LayoutInflater.from(this)
            .inflate(R.layout.layout_record_list_elem, null)
            .also {
                println("record.fileExtension = ${record.fileExtension}")
                it.findViewById<TextView>(R.id.lyt_record_list_elem__tv__text).text = record.fileName
                it.findViewById<ImageView>(R.id.lyt_record_list_elem__ic__icon).setImageResource(
                    when (record.fileExtension) {
                        "pdf" -> R.drawable.ic_pdf_file
                        else -> R.drawable.ic_unknow_file
                    }
                )
            }

    private fun openFileToUSer(filename: String) {
        val (uri, mime) = recordFileService.getUriAndMimeForeRecordFile(filename)
        Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(uri, mime)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }.also(this::startActivity)
    }


    companion object {
        const val EXTRA__RECORD_GROUP_NAME = "recordGroup.name"
    }
}
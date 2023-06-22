package solutions.mk.mobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import solutions.mk.mobile.common.appStarter
import solutions.mk.mobile.common.get
import solutions.mk.mobile.common.switchActivityTo
import solutions.mk.mobile.components.AnyAdapter
import solutions.mk.mobile.persist.dao.GroupRepo
import solutions.mk.mobile.persist.sqlBlocking


/**
 * TODO
 *      3 - main activity - show category grid view
 *      4 - new activity - show record list view
 *      5 - show record file
 *      6 - ImportFromDeviceActivity - add scroll bar
 *      7 - ImportFromDeviceActivity - add select non-img file Mode/?Fragment
 *      8 - ImportFromDeviceActivity - add info "Selected file will be copied into applications directory."
 *      ? - make impl for translate values/string.xml for other language
 */
class MainActivity : AppCompatActivity() {

    private val importRecordsFab: FloatingActionButton by lazy { findViewById(R.id.main_importRecord_fab) }
    private val recordGroupGrid: GridView by lazy { findViewById(R.id.act_main__gv__record_groups) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appStarter.startElseIgnore(this)

        initActivityUI()
        Log.i(this.javaClass.simpleName, "# onCreate() - END")
    }

    private fun initActivityUI() {
        importRecordsFab.setOnClickListener { switchActivityTo<ImportFromDeviceActivity>() }


//        val addRecordGroupNames = listOf("111", "222", "333", "444", "555", "666")
        val allRecordGroupNames = sqlBlocking { get<GroupRepo>().getAllNames() }
        with(recordGroupGrid) {
            adapter = AnyAdapter(allRecordGroupNames) { groupName, _, _, _ -> recordGroupNameToView(groupName) }
            setOnItemClickListener { _, _, position, _ ->
                switchActivityTo<RecordListActivity> {
                    putExtra(RecordListActivity.EXTRA__RECORD_GROUP_NAME, allRecordGroupNames[position])
                }
                println("$position")
            }
        }

        Log.i(this.javaClass.simpleName, "# initActivityUI() - END")
    }

    private fun recordGroupNameToView(groupName: String): View =
        LayoutInflater.from(this)
            .inflate(R.layout.layout_record_group_grid_elem, null)
            .also { it.findViewById<TextView>(R.id.lyt_record_group_grid_elem__tv__text).text = groupName }
}

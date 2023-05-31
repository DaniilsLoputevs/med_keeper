package solutions.mk.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import solutions.mk.mobile.config.KoinConfig


/**
 * TODO
 *      3 - main activity - show category grid view
 *      4 - new activity - show record list view
 *      5 - show record file
 *      6 - ImportFromDeviceActivity - add scroll bar
 *      7 - ImportFromDeviceActivity - add select non-img file Mode/?Fragment
 *      8 - ImportFromDeviceActivity - add info "Selected file will be copied into applications directory."
 *      ? - fix - after resume app - it's crash on startKoin because it's started already. + create singleton instance of KoinConfig
 *      ? - make impl for translate values/string.xml for other language
 */
// todo - take out to other activity(select and add file)
class MainActivity : AppCompatActivity() {

    private val importRecordsFab: FloatingActionButton by lazy { findViewById(R.id.main_importRecord_fab) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // todo - fix - after resume app - it's crash on startKoin because it's started already. + create singleton instance of KoinConfig
        KoinConfig(this).startKoin()

        initActivityUI()
    }

    private fun initActivityUI() {
        importRecordsFab.setOnClickListener { startActivity(Intent(this, ImportFromDeviceActivity::class.java)) }
        Log.i(this.javaClass.simpleName, "# initActivityUI() - END")
    }

}
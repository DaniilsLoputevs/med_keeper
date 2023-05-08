package solutions.mk.mobile.common

import android.content.ContentResolver
import android.content.Context
import solutions.mk.mobile.MainActivity
import solutions.mk.mobile.config.ApplicationConfig
import solutions.mk.mobile.service.RecordFileService

// todo replace with DI - by coin or other
object Global {
    lateinit var applicationContext: Context
    lateinit var contentResolver: ContentResolver
    lateinit var recordFileService: RecordFileService
    val applicationConfig: ApplicationConfig = ApplicationConfig()

    /** invoke in MainActivity.onCreated() */
    fun init(mainActivity: MainActivity) {
        applicationContext = mainActivity.applicationContext
        contentResolver = mainActivity.contentResolver
        recordFileService = RecordFileService()
    }
}
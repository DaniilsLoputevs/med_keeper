package solutions.mk.mobile.service

import android.net.Uri
import solutions.mk.mobile.common.*
import solutions.mk.mobile.config.ApplicationConfig
import java.io.File
import java.io.FileOutputStream

class RecordFileService {
    private val appConfig: ApplicationConfig = Global.applicationConfig

    /**
     * default: File("${app}/files/records")
     */
    private val recordFilesDir: File by lazy {
        File("${Global.applicationContext.filesDir}${appConfig.relativePathToStoreRecordFiles}")
            .apply { if (!exists()) mkdirs() }
    }


    /**
     * Tested with [Uri] what started from "content".
     *
     * @return Saved File.
     */
    fun saveRecordFile(uri: Uri): File =
        File(recordFilesDir, takeFullFileName(uri))
            .apply { createNewFile() }
            .apply { println(absolutePath) }
            .also { recordFile -> copyContent(uri, recordFile) }

    fun deleteRecordFile(fileNameWithExtension: String) = File(recordFilesDir, fileNameWithExtension).deleteOnExit()


    /* PRIVATE PART */


    private fun copyContent(fromUri: Uri, toFile: File) =
        FileOutputStream(toFile).use { toOutput ->
            Global.contentResolver.openInputStream(fromUri)?.use { fromInput -> copy(fromInput, toOutput) }
        }
}




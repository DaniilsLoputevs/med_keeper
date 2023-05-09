package solutions.mk.mobile.service

import android.net.Uri
import solutions.mk.mobile.common.Global
import solutions.mk.mobile.common.copy
import solutions.mk.mobile.common.takeFullFileName
import solutions.mk.mobile.common.use
import solutions.mk.mobile.config.ApplicationConfig
import java.io.File
import java.io.FileOutputStream

class RecordFileService {
    // TODO : Inject!
    private val appConfig: ApplicationConfig by lazy { Global.applicationConfig }

    // TODO : Inject!
    private val pdfConverter: PDFConverter by lazy { Global.pdfConverter }

    // TODO : Inject!
    private val filesDir: File by lazy { Global.applicationContext.filesDir }

    /**
     * default: File("${app}/files/records")
     */
    private val recordFilesDir: File by lazy {
        File("${filesDir}${appConfig.relativePathToStoreRecordFiles}")
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

    /**
     * @param fileName without extension
     * @param imageUriList
     */
    fun saveRecordFile(fileName: String, imageUriList: List<Uri>): File =
        File(recordFilesDir, "${fileName}.pdf")
            .apply { createNewFile() }
            .also { pdfFile ->
                pdfConverter.allImagesConvertToPDF(imageUriList).use { input ->
                    FileOutputStream(pdfFile).use { output ->
                        input.writeTo(output)
                    }
                }
            }

    fun deleteRecordFile(fileNameWithExtension: String) = File(recordFilesDir, fileNameWithExtension).deleteOnExit()


    /* PRIVATE PART */


    private fun copyContent(fromUri: Uri, toFile: File) =
        FileOutputStream(toFile).use { toOutput ->
            Global.contentResolver.openInputStream(fromUri)?.use { fromInput -> copy(fromInput, toOutput) }
        }
}

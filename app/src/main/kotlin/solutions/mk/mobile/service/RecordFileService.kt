package solutions.mk.mobile.service

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import org.koin.core.annotation.Single
import solutions.mk.mobile.common.*
import solutions.mk.mobile.config.ApplicationConfig
import java.io.File
import java.io.FileOutputStream

@Single class RecordFileService {
    private val contentResolver: ContentResolver by injectAndroid()
    private val appContext: Context by injectAndroid()
    private val appConfig: ApplicationConfig by inject()
    private val pdfConverter: PDFConverter by inject()

    /**
     * default: File("${app}/files/records")
     */
    private val recordFilesDir: File by lazy {
        File("${appContext.filesDir}${appConfig.relativePathToStoreRecordFiles}")
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
     * TODO : Ideas: Look at this thing like a Factory
     *      - receive Uri and return file witch one of type [pdf, mp4 and etc]
     *      if you need fileExtension, take in from @return File.name.split(".").last()
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
            contentResolver.openInputStream(fromUri)?.use { fromInput -> copy(fromInput, toOutput) }
        }
}

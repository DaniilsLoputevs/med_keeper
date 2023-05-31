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
    @Deprecated("old API")
    fun saveRecordFile(uri: Uri): File =
        File(recordFilesDir, takeFullFileName(uri))
            .apply { createNewFile() }
            .apply { println(absolutePath) }
            .also { recordFile -> copyContent(uri, recordFile) }

    /**
     * @param fileName without file extension.
     * @param imageUriList content for PDF.
     * @return File("${app}/files/${appConfig.relativePathToStoreRecordFiles}/${filename}.pdf")
     */
    fun saveRecordFilePdfFromImages(fileName: String, imageUriList: List<Uri>): File =
        File(recordFilesDir, "${fileName}.pdf")
            .apply { createNewFile() }
            .also { pdfFile ->
                pdfConverter.allImagesConvertToPDF(imageUriList).use { pdfDocument ->
                    FileOutputStream(pdfFile).use { output ->
                        pdfDocument.writeTo(output)
                    }
                }
            }

    fun deleteRecordFile(fileNameWithExtension: String) = File(recordFilesDir, fileNameWithExtension).deleteOnExit()


    /* PRIVATE PART */


    private fun copyContent(fromUri: Uri, toFile: File) =
        FileOutputStream(toFile).use { toOutput ->
            contentResolver.openInputStream(fromUri)?.use { fromInput -> copy(fromInput, toOutput) }
        }

    fun isExistsFileWithName(fileNameWithExtension: String): Boolean =
        File(recordFilesDir, fileNameWithExtension).exists()
}

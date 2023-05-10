package solutions.mk.mobile.service

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import org.koin.core.annotation.Single
import solutions.mk.mobile.common.injectAndroid

@Single class PDFConverter {
    private val contentResolver: ContentResolver by injectAndroid()
    private val colourWhite = Paint().apply { color = Color.parseColor("#ffffff") }

    /**
     * IMPORTANT!!! [android.graphics.pdf.PdfDocument] is Closeable but not implement interface!!!
     * You must use it with Try-with-resources OR [AutoCloseable.use]
     */
    fun allImagesConvertToPDF(imageUriList: List<Uri>): PdfDocument =
        PdfDocument().also { document ->
            imageUriList.asSequence()
                .map(::uriToBitmap)
                .forEachIndexed { pageIndex, bitmap ->
                    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, pageIndex).create()
                    val currPage = document.startPage(pageInfo).apply {
                        canvas.apply {
                            drawPaint(colourWhite)
                            drawBitmap(
                                Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true),
                                0f, 0f, null
                            )
                        }
                    }
                    document.finishPage(currPage)
                }
        }


    private fun filePathToBitmap(filePath: String): Bitmap =
        BitmapFactory.decodeFile(filePath)
            ?: kotlin.run { throw RuntimeException("Fail to decode Bitmap from filepath:$filePath") }

    private fun uriToBitmap(imageUri: Uri): Bitmap =
        BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
            ?: kotlin.run { throw RuntimeException("Fail to open InputStream from Uri:$imageUri") }

}
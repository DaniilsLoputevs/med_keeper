//package solutions.mk.mobile.service
//
//import android.Manifest
//import android.R
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.Color
//import android.graphics.Paint
//import android.graphics.pdf.PdfDocument
//import android.graphics.pdf.PdfDocument.PageInfo
//import android.os.Bundle
//import android.provider.MediaStore
//import android.util.DisplayMetrics
//import android.view.View
//import android.view.WindowManager
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//
//
//class Aaaa {
//}
//
//import android.support.v4.app.ActivityCompat
//import android.support.v4.content.ContextCompat
//import android.support.v7.app.AppCompatActivity
//
//
//class MainActivity : AppCompatActivity(), View.OnClickListener {
//    var btn_select: Button? = null
//    var btn_convert: Button? = null
//    var iv_image: ImageView? = null
//    var boolean_permission = false
//    var boolean_save = false
//    var bitmap: Bitmap? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        init()
//        listener()
//        fn_permission()
//    }
//
//    private fun init() {
//        btn_select = findViewById<View>(R.id.btn_select) as Button
//        btn_convert = findViewById<View>(R.id.btn_convert) as Button
//        iv_image = findViewById<View>(R.id.iv_image) as ImageView
//    }
//
//    private fun listener() {
//        btn_select!!.setOnClickListener(this)
//        btn_convert!!.setOnClickListener(this)
//    }
//
//    override fun onClick(view: View) {
//        when (view.id) {
//            R.id.btn_select -> {
//                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                startActivityForResult(intent, GALLERY_PICTURE)
//            }
//
//            R.id.btn_convert -> if (boolean_save) {
//                val intent1 = Intent(applicationContext, PDFViewActivity::class.java)
//                startActivity(intent1)
//            } else {
//                createPdf()
//            }
//        }
//    }
//
//    private fun createPdf() {
//        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
//        val display = wm.defaultDisplay
//        val displaymetrics = DisplayMetrics()
//        this.windowManager.defaultDisplay.getMetrics(displaymetrics)
//        val hight = displaymetrics.heightPixels.toFloat()
//        val width = displaymetrics.widthPixels.toFloat()
//        val convertHighet = hight.toInt()
//        val convertWidth = width.toInt()
//
////        Resources mResources = getResources();
////        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);
//        val document = PdfDocument()
//        val pageInfo = PageInfo.Builder(bitmap!!.width, bitmap!!.height, 1).create()
//        val page = document.startPage(pageInfo)
//        val canvas = page.canvas
//        val paint = Paint()
//        paint.color = Color.parseColor("#ffffff")
//        canvas.drawPaint(paint)
//        bitmap = Bitmap.createScaledBitmap(bitmap!!, bitmap!!.width, bitmap!!.height, true)
//        paint.color = Color.BLUE
//        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
//        document.finishPage(page)
//
//
//        // write the document content
//        val targetPdf = "/sdcard/test.pdf"
//        val filePath = File(targetPdf)
//        try {
//            document.writeTo(FileOutputStream(filePath))
//            btn_convert!!.text = "Check PDF"
//            boolean_save = true
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Toast.makeText(this, "Something wrong: $e", Toast.LENGTH_LONG).show()
//        }
//
//        // close the document
//        document.close()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == GALLERY_PICTURE && resultCode == RESULT_OK) {
//            if (resultCode == RESULT_OK) {
//                val selectedImage = data!!.data
//                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//                val cursor = contentResolver.query(
//                    selectedImage!!, filePathColumn, null, null, null
//                )
//                cursor!!.moveToFirst()
//                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
//                val filePath = cursor.getString(columnIndex)
//                cursor.close()
//                bitmap = BitmapFactory.decodeFile(filePath)
//                iv_image!!.setImageBitmap(bitmap)
//                btn_convert!!.isClickable = true
//            }
//        }
//    }
//
//    private fun fn_permission() {
//        if (ContextCompat.checkSelfPermission(
//                applicationContext,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
//                applicationContext,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this@MainActivity,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//            ) {
//            } else {
//                ActivityCompat.requestPermissions(
//                    this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                    REQUEST_PERMISSIONS
//                )
//            }
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this@MainActivity,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
//            ) {
//            } else {
//                ActivityCompat.requestPermissions(
//                    this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                    REQUEST_PERMISSIONS
//                )
//            }
//        } else {
//            boolean_permission = true
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_PERMISSIONS) {
//            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                boolean_permission = true
//            } else {
//                Toast.makeText(applicationContext, "Please allow the permission", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//    companion object {
//        const val GALLERY_PICTURE = 1
//        const val REQUEST_PERMISSIONS = 1
//    }
//}
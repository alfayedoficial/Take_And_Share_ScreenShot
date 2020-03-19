package com.alialfayed.testsharescreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    private val PERMISSIONCODE: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnScreenShot.setOnClickListener {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                || checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || checkPermission(Manifest.permission.INTERNET)
            ) {
                //            val view: View = this.window.decorView
                val bm: Bitmap = screenShot(this.window.decorView)
                val file: File = saveBitmap(bm)
                Log.i("chase", "filepath: " + file.absolutePath)
                val uri: Uri = Uri.fromFile(File(file.absolutePath))
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my app.")
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                shareIntent.type = "image/*"
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(Intent.createChooser(shareIntent, "share via"))

            } else {
                takePermission()
            }

        }

    }

    private fun saveBitmap(bm: Bitmap): File {
//        val path: String =   Environment.getExternalStorageDirectory().absolutePath.toString() + "/Screenshots"
        val path: String = this.getExternalFilesDir(null)!!.absolutePath.toString() + "/Screenshots"

        val dir = File(path)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "mantis_image.png")
        try {
            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    private fun screenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    // Check checkPermission
    fun checkPermission(permission: String): Boolean {
        val check: Int = ContextCompat.checkSelfPermission(this, permission)
        return (check == PackageManager.PERMISSION_GRANTED)
    }


    // take Permission
    fun takePermission() {
        val arrayPermission = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(this, arrayPermission, PERMISSIONCODE)
    }


}

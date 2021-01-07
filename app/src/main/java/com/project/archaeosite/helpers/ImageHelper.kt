package com.project.archaeosite.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.project.archaeosite.R
import java.io.IOException

//this is to open other document to select picture
fun showImagePicker(parent: Activity, id: Int) {
    val intent = Intent()
    intent.type = "image/*" //let us pick any ind of image
    intent.action = Intent.ACTION_OPEN_DOCUMENT
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    val chooser = Intent.createChooser(intent, R.string.select_site_image.toString())
    parent.startActivityForResult(chooser, id)
}

//in order to display the image, we introduce another helper :
// for adding image
// this is to read image from other source file then display to the application
fun readImage(activity: Activity, resultCode: Int, data: Intent?): Bitmap? {
    var bitmap: Bitmap? = null
    if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, data.data)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return bitmap
}

fun readImageFromPath(context: Context, path : String) : Bitmap? {
    var bitmap : Bitmap? = null
    val uri = Uri.parse(path)
    if (uri != null) {
        try {
            val parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.getFileDescriptor()
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()
        } catch (e: Exception) {
        }
    }
    return bitmap
}

package com.project.archaeosite.helpers

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import org.jetbrains.anko.toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

val CAMERA_PERMISSION_REQUEST_CODE = 200
lateinit var currentPhotoPath: String

fun checkCameraPermissions(activity: Activity) : Boolean {
    return if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        true
    }
    else {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA),CAMERA_PERMISSION_REQUEST_CODE)
        false
    }
}

fun isCameraPermissionGranted(code: Int, grantResults: IntArray): Boolean {
    var permissionGranted = false
    if (code ==  CAMERA_PERMISSION_REQUEST_CODE) {
        when {
            grantResults.isEmpty() ->
                Log.i("Camera", "User interaction was cancelled.")
            (grantResults[0] == PackageManager.PERMISSION_GRANTED) ->
            {
                permissionGranted = true
                Log.i("Camera", "Permission Granted.")
            }
            else -> Log.i("Camera", "Permission Denied.")
        }
    }
    return permissionGranted
}

fun createImageFile(parent: Activity) : File {
    // genererate a unique filename with date.
    val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    // get access to the directory where we can write pictures.
    val storageDir: File? = parent.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("Site${timestamp}", ".jpg", storageDir).apply {
        currentPhotoPath = absolutePath
    }
}

fun showCamera(parent: Activity, id: Int) {
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{
        takePictureIntent -> takePictureIntent.resolveActivity(parent.packageManager)
        if (takePictureIntent == null) {
            parent.toast("Unable to save photo")
        }
        else {
            // if we are here, we have a valid intent.
            val photoFile: File = createImageFile(parent)
            photoFile.also {
                val photoURI = FileProvider.getUriForFile(parent.applicationContext, "com.project.archaeosite.provider", it)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                parent.startActivityForResult(takePictureIntent, id)
            }
        }
    }
}
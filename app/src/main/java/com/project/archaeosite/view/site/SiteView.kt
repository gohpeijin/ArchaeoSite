package com.project.archaeosite.view.site

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.project.archaeosite.R
import com.project.archaeosite.helpers.storeImagetoSD
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.models.Location
import com.project.archaeosite.view.base.BaseView
import com.project.archaeosite.view.base.REQUEST_IMAGE_CAPTURE
import kotlinx.android.synthetic.main.activity_site.*
import kotlinx.android.synthetic.main.dialog_hillfortsite.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class SiteView : BaseView(), AnkoLogger,DatePickerDialog.OnDateSetListener {

   lateinit var presenter: SitePresenter
    var site = ArchaeoModel()
    lateinit var map: GoogleMap


    protected val SAVE_IMAGE_REQUEST_CODE: Int = 1999
    protected val CAMERA_REQUEST_CODE: Int = 1998
    protected val CAMERA_PERMISSION_REQUEST_CODE = 1997

    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)

        presenter = initPresenter (SitePresenter(this)) as SitePresenter

        super.init(mytoolbar, true)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            map = it
            presenter.doConfigureMap(map)
            it.setOnMapClickListener { presenter.doSetLocation() }
        }

        //region add & edit site name & description
        item_save.setOnClickListener {
            if(text_Site_Name.text.toString().isEmpty()){
                toast ("Please Enter a site name")
            }
            else {
               presenter.doAddOrEdit(text_Site_Name.text.toString(),text_Site_Description.text.toString(),text_AdditionalNote.text.toString())
            }

        }
        //endregion

        button_take_pic.setOnClickListener {
            prepTakePhoto()
            presenter.doTakePhoto() }

        //region select imgae
        button_Select_Image.setOnClickListener{ presenter.doSelectImage() }
        //endregion

        //region previous & next image
        button_next_image.setOnClickListener{ presenter.doNextImage() }
        button_previos_image.setOnClickListener{ presenter.doPreviousImage() }
        //endregion

        //region navigation
        item_back.setOnClickListener{ presenter.doCancel()}
        item_delete.setOnClickListener { presenter.doDelete()}
        //endregion

        //region date picker
        editTextDate.setOnClickListener {
            val cal:Calendar = Calendar.getInstance()
            DatePickerDialog(this,this,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        //endregion

        //region visited checkbox
        checkBox_visited.setOnClickListener { presenter.doVisitedCheckbox(checkBox_visited.isChecked) }
        //endregion
    }

    fun prepTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        } else {
            val permissionRequest = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(permissionRequest,  CAMERA_PERMISSION_REQUEST_CODE)

        }
    }

    fun takePhoto() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
//            takePictureIntent -> takePictureIntent.resolveActivity(this.packageManager).also {
//            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE) }
//        }

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{
            takePictureIntent -> takePictureIntent.resolveActivity(this.packageManager)
            if (takePictureIntent == null) {
                Toast.makeText(this, "Unable to save photo", Toast.LENGTH_LONG).show()
            } else {
                // if we are here, we have a valid intent.
                val photoFile:File = createImageFile()

                photoFile.also {
                    val photoURI = FileProvider.getUriForFile(this.applicationContext, "com.project.archaeosite.provider", it)

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, SAVE_IMAGE_REQUEST_CODE)
                }
            }
        }


    }


    private fun createImageFile() : File {
        // genererate a unique filename with date.
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        // get access to the directory where we can write pictures.
        val storageDir: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("Site${timestamp}", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted, let's do stuff.
                    takePhoto()
                } else {
                    toast("Unable to take photo without permission")
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
    //region read image activity & map activity
    //thing need to be added "change image" button and "add image" button will be shown in edit mode - on hold
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
//            if (requestCode == CAMERA_REQUEST_CODE)  {
//                // now we can get the thumbnail
//                val imageBitmap = data!!.extras!!.get("data") as Bitmap
//                ImageSelected.setImageBitmap(imageBitmap)
//                storeImagetoSD(this,imageBitmap,"Site","SiteFromCamera")
//                 }

            if (requestCode ==SAVE_IMAGE_REQUEST_CODE){
                if (resultCode == RESULT_OK) {
                    val file = File(currentPhotoPath)
                    var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.fromFile(file))
                    if (bitmap != null) {
                        ImageSelected.setImageBitmap(bitmap)
                    }
                }
            }
            }
        if(data!=null)
            presenter.doActivityResult(requestCode, resultCode, data)
    }
    //endregion























    override fun displayImageByPosition(site: ArchaeoModel, num: Int){
        Glide.with(this).load(site.image[num]).into(ImageSelected)
    }

    @SuppressLint("SetTextI18n")
    override fun setSiteContent(site: ArchaeoModel, editmode: Boolean){
        if ( text_Site_Name.text.isEmpty())  text_Site_Name.setText(site.title)
        if (text_Site_Description.text.isEmpty()) text_Site_Description.setText(site.description)
        if(text_AdditionalNote.text.isEmpty()) text_AdditionalNote.setText(site.additionalNote)
        if(site.visited) checkBox_visited.isChecked=true
        if(site.date.day==0&&site.date.month==0&&site.date.year==0) editTextDate.text = getString(R.string.dateformat)
        else editTextDate.text = "${site.date.day}/${site.date.month}/${site.date.year}"
        if(site.image.isNotEmpty())
            Glide.with(this).load(site.image[0]).into(ImageSelected)
       // if(this::presenter.isInitialized)
            if(editmode){
                item_delete.visibility = View.VISIBLE
                item_save.text = getString(R.string.fromAddtoSaveText)
                item_back.visibility=View.INVISIBLE
            }
        this.showLocation(site.location)
    }

    @SuppressLint("SetTextI18n")
    override fun showLocation (location : Location) {
        lat.text = "%.6f".format(location.lat)
        lng.text = "%.6f".format(location.lng)
    }


//region map
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        presenter.doResartLocationUpdates()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
    //endregion

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        editTextDate.text = "$dayOfMonth/${month+1}/$year"
        presenter.doUpdateDate(year, month+1, dayOfMonth)
    }
}

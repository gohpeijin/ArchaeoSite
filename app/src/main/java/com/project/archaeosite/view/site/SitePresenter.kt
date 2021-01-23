package com.project.archaeosite.view.site

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.project.archaeosite.helpers.*
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.models.Location
import com.project.archaeosite.view.base.*
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.*
import java.io.File

class SitePresenter (view: SiteView): BasePresenter(view),AnkoLogger {

    var site = ArchaeoModel()
    var edit=false
    var defaultLocation = Location(52.245696, -7.139102, 15f)
    var imageposition=0
    var map: GoogleMap? = null
    var locationManualyChanged = false

    var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view)
    val locationRequest = createDefaultLocationRequest()

    init {
        if (view.intent.hasExtra("site_edit")) {
            edit = true
            site = view.intent.extras?.getParcelable<ArchaeoModel>("site_edit")!!
            view.setSiteContent(site, edit)
        } else {
            if (checkLocationPermissions(view)) {
                doSetCurrentLocation()
            }
        }
    }
    //region location
    //defines a callback - to be triggered when we turn location updates
    //checks to see if we are in edit mode - if not, it is assumed we would like live location updates to commence.
    @SuppressLint("MissingPermission")
    fun doResartLocationUpdates() {
        var locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null && locationResult.locations != null) {
                    val l = locationResult.locations.last()
                    if (!locationManualyChanged) {
                        locationUpdate(Location(l.latitude, l.longitude))
                    }
                }
            }
        }
        if (!edit) {
            locationService.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

        @SuppressLint("MissingPermission")
        fun doSetCurrentLocation() {
            locationService.lastLocation.addOnSuccessListener {
                locationUpdate(Location(it.latitude, it.longitude))
            }
        }

        override fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            if (isPermissionGranted(requestCode, grantResults)) {
                doSetCurrentLocation()
            } else {
                // permissions denied, so use the default location
                locationUpdate(defaultLocation)
            }

            if(isCameraPermissionGranted(requestCode,grantResults)){
                takephoto()
            }
            else {
                view!!.toast("Unable to take photo without permission")
            }
        }

        fun doConfigureMap(m: GoogleMap) {
            map = m
            locationUpdate(site.location)
        }

        fun locationUpdate(location:Location) {
            site.location =location
            site.location.zoom = 15f
            map?.clear()
            map?.uiSettings?.isZoomControlsEnabled = true
            val options = MarkerOptions().title(site.title).position(LatLng(site.location.lat, site.location.lng))
            map?.addMarker(options)
            map?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(site.location.lat, site.location.lng),
                    site.location.zoom
                )
            )
        }

    fun doSetLocation() {
        locationManualyChanged = true
        view?.navigateTo(
            VIEW.LOCATION,
            LOCATION_REQUEST,
            "location",
            Location(site.location.lat, site.location.lng, site.location.zoom)
        )
    }
    //endregion


    fun doTakePhoto() {
        if (checkCameraPermissions(view!!)) {
           takephoto()
        }
    }

    fun takephoto(){
        view?.let{
            showCamera(view!!,SAVE_IMAGE_REQUEST_CODE)
        }
        //view?.takePhoto()

    }

        fun doSelectImage() {
            view?.let {
                showImagePicker(view!!, IMAGE_REQUEST)
            }
        }

        fun doNextImage() {
            if (imageposition < site.image.size - 1) {
                imageposition++
                view?.displayImageByPosition(site, imageposition)
                //ImageSelected.setImageBitmap(readImageFromPath(this, site.image.get(imageposition)))
            } else {
                view?.toast("No more images")
            }
        }

        fun doPreviousImage() {
            if (imageposition > 0) {
                imageposition--
                view?.displayImageByPosition(site, imageposition)
            } else {
                view?.toast("Reach the first image")
            }
        }



        override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            when (requestCode) {
                //region image activity
                IMAGE_REQUEST -> {
                    if (resultCode == Activity.RESULT_OK &&data!=null) { //to prevent the app stopping when no image selected
                        if (data.clipData != null) {
                            val count = data.clipData!!.itemCount
                            if (count > 4) //only up to 4images can be selected
                                view?.toast("You can only select maximum 4 image. Please select again")
                            else {
                                site.image.clear()
                                for (i in 0 until count) {
                                    val imageUri = data.clipData!!.getItemAt(i).uri
                                    site.image.add(imageUri.toString())
                                }
                                imageposition = 0 //reset it to 0 if not when select multiple image and set the number to last image and select pic again will return index out of bound
                                view?.displayImageByPosition(site, imageposition)

                            }
                        } else {
                            site.image.clear()
                            site.image.add(data.data.toString())
                            imageposition = 0
                            view?.displayImageByPosition(site, imageposition)

                        }
                    }
                }
                //endregion
                LOCATION_REQUEST -> {
                    if (data != null) {
                        val location = data.extras?.getParcelable<Location>("location")!!
                        site.location = location
                        locationUpdate(location)

                        view?.setSiteContent(site,edit)
                    }
                }

                SAVE_IMAGE_REQUEST_CODE->{
                    if (resultCode == Activity.RESULT_OK) {
                    val file = File(currentPhotoPath)
                        val uri = FileProvider.getUriForFile(
                                view!!.applicationContext,
                                "com.project.archaeosite.provider",
                                file
                        )
                        site.image.clear()
                        site.image.add(uri.toString())
                        imageposition = 0
                        view?.displayImageByPosition(site, imageposition)
//                        imageposition = 0
//                        view?.displayImageByPosition(site, imageposition)
//                    var bitmap = MediaStore.Images.Media.getBitmap(view!!.contentResolver, Uri.fromFile(file))
//                    if (bitmap != null) {
//                        view!!.ImageSelected.setImageBitmap(bitmap)
//                    }
                    }
                }
            }
        }

    fun doAddOrEdit(title: String, description: String,additionalnote: String) {
        site.title = title
        site.description = description
        if (additionalnote.isBlank()) { site.additionalNote="" }
        else site.additionalNote=additionalnote
        doAsync {
            if (edit) {
                app.sites.update(site)
            } else {
                app.sites.create(site)
            }
            uiThread {
                view?.finish()
            }
        }
    }

    fun doCancel() {
        view?.finish()
    }

    fun doDelete() {
        doAsync {
            app.sites.delete(site)
            uiThread {
                view?.finish()
            }
        }
    }

    fun doUpdateDate(year: Int, month: Int, dayOfMonth: Int){
        site.date.day=dayOfMonth
        site.date.month=month
        site.date.year=year
    }

    fun doVisitedCheckbox(checked: Boolean){
        site.visited=checked
    }



}


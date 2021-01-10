package com.project.archaeosite.view.site

import android.app.Activity
import android.content.Intent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.project.archaeosite.view.location.EditLocationView
import com.project.archaeosite.helpers.readImageFromPath
import com.project.archaeosite.helpers.showImagePicker
import com.project.archaeosite.main.MainApp
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.models.Location
import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.IMAGE_REQUEST
import com.project.archaeosite.view.base.LOCATION_REQUEST
import com.project.archaeosite.view.base.VIEW
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast


class SitePresenter (view: SiteView): BasePresenter(view) {

    var site = ArchaeoModel()
    var edit=false
    var defaultLocation = Location(52.245696, -7.139102, 15f)
    var imageposition=0
    var map: GoogleMap? = null

    init {
        if (view.intent.hasExtra("site_edit")) {
            edit = true
            site = view.intent.extras?.getParcelable<ArchaeoModel>("site_edit")!!
            view.setSiteContent(site, edit)
        } else {
            site.lat = defaultLocation.lat
            site.lng = defaultLocation.lng

        }
    }

    fun doConfigureMap(m: GoogleMap) {
        map = m
        locationUpdate( site.lat,  site.lng)
    }
    fun locationUpdate(lat: Double, lng: Double) {
        site.lat = lat
        site.lng = lng
        site.zoom = 15f
        map?.clear()
        map?.uiSettings?.setZoomControlsEnabled(true)
        val options = MarkerOptions().title(site.title).position(LatLng(site.lat, site.lng))
        map?.addMarker(options)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(site.lat, site.lng), site.zoom))
        //view?.setSiteContent(site)
    }

    fun doAddOrEdit (title: String, description:String){
        site.title=title
        site.description=description
        if(edit){
            app.sites.update(site)
        }
        else{
            app.sites.create(site)
        }
        view?.finish()
    }


    fun doCancel() {
        view?.finish()
    }

    fun doDelete() {
        app.sites.delete(site)
        view?.finish()
    }

    fun doSelectImage() {
        view?.let {
            showImagePicker(view!!, IMAGE_REQUEST)
        }

    }
    fun doNextImage(){
        if(imageposition<site.image.size-1)
        {
            imageposition++
            view?.displayImageByPosition(site,imageposition)
            //ImageSelected.setImageBitmap(readImageFromPath(this, site.image.get(imageposition)))
        }
        else
        {
            view?.toast("No more images")
        }
    }
    fun doPreviousImage(){
        if(imageposition>0)
        {
            imageposition--
            view?.displayImageByPosition(site,imageposition)
        }
        else
        {
            view?.toast("Reach the first image")
        }
    }
    fun doSetLocation() {
        if (!edit) {
            view?.navigateTo(VIEW.LOCATION, LOCATION_REQUEST, "location", defaultLocation)
        } else {
            view?.navigateTo(VIEW.LOCATION,
                    LOCATION_REQUEST,
                    "location",
                    Location(site.lat, site.lng, site.zoom)
            )
        }
    }

   override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            //region image activity
            IMAGE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) { //to prevent the app stopping when no image selected
                    if (data!!.clipData != null) {
                        val count = data.clipData!!.itemCount
                        if (count > 4) //only up to 4images can be selected
                            view?.toast("You can only select maximum 4 image. Please select again")
                        else {
                            site.image.clear()
                            for (i in 0 until count) {
                                val imageUri = data.clipData!!.getItemAt(i).uri
                                site.image.add(imageUri.toString())
                            }
                            imageposition=0 //reset it to 0 if not when select multiple image and set the number to last image and select pic again will return index out of bound
                            view?.displayImageByPosition(site,imageposition)
                           // view.ImageSelected.setImageBitmap(readImageFromPath(view, site.image.get(0)))
                            //view.setListContent(site,edit)
                        }
                    } else {
                        site.image.clear()
                        site.image.add(data.data.toString())
                        imageposition=0
                        view?.displayImageByPosition(site,imageposition)
                        //view.ImageSelected.setImageBitmap(readImageFromPath(view, site.image.get(0)))
                       // view?.setSiteContent(site,edit)
                    }
                }
            }
            //endregion

            LOCATION_REQUEST -> {
                if (data != null) {
                    val location = data.extras?.getParcelable<Location>("location")!!
                    site.lat = location.lat
                    site.lng =location.lng
                    site.zoom = location.zoom
                    locationUpdate(site.lat, site.lng)
                }
            }
        }
    }



}
package com.project.archaeosite.view.site

import android.app.Activity
import android.content.Intent
import com.project.archaeosite.activities.MapActivity
import com.project.archaeosite.helpers.readImageFromPath
import com.project.archaeosite.helpers.showImagePicker
import com.project.archaeosite.main.MainApp
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.models.Location
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast


class SitePresenter (val view: SiteView){

    var site = ArchaeoModel()
    var app: MainApp
    var edit=false
    val IMAGE_REQUEST = 1
    val LOCATION_REQUEST = 2
    var location = Location(52.245696, -7.139102, 15f)
    var imageposition=0

    init{
        app = view.application as MainApp
        if (view.intent.hasExtra("site_edit")) {
            edit=true
            site = view.intent.extras?.getParcelable<ArchaeoModel>("site_edit")!!
            view.setListContent(site,edit)
        }
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
        view.finish()
    }


    fun doCancel() {
        view.finish()
    }

    fun doDelete() {
        app.sites.delete(site)
        view.finish()
    }

    fun doSelectImage() {
        showImagePicker(view, IMAGE_REQUEST)
    }
    fun doNextImage(){
        if(imageposition<site.image.size-1)
        {
            imageposition++
            view.displayImageByPosition(site,imageposition)
            //ImageSelected.setImageBitmap(readImageFromPath(this, site.image.get(imageposition)))
        }
        else
        {
            view.toast("No more images")
        }
    }
    fun doPreviousImage(){
        if(imageposition>0)
        {
            imageposition--
            view.displayImageByPosition(site,imageposition)
        }
        else
        {
            view.toast("Reach the first image")
        }
    }
    fun doSetLocation() {
        if (site.zoom != 0f) {
            location.lat = site.lat
            location.lng = site.lng
            location.zoom = site.zoom
        }
        view.startActivityForResult(view.intentFor<MapActivity>().putExtra("location", location), LOCATION_REQUEST)
    }

    fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            //region image activity
            IMAGE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) { //to prevent the app stopping when no image selected
                    if (data!!.clipData != null) {
                        val count = data.clipData!!.itemCount
                        if (count > 4) //only up to 4images can be selected
                            view.toast("You can only select maximum 4 image. Please select again")
                        else {
                            site.image.clear()
                            for (i in 0 until count) {
                                val imageUri = data.clipData!!.getItemAt(i).uri
                                site.image.add(imageUri.toString())
                            }
                            view.displayImageByPosition(site,imageposition)
                           // view.ImageSelected.setImageBitmap(readImageFromPath(view, site.image.get(0)))
                            //view.setListContent(site,edit)
                        }
                    } else {
                        site.image.clear()
                        site.image.add(data.data.toString())
                        view.ImageSelected.setImageBitmap(readImageFromPath(view, site.image.get(0)))
                       // view.setListContent(site,edit)
                    }
                }
            }
            //endregion

            LOCATION_REQUEST -> {
                if (data != null) {
                    location = data.extras?.getParcelable<Location>("location")!!
                    site.lat = location.lat
                    site.lng =location.lng
                    site.zoom = location.zoom
                }
            }
        }
    }



}
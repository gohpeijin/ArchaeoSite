package com.project.archaeosite.view.site

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.project.archaeosite.R
import com.project.archaeosite.helpers.readImageFromPath
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.view.base.BaseView
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast

class SiteView : BaseView(), AnkoLogger {

   lateinit var presenter: SitePresenter
    var site = ArchaeoModel()
    lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)

        presenter = initPresenter (SitePresenter(this)) as SitePresenter

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
               presenter.doAddOrEdit(text_Site_Name.text.toString(),text_Site_Description.text.toString())
            }

        }
        //endregion

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
    }

    override fun displayImageByPosition(site: ArchaeoModel,num: Int){
        ImageSelected.setImageBitmap(readImageFromPath(this, site.image.get(num)))
    }

    override fun setSiteContent(site: ArchaeoModel, editmode: Boolean){
        text_Site_Name.setText(site.title)
        text_Site_Description.setText(site.description)

        if(site.image.isNotEmpty())
            ImageSelected.setImageBitmap(readImageFromPath(this, site.image.get(0)))
       // if(this::presenter.isInitialized)
            if(editmode){
                item_delete.visibility = View.VISIBLE
                item_save.text = "SAVE"
            }

        lat.text = "%.6f".format(site.lat)
        lng.text = "%.6f".format(site.lng)
    }
    //region read image activity & map activity
    //thing need to be added "change image" button and "add image" button will be shown in edit mode - on hold

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!=null)
            presenter.doActivityResult(requestCode, resultCode, data)
    }
    //endregion

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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
    //endregion
}

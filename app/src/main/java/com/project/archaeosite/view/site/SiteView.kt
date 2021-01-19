package com.project.archaeosite.view.site

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.project.archaeosite.R
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.models.Location
import com.project.archaeosite.view.base.BaseView
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import java.util.*

class SiteView : BaseView(), AnkoLogger,DatePickerDialog.OnDateSetListener {

   lateinit var presenter: SitePresenter
    var site = ArchaeoModel()
    lateinit var map: GoogleMap

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

    override fun displayImageByPosition(site: ArchaeoModel,num: Int){
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

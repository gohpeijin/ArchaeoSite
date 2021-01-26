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

        button_take_pic.setOnClickListener { presenter.doTakePhoto() }

        //region select imgae
        button_Select_Image.setOnClickListener{ presenter.doSelectImage() }
        //endregion

        //region previous & next image
        button_next_image.setOnClickListener{ presenter.doNextImage() }
        button_previos_image.setOnClickListener{ presenter.doPreviousImage() }
        //endregion

        //region navigation
        item_cancel.setOnClickListener{ presenter.doCancel()}
        item_delete.setOnClickListener { presenter.doDelete()}
        //endregion

        //region date picker
        editTextDate.setOnClickListener {
            val cal:Calendar = Calendar.getInstance()
            DatePickerDialog(this,this,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        //endregion

        //region checkbox
        checkBox_visited.setOnClickListener { presenter.doVisitedCheckbox(checkBox_visited.isChecked) }
        checkBox_favourite.setOnClickListener { presenter.doFavouriteCheckbox(checkBox_favourite.isChecked) }
        //endregion

        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            presenter.doGetRating(rating)
        }
    }


    //region read image activity & map activity
    //thing need to be added "change image" button and "add image" button will be shown in edit mode - on hold
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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

        checkBox_visited.isChecked=site.visited
        checkBox_favourite.isChecked=site.favourite

        if (site.rating == null) ratingBar.rating = 0F
        else ratingBar.rating = site.rating!!

        if(site.date.day==0&&site.date.month==0&&site.date.year==0) editTextDate.text = getString(R.string.dateInitialize)
        else editTextDate.text = "${site.date.day}/${site.date.month}/${site.date.year}"

        if(site.image.isNotEmpty())
            Glide.with(this).load(site.image[0]).into(ImageSelected)
        if (site.image.size==1||site.image.isEmpty())
            hideVisibility()

       // if(this::presenter.isInitialized)
            if(editmode){
                item_delete.visibility = View.VISIBLE
                item_save.text = getString(R.string.fromAddtoSaveText)
                item_cancel.visibility=View.INVISIBLE
            }
        this.showLocation(site.location)
    }

    override fun showVisiblility() {
        button_previos_image.visibility=View.VISIBLE
        button_next_image.visibility=View.VISIBLE
    }

    override fun hideVisibility() {
        button_previos_image.visibility=View.INVISIBLE
        button_next_image.visibility=View.INVISIBLE
    }

    @SuppressLint("SetTextI18n")
    override fun showLocation (location : Location) {
        lat.text = "latitude: "+ "%.6f".format(location.lat)
        lng.text = "longitude: "+"%.6f".format(location.lng)
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

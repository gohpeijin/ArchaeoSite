package com.project.archaeosite.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.project.archaeosite.R
import com.project.archaeosite.helpers.readImage
import com.project.archaeosite.helpers.readImageFromPath
import com.project.archaeosite.helpers.showImagePicker
import com.project.archaeosite.main.MainApp
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.models.Location
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast

class SiteActivity : AppCompatActivity(), AnkoLogger {
    var site = ArchaeoModel()
    lateinit var app: MainApp
    val IMAGE_REQUEST = 1

    val LOCATION_REQUEST = 2
    var location = Location(52.245696, -7.139102, 15f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)

        app = application as MainApp
        var edit=false

        //region editmode
        if (intent.hasExtra("site_edit")) {
            edit=true
            site = intent.extras?.getParcelable<ArchaeoModel>("site_edit")!!
            text_Site_Name.setText(site.title)
            text_Site_Description.setText(site.description)
            button_Add_Site.setText("Save Site")
           // ImageSelected.setImageBitmap(readImageFromPath(this,site.image))
            if(site.image.isNotEmpty())
                ImageSelected.setImageBitmap(readImageFromPath(this, site.image.get(0)))
            item_delete.setVisibility(View.VISIBLE)
        }
        //endregion

        //region add & edit site name & description
        button_Add_Site.setOnClickListener(){
            site.title=text_Site_Name.text.toString()
            site.description=text_Site_Description.text.toString()
            if(site.title.isEmpty()){
                toast ("Please Enter a site name")
            }
            else {
                if(edit){
                    info("Edit Mode")
                    app.sites.update(site.copy())
                }
                else{
                    info("Create Mode")
                    app.sites.create(site.copy())
                }
                setResult(AppCompatActivity.RESULT_OK)
                finish()
            }
        }
        //endregion

        //region select imgae
        button_Select_Image.setOnClickListener{
            info("select image button pressed")
            showImagePicker(this,IMAGE_REQUEST)
        }
        //endregion

        //region previous & next image
        var imageposition=0
        button_next_image.setOnClickListener(){
            if(imageposition<site.image.size-1)
            {
                imageposition++
                ImageSelected.setImageBitmap(readImageFromPath(this, site.image.get(imageposition)))
            }
            else
            {
                toast("No more images")
            }
        }
        button_previos_image.setOnClickListener(){
            if(imageposition>0)
            {
                imageposition--
                ImageSelected.setImageBitmap(readImageFromPath(this, site.image.get(imageposition)))
            }
            else
            {
                toast("Reach the first image")
            }
        }
        //endregion

        //region map activity
        button_Select_Location.setOnClickListener {
            info("select location button pressed")
            //val location = Location(52.245696, -7.139102, 15f)
            if (site.zoom != 0f) {
                location.lat =  site.lat
                location.lng = site.lng
                location.zoom = site.zoom
            }
            startActivityForResult(intentFor<MapActivity>().putExtra("location", location), LOCATION_REQUEST)
        }
        //endregion

        //region navigation
        item_back.setOnClickListener(){
            info("back icon pressed")
            finish()
        }
        item_delete.setOnClickListener {
            app.sites.delete(site)
            finish()
        }
        //endregion
    }


    //region read image activity & map activity
    //thing need to be added "change image" button and "add image" button will be shown in edit mode - on hold

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            //region image activity
            IMAGE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) { //to prevent the app stopping when no image selected
                    if (data!!.clipData != null) {
                        val count = data.clipData!!.itemCount
                        if (count > 4) //only up to 4images can be selected
                            toast("You can only select maximum 4 image. Please select again")
                        else {
                            site.image.clear()
                            for (i in 0 until count) {
                                val imageUri = data.clipData!!.getItemAt(i).uri
                                site.image.add(imageUri.toString())
                            }
                            ImageSelected.setImageBitmap(readImageFromPath(this, site.image.get(0)))
                        }
                    } else {
                        site.image.clear()
                        site.image.add(data.getData().toString())
                        ImageSelected.setImageBitmap(readImage(this, resultCode, data))
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
    //endregion
}

package com.project.archaeosite.View.Site

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.project.archaeosite.R
import com.project.archaeosite.helpers.readImageFromPath
import com.project.archaeosite.models.ArchaeoModel
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast

class SiteView : AppCompatActivity(), AnkoLogger {

    lateinit var presenter: SitePresenter
    var site = ArchaeoModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)

        presenter =SitePresenter(this)

        //region add & edit site name & description
        button_Add_Site.setOnClickListener {
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

        //region map activity
        button_Select_Location.setOnClickListener { presenter.doSetLocation() }
        //endregion

        //region navigation
        item_back.setOnClickListener{ presenter.doCancel()}
        item_delete.setOnClickListener { presenter.doDelete()}
        //endregion
    }

    fun displayImageByPosition(site: ArchaeoModel,num: Int){
        ImageSelected.setImageBitmap(readImageFromPath(this, site.image.get(num)))
    }

    fun setListContent(site: ArchaeoModel,editemode: Boolean){
        text_Site_Name.setText(site.title)
        text_Site_Description.setText(site.description)

        if(site.image.isNotEmpty())
            ImageSelected.setImageBitmap(readImageFromPath(this, site.image.get(0)))
       // if(this::presenter.isInitialized)
            if(editemode){
                item_delete.visibility = View.VISIBLE
                button_Add_Site.text = "Save Site"
            }
    }
    //region read image activity & map activity
    //thing need to be added "change image" button and "add image" button will be shown in edit mode - on hold

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            presenter.doActivityResult(requestCode, resultCode, data)

    }
    //endregion
}

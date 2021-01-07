package com.project.archaeosite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.archaeosite.R
import com.project.archaeosite.helpers.readImage
import com.project.archaeosite.helpers.readImageFromPath
import com.project.archaeosite.helpers.showImagePicker
import com.project.archaeosite.main.MainApp
import com.project.archaeosite.models.ArchaeoModel
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class SiteActivity : AppCompatActivity(), AnkoLogger {
    var site = ArchaeoModel()
    lateinit var app: MainApp
    val IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)

        app = application as MainApp
        var edit=false

        if (intent.hasExtra("site_edit")) {
            edit=true
            site = intent.extras?.getParcelable<ArchaeoModel>("site_edit")!!
            text_Site_Name.setText(site.title)
            text_Site_Description.setText(site.description)
            button_Add_Site.setText("Save Site")
            ImageSelected.setImageBitmap(readImageFromPath(this,site.image))
        }

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

        button_Select_Image.setOnClickListener{
            info("select image button pressed")
            showImagePicker(this,IMAGE_REQUEST)
        }

        item_back.setOnClickListener(){
            info("back icon pressed")
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_REQUEST -> {
                if (data != null) {
                    info("setting up image")
                    site.image = data.getData().toString()
                 //  site.image ="content://com.android.externalstorage.documents/document/3232-6531%3ADalat%2F20191005_130946_018_saved.jpg"
                    ImageSelected.setImageBitmap(readImage(this, resultCode, data))
                }
            }
        }
    }
}

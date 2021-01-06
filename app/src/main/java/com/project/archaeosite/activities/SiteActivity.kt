package com.project.archaeosite.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.archaeosite.R
import com.project.archaeosite.main.MainApp
import com.project.archaeosite.models.ArchaeoModel
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class SiteActivity : AppCompatActivity(), AnkoLogger {
    var site = ArchaeoModel()
    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)

        app = application as MainApp

        button_Add_Site.setOnClickListener(){
            site.title=text_Site_Name.text.toString()
            site.description=text_Site_Description.text.toString()
            if(site.title.isEmpty()){
                toast ("Please Enter a site name")
            }
            else {
                app.sites.create(site.copy())
                setResult(AppCompatActivity.RESULT_OK)
                finish()
            }
        }

        item_back.setOnClickListener(){
            finish()
        }


    }
}
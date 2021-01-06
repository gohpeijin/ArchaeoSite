package com.project.archaeosite.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.archaeosite.R
import com.project.archaeosite.main.MainApp
import com.project.archaeosite.models.ArchaeoModel
import kotlinx.android.synthetic.main.activity_display_lists.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivityForResult

class DisplayListsActivity : AppCompatActivity(), SitesListener{
    lateinit var app: MainApp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_lists)
        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        recyclerview_sites.layoutManager = layoutManager
        recyclerview_sites.adapter = SitesAdapter(app.sites.findAll(),this)

        item_add.setOnClickListener(){
            startActivityForResult<SiteActivity>(0)
        }
    }
    override fun onSiteClick(site: ArchaeoModel) {
        startActivityForResult(intentFor<SiteActivity>(),0)
    }
}
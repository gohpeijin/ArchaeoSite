package com.project.archaeosite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.archaeosite.R
import com.project.archaeosite.main.MainApp
import com.project.archaeosite.models.ArchaeoModel
import kotlinx.android.synthetic.main.activity_display_lists.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivityForResult

class DisplayListsActivity : AppCompatActivity(),AnkoLogger,SitesListener{
    lateinit var app: MainApp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_lists)
        app = application as MainApp

        item_add.setOnClickListener(){
            startActivityForResult<SiteActivity>(0)
        }

        val layoutManager = LinearLayoutManager(this)
        recyclerview_sites.layoutManager = layoutManager
        recyclerview_sites.adapter = SitesAdapter(app.sites.findAll(),this)
    }

    override fun onSiteClick(site: ArchaeoModel) {
        startActivityForResult(intentFor<SiteActivity>().putExtra("site_edit",site),0)
        //passing the data of the selected site to another activity
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
     recyclerview_sites.adapter?.notifyDataSetChanged() //change to loadplacemark, lab A06
    super.onActivityResult(requestCode, resultCode, data)
    }
}




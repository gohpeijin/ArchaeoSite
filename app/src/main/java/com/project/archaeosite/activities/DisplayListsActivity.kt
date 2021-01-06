package com.project.archaeosite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.archaeosite.R
import com.project.archaeosite.main.MainApp
import com.project.archaeosite.models.ArchaeoModel
import kotlinx.android.synthetic.main.activity_display_lists.*
import kotlinx.android.synthetic.main.card_sites.view.*
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

     recyclerview_sites.adapter?.notifyDataSetChanged() //change to loadplacemark, lab A06
    super.onActivityResult(requestCode, resultCode, data)
    }
}




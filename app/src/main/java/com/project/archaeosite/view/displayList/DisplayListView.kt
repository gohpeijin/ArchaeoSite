package com.project.archaeosite.view.displayList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.archaeosite.R
import com.project.archaeosite.activities.SitesAdapter
import com.project.archaeosite.activities.SitesListener
import com.project.archaeosite.models.ArchaeoModel
import kotlinx.android.synthetic.main.activity_display_lists.*
import org.jetbrains.anko.*

class DisplayListView : AppCompatActivity(),AnkoLogger, SitesListener {
    lateinit var presenter: DisplayListPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_lists)

        presenter= DisplayListPresenter(this)

        item_add.setOnClickListener(){
            info("Add icon pressed")
           presenter.doAddSite()
        }
        item_map.setOnClickListener {
           presenter.doShowSitesMap()
        }

        val layoutManager = LinearLayoutManager(this)
        recyclerview_sites.layoutManager = layoutManager
        recyclerview_sites.adapter = SitesAdapter(presenter.loadSitesList(),this)
        recyclerview_sites.adapter?.notifyDataSetChanged()

    }



    override fun onSiteClick(site: ArchaeoModel) {
        presenter.doEditSite(site)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        recyclerview_sites.adapter?.notifyDataSetChanged()
        super.onActivityResult(requestCode, resultCode, data)
    }
}




package com.project.archaeosite.view.displayList

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.archaeosite.R
import com.project.archaeosite.activities.SitesAdapter
import com.project.archaeosite.activities.SitesListener
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.view.base.BaseView
import kotlinx.android.synthetic.main.activity_display_lists.*
import org.jetbrains.anko.*

class DisplayListView : BaseView(), AnkoLogger, SitesListener {

    lateinit var presenter: DisplayListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_lists)

        presenter = initPresenter (DisplayListPresenter (this)) as DisplayListPresenter

        item_add.setOnClickListener { presenter.doAddSite() }
        item_map.setOnClickListener { presenter.doShowSitesMap() }
        item_logout.setOnClickListener { presenter.doLogout() }

        val layoutManager = LinearLayoutManager(this)
        recyclerview_sites.layoutManager = layoutManager
        presenter.loadSitesList()
    }

    override fun showSites(sites: List<ArchaeoModel>) {
        recyclerview_sites.adapter = SitesAdapter(sites,this)
        recyclerview_sites.adapter?.notifyDataSetChanged()
    }


    override fun onSiteClick(site: ArchaeoModel) {
        presenter.doEditSite(site)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.loadSitesList()
        super.onActivityResult(requestCode, resultCode, data)
    }
}




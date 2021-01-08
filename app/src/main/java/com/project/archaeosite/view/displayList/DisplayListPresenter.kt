package com.project.archaeosite.view.displayList

import com.project.archaeosite.activities.SitesMapsActivity
import com.project.archaeosite.view.site.SiteView
import com.project.archaeosite.main.MainApp
import com.project.archaeosite.models.ArchaeoModel
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class DisplayListPresenter (val view:DisplayListView){
    var app: MainApp
    init {
        app = view.application as MainApp
    }

    fun loadSitesList()=app.sites.findAll()


    fun doAddSite(){
        view.startActivityForResult<SiteView>(0)
    }

    fun doEditSite(site:ArchaeoModel){
        view.startActivityForResult(view.intentFor<SiteView>().putExtra("site_edit",site),0)
        //passing the data of the selected site to another activity
    }
    fun doShowSitesMap(){
        view.startActivity<SitesMapsActivity>()
    }

}
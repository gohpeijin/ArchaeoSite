package com.project.archaeosite.view.displayList


import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView
import com.project.archaeosite.view.base.VIEW
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class DisplayListPresenter (view: BaseView): BasePresenter(view){


    fun loadSitesList(){
        doAsync {
            val sites = app.sites.findAll()
            uiThread {
                view?.showSites(sites)
            }
        }
       // app.sites.findAll()
    }

    fun doAddSite(){
       view?.navigateTo(VIEW.SITE)
       // view?.startActivityForResult<SiteView>(0)
    }


    fun doEditSite(site:ArchaeoModel){
       view?.navigateTo(VIEW.SITE,0,"site_edit",site)
       // view?.startActivityForResult(view?.intentFor<SiteView>()?.putExtra("site_edit",site),0)

    }
    fun doShowSitesMap(){
        view?.navigateTo(VIEW.MAPS)
    }

}
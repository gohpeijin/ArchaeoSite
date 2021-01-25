package com.project.archaeosite.view.displayList

import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView
import com.project.archaeosite.view.base.VIEW
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DisplayListPresenter (view: BaseView): BasePresenter(view),AnkoLogger{

    fun loadSitesList(){
        doAsync {
            val sites = app.sites.findAll()
            uiThread {
                view?.showSites(sites)
            }
        }
    }

    fun doAddSite(){
       view?.navigateTo(VIEW.SITE)
    }

    fun doEditSite(site:ArchaeoModel){
       view?.navigateTo(VIEW.SITE,0,"site_edit",site)
    }
    fun doShowSitesMap(){
        view?.navigateTo(VIEW.MAPS)
    }
    fun doLogout() {
        FirebaseAuth.getInstance().signOut()
        app.sites.clear()
        view?.navigateTo(VIEW.LOGIN)
    }
    fun doOpenProfile(){
        view?.navigateTo(VIEW.PROFILE)
    }
    fun doShowHillfort(){
        view?.navigateTo(VIEW.HILLFORT)
    }
}
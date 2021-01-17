package com.project.archaeosite.view.hillfort


import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.models.firebase.FirebaseRepo_Hillfort
import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView
import com.project.archaeosite.view.base.VIEW
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import org.jetbrains.anko.uiThread


class HillfortPresenter(view: BaseView): BasePresenter(view),AnkoLogger {


    fun loadHillfortList(){
        app.hillfortlist.loadHillfortData(object: FirebaseRepo_Hillfort.MyCallback{
            override fun onCallback(hillfortlist: List<HillfortModel>) {
                doAsync {
                    uiThread {
                        view?.showHillfortList(hillfortlist)
                        info("Hillfort List:"+hillfortlist)
                    }
                }
            }
        })

    }
    fun doAddSite(){
        view?.navigateTo(VIEW.SITE)
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
    fun doDisplayList(){
        view?.navigateTo(VIEW.LIST)
    }

}
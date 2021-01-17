package com.project.archaeosite.view.hillfort


import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView
import com.project.archaeosite.view.base.VIEW


class HillfortPresenter(view: BaseView): BasePresenter(view) {

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
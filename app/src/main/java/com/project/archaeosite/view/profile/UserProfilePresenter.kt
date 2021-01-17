package com.project.archaeosite.view.profile

import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.models.firebase.FirebaseRepo_Hillfort
import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView

class UserProfilePresenter (view: BaseView) : BasePresenter(view){

    fun doComputeIndiSite():Int{
        return app.sites.findAll().size
    }

    fun doComputeHillfortSite(){
        app.hillfortlist.loadHillfortData(object: FirebaseRepo_Hillfort.MyCallback{
            override fun onCallback(hillfortlist: List<HillfortModel>) {
               view?.showHillfortList(hillfortlist)
            }
        })
    }

}
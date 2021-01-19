package com.project.archaeosite.view.profile

import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.models.firebase.FirebaseRepo_Hillfort
import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView
import kotlinx.android.synthetic.main.activity_user_profile_view.*

class UserProfilePresenter (view: BaseView) : BasePresenter(view){

    val user = FirebaseAuth.getInstance().currentUser

    fun doComputeIndiSite():Int{
        return app.sites.findAll().size
    }

    fun doGetUserMail():String{
        return user?.email?.toString() ?: "Invalid User"
    }

    fun doComputeHillfortSite(){
        app.hillfortlist.loadHillfortData(object: FirebaseRepo_Hillfort.MyCallback{
            override fun onCallback(hillfortlist: List<HillfortModel>) {
               view?.showHillfortList(hillfortlist)
            }
        })
    }
    fun doComputeIndiVisitedSite():Int{
        var visited:Int=0
        app.sites.findAll()
        for (site in app.sites.findAll()){
            if(site.visited)
                visited++
        }
        return visited
    }

}
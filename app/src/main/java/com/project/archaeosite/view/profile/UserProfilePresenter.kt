package com.project.archaeosite.view.profile

import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.models.firebase.FirebaseRepo_Hillfort
import com.project.archaeosite.models.firebase.SiteFireStore
import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView

class UserProfilePresenter (view: BaseView) : BasePresenter(view){

    val user = FirebaseAuth.getInstance().currentUser

    var fireStore = app.sites as SiteFireStore

    fun doGetUserMail():String{
        return user?.email?.toString() ?: "Invalid User"
    }

    fun doGetUserPassword(): String {
        return fireStore.returnCurrentUser().password
    }

    fun doComputeIndiSite():Int{
        return app.sites.findAll().size
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

    fun doLoadHillfortSite(){
        app.hillfortlist.loadHillfortData(object: FirebaseRepo_Hillfort.MyCallback{
            override fun onCallback(hillfortlist: List<HillfortModel>) {
               view?.showHillfortList(hillfortlist)
            }
        })
    }

    fun doComputeHillfortSite(hillfortList: List<HillfortModel>):Int{
        return hillfortList.size
    }

    fun doComputevisitedHillfort(hillfortList: List<HillfortModel>):Int{
        var hillfortvisited:Int=0
        for (site in hillfortList){
            for (userReactions in site.userReaction)
                if(userReactions.userID== user!!.uid &&userReactions.visited)
                    hillfortvisited++
        }
        return hillfortvisited
    }
}
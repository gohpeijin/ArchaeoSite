package com.project.archaeosite.view.hillfort


import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.models.UserReaction
import com.project.archaeosite.models.firebase.FirebaseRepo_Hillfort
import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView
import com.project.archaeosite.view.base.VIEW
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import org.jetbrains.anko.uiThread


class HillfortPresenter(view: BaseView): BasePresenter(view),AnkoLogger {

    val user = FirebaseAuth.getInstance().currentUser

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

    fun doCheckUserVisited(hillfort: HillfortModel):Boolean{
        var reacted:Boolean=false
        for (userReactions in  hillfort.userReaction) {
            if(userReactions.userID== user!!.uid ){
                reacted=userReactions.visited
            }
        }
        return reacted
    }
    fun doVisitedCheckbox(checked: Boolean,hillfort: HillfortModel) {
        val indiReaction = UserReaction()
        var reacted:Boolean=false
        for (userReactions in  hillfort.userReaction) {
            if(userReactions.userID== user!!.uid ){
                userReactions.visited= checked
                reacted=true
            }
        }
        if (!reacted){
            indiReaction.userID=user!!.uid
            indiReaction.visited=checked
            hillfort.userReaction.add(indiReaction)
        }

        app.hillfortlist.updateHillfort(hillfort)
    }
}
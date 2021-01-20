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
        var visited:Boolean=false
        var reacted=false
        for (userReactions in  hillfort.userReaction) {
            if(userReactions.userID== user!!.uid ){
                reacted=true
                visited=userReactions.visited
            }
            if(reacted)
                break
        }
        return visited
    }

    fun doCheckUserFavourite(hillfort: HillfortModel):Boolean{
        var favourite:Boolean=false
        var reacted=false
        for (userReactions in  hillfort.userReaction) {
            if(userReactions.userID== user!!.uid ){
                reacted=true
                favourite=userReactions.favourite
            }
            if(reacted)
                break
        }
        return favourite
    }

    fun doVisitedCheckbox(checked: Boolean,hillfort: HillfortModel) {
        val indiReaction = UserReaction()
        var reacted:Boolean=false //check user first react or has reacted before
        for (userReactions in  hillfort.userReaction) {
            if(userReactions.userID== user!!.uid ){ //user got made reaction before
                userReactions.visited= checked  //change the state
                indiReaction.favourite=userReactions.visited
                reacted=true
            }
            if(reacted)
                break
        }
        if (!reacted){ //if user never react to that post before, create the user obj in that post
            indiReaction.userID=user!!.uid
            indiReaction.visited=checked
            hillfort.userReaction.add(indiReaction)
        }

        app.hillfortlist.updateHillfort(hillfort)
    }

    fun doFavourite(checked: Boolean,hillfort: HillfortModel){
        val indiReaction = UserReaction()
        var reacted:Boolean=false
        for (userReactions in  hillfort.userReaction) {
            if(userReactions.userID== user!!.uid ){
                userReactions.favourite= checked
                reacted=true
            }
            if(reacted)
                break
        }
        if (!reacted){
            indiReaction.userID=user!!.uid
            indiReaction.favourite=checked
            hillfort.userReaction.add(indiReaction)
        }

        app.hillfortlist.updateHillfort(hillfort)
    }
}
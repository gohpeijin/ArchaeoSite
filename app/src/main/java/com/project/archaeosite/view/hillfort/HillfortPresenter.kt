package com.project.archaeosite.view.hillfort


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
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



    //region listView
    fun loadHillfortList(){
        app.hillfortlist.loadHillfortData(object: FirebaseRepo_Hillfort.MyCallback{
            override fun onCallback(hillfortlist: List<HillfortModel>) {
                doAsync {
                    uiThread {
                        view?.showHillfortList(hillfortlist)
                    }
                }
            }
        })
    }
    //endregion

    //region drawer
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
    //endregion

    //region Dialog View
    val user = FirebaseAuth.getInstance().currentUser
    lateinit var indiUserReaction :UserReaction
    var reacted=false
    var index: Int? =null


    //for display use only and will no updated unless the dialog is called agn
    fun getIndiReactionModel(hillfort: HillfortModel):UserReaction {
        indiUserReaction = UserReaction(userID = user!!.uid)
        reacted=false
        info(indiUserReaction)
        for ((i,userReactions) in hillfort.userReaction.withIndex()) {
            if (userReactions.userID == user!!.uid) {
                reacted = true
                index=i
                indiUserReaction = userReactions
            }
            if (reacted)
                break
        }
        return indiUserReaction
    }

    fun doCalculateAvg(hillfort: HillfortModel):Float {
        var avgRate:Float=0.0F
        var count:Int=0
        for (userReactions in hillfort.userReaction) {
            if(userReactions.rating!=null){
                avgRate+=userReactions.rating!!
                count++
            }
        }
        return avgRate/count
    }

//    fun doCheckUserVisited(hillfort: HillfortModel):Boolean{
//        var visited:Boolean=false
//        var reacted=false
//        for (userReactions in  hillfort.userReaction) {
//            if(userReactions.userID== user!!.uid ){
//                reacted=true
//                visited=userReactions.visited
//            }
//            if(reacted)
//                break
//        }
//        return visited
//    }
//
//    fun doCheckUserFavourite(hillfort: HillfortModel):Boolean{
//        var favourite:Boolean=false
//        var reacted=false
//        for (userReactions in  hillfort.userReaction) {
//            if(userReactions.userID== user!!.uid ){
//                reacted=true
//                favourite=userReactions.favourite
//            }
//            if(reacted)
//                break
//        }
//        return favourite
//    }
//
//    fun doCheckUserRating(hillfort: HillfortModel): Float {
//        var rating: Float? =0.0F
//        var reacted=false
//        for (userReactions in  hillfort.userReaction) {
//            if(userReactions.userID== user!!.uid ){
//                reacted=true
//                rating= userReactions.rating
//            }
//            if(reacted)
//                break
//        }
//        return rating ?: 0.0F
//    }

    fun doVisitedCheckbox(checked: Boolean,hillfort: HillfortModel) {
        //region old
//        val indiReaction = UserReaction()
//        var reacted:Boolean=false //check user first react or has reacted before
//        for (userReactions in  hillfort.userReaction) {
//            if(userReactions.userID== user!!.uid ){ //user got made reaction before
//                userReactions.visited= checked  //change the state
//                indiReaction.favourite=userReactions.visited
//                reacted=true
//            }
//            if(reacted)
//                break
//        }
//        if (!reacted){ //if user never react to that post before, create the user obj in that post
//            indiReaction.userID=user!!.uid
//            indiReaction.visited=checked
//            hillfort.userReaction.add(indiReaction)
//        }
//endregion
        if(reacted)
            hillfort.userReaction[index!!].visited=checked
        else {
            reacted=true
            indiUserReaction.visited=checked
            hillfort.userReaction.add(indiUserReaction)
            index =hillfort.userReaction.indexOf(indiUserReaction)
        }
        app.hillfortlist.updateHillfort(hillfort)
    }

    fun doFavourite(checked: Boolean,hillfort: HillfortModel){
        //region old
//        val indiReaction = UserReaction()
//        var reacted:Boolean=false
//        for (userReactions in  hillfort.userReaction) {
//            if(userReactions.userID== user!!.uid ){
//                userReactions.favourite= checked
//                reacted=true
//            }
//            if(reacted)
//                break
//        }
//        if (!reacted){
//            indiReaction.userID=user!!.uid
//            indiReaction.favourite=checked
//            hillfort.userReaction.add(indiReaction)
//        }
        //endregion

        if(reacted)
            hillfort.userReaction[index!!].favourite=checked
        else {
            reacted=true
            indiUserReaction.favourite=checked
            hillfort.userReaction.add(indiUserReaction)
            index =hillfort.userReaction.indexOf(indiUserReaction)
        }
        app.hillfortlist.updateHillfort(hillfort)
    }

    fun doGetRating(rating: Float, hillfort: HillfortModel) {
        //region old
//        val indiReaction = UserReaction()
//        var reacted:Boolean=false
//        for (userReactions in  hillfort.userReaction) {
//            if(userReactions.userID== user!!.uid ){
//                if(rating==0F) userReactions.rating= null
//                else userReactions.rating= rating
//                reacted=true
//            }
//            if(reacted)
//                break
//        }
//        if (!reacted){
//            indiReaction.userID=user!!.uid
//            if(rating==0F)  indiReaction.rating= null
//            else indiReaction.rating=rating
//            hillfort.userReaction.add(indiReaction)
//        }
        //endregion
        if(reacted)
            if(rating==0F) hillfort.userReaction[index!!].rating=null
                else hillfort.userReaction[index!!].rating=rating
        else {
            reacted=true
            if(rating==0F)  indiUserReaction.rating= null
            else indiUserReaction.rating=rating
            hillfort.userReaction.add(indiUserReaction)
            index =hillfort.userReaction.indexOf(indiUserReaction)
        }
        app.hillfortlist.updateHillfort(hillfort)
    }

    //endregion
}
package com.project.archaeosite.view.hillfort


import android.R
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.models.UserReaction
import com.project.archaeosite.models.firebase.FirebaseRepo_Hillfort
import com.project.archaeosite.view.base.*
import org.jetbrains.anko.*


class HillfortPresenter(view: BaseView): BasePresenter(view),AnkoLogger {

    //region listView
    fun loadHillfortList(int: Int){
        app.hillfortlist.loadHillfortData(object : FirebaseRepo_Hillfort.MyCallback {
            override fun onCallback(hillfortlist: List<HillfortModel>) {
                doAsync {
                    uiThread {
                        when (int) {
                            HILLFORT_LIST -> view?.showHillfortList(hillfortlist)
                            HILLFORT_FAV_LIST -> {
                                var hillfortFavList = ArrayList<HillfortModel>()
                                for (hillfort in hillfortlist) {
                                    for (findfav in hillfort.userReaction) {
                                        if (findfav.userID == user!!.uid) {
                                            if (findfav.favourite) {
                                                hillfortFavList.add(hillfort)
                                            }
                                        }
                                    }
                                }
                                view?.showHillfortList(hillfortFavList)
                            }
                        }
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
        for ((i, userReactions) in hillfort.userReaction.withIndex()) {
            if (userReactions.userID == user.uid) {
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


    fun doVisitedCheckbox(checked: Boolean, hillfort: HillfortModel) {
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

    fun doFavourite(checked: Boolean, hillfort: HillfortModel){
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

    fun doShareSite(hillfort: HillfortModel) {
//        val intent  = Intent()
//        intent.action=Intent.ACTION_SEND
//        intent.putExtra(Intent.EXTRA_TEXT,"testing")
//        intent.type="text/plain"
//
//        view?.startActivity(Intent.createChooser(intent,"Please select app to share:"))


//        val bm: Bitmap = screenShot(view?.contentView)!!
//        val file: File = saveBitmap(bm, "mantis_image.png")!!
//        Log.i("chase", "filepath: " + file.absolutePath)
//        val uri: Uri = Uri.fromFile(File(file.absolutePath))
//        val shareIntent = Intent()
//        shareIntent.action = Intent.ACTION_SEND
//        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my app.")
//        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
//        shareIntent.type = "image/*"
//        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        view?.startActivity(Intent.createChooser(shareIntent, "share via"))


    }
//
//    private fun screenShot(view: View?): Bitmap? {
//        val bitmap = Bitmap.createBitmap(view!!.width, view.height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        view.draw(canvas)
//        return bitmap
//    }
//
//    private fun saveBitmap(bm: Bitmap, fileName: String): File? {
//        val path: String = Environment.getExternalStorageDirectory().absolutePath.toString() + "/Screenshots"
//        val dir = File(path)
//        if (!dir.exists()) dir.mkdirs()
//        val file = File(dir, fileName)
//        try {
//            val fOut = FileOutputStream(file)
//            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut)
//            fOut.flush()
//            fOut.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return file
//    }

    //endregion
}
package com.project.archaeosite.view.displayList

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.view.base.*
import org.jetbrains.anko.*
import java.io.File
import java.io.FileOutputStream

class DisplayListPresenter (view: BaseView): BasePresenter(view),AnkoLogger{

    var imageposition=0

    fun loadSitesList(int: Int){
        doAsync {
            val sites = app.sites.findAll()
            uiThread {
                when(int) {
                    DISPLAY_ALL_LIST -> view?.showSites(sites)
                    DISPLAY_FAV_LIST -> {
                        var siteFavList = ArrayList<ArchaeoModel>()
                        for (site in sites) {
                          if(site.favourite)
                              siteFavList.add(site)
                        }
                        view?.showSites(siteFavList)
                    }
                }
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

    fun doShareSite(site: ArchaeoModel, siteDialog: AlertDialog) {
        val b: Bitmap = screenshot(siteDialog)!!
        shareImage(store(b, "Site.png")!!,site)
    }

    fun screenshot(dialog: Dialog): Bitmap? {
        val window: Window = dialog.window!!
        val decorView: View = window.decorView
        val bitmap = Bitmap.createBitmap(decorView.width, decorView.height, Bitmap.Config.ARGB_8888)
        decorView.draw(Canvas(bitmap))
        return bitmap
    }

    fun store(bm: Bitmap, fileName: String?) : File?{
        val dirPath = view?.getExternalFilesDir(null)!!.absolutePath.toString() + "/Screenshots"
        val dir = File(dirPath)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dirPath, fileName)
        try {
            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return file
    }

    fun shareImage(file: File, site: ArchaeoModel) {
        val uri = FileProvider.getUriForFile(
                view!!.applicationContext,
                "com.project.archaeosite.provider",
                file
        )
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_SUBJECT, site.title)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            view!!.startActivity(Intent.createChooser(intent, "Share Screenshot"))
        } catch (e: ActivityNotFoundException) {
            view?.toast("No App Available")
        }
    }


    fun doNextImage(site: ArchaeoModel) :Int{
        if (imageposition < site.image.size - 1) {
            imageposition++
        } else {
            view?.toast("No more images")
        }
        return imageposition
    }

    fun doPreviousImage(site: ArchaeoModel) :Int{
        if (imageposition > 0) {
            imageposition--
        } else {
            view?.toast("Reach the first image")
        }
        return imageposition
    }
}
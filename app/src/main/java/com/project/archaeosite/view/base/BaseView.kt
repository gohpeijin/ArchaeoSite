package com.project.archaeosite.view.base

import android.content.Intent
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.location.LocationSettingsRequest
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.models.Location
import com.project.archaeosite.view.displayList.DisplayListView
import com.project.archaeosite.view.hillfort.HillfortView
import com.project.archaeosite.view.location.EditLocationView
import com.project.archaeosite.view.login.LoginView
import com.project.archaeosite.view.map.SiteMapView
import com.project.archaeosite.view.navigator.NavigatorView
import com.project.archaeosite.view.profile.UserProfileView
import com.project.archaeosite.view.site.SiteView
import org.jetbrains.anko.AnkoLogger

val IMAGE_REQUEST = 1
val LOCATION_REQUEST = 2
val SAVE_IMAGE_REQUEST = 3
val UPDATE_LOCATION_REQUEST = 4
val DISPLAY_ALL_LIST=1
val DISPLAY_FAV_LIST =2

enum class VIEW {
    LOCATION, SITE, MAPS, LIST, LOGIN, PROFILE, HILLFORT, NAVIGATOR
}

open abstract class BaseView() : AppCompatActivity(), AnkoLogger {

    var basePresenter: BasePresenter? = null

    fun navigateTo(view: VIEW, code: Int = 0, key: String = "", value: Parcelable? = null) {
        var intent = Intent(this, DisplayListView::class.java)
        when (view) {
            VIEW.LOCATION -> intent = Intent(this, EditLocationView::class.java)
            VIEW.SITE -> intent = Intent(this,SiteView::class.java)
            VIEW.MAPS -> intent = Intent(this, SiteMapView::class.java)
            VIEW.LIST -> intent = Intent(this, DisplayListView::class.java)
            VIEW.LOGIN -> intent = Intent(this, LoginView::class.java)
            VIEW.PROFILE -> intent = Intent(this, UserProfileView::class.java)
            VIEW.HILLFORT -> intent = Intent(this, HillfortView::class.java)
            VIEW.NAVIGATOR->intent = Intent(this, NavigatorView::class.java)
        }
        if (key != "") {
            intent.putExtra(key, value)
        }

       startActivityForResult(intent, code)
    }

    fun initPresenter(presenter: BasePresenter): BasePresenter {
        basePresenter = presenter
        return presenter
    }

    fun init(toolbar: Toolbar, upEnabled: Boolean) {
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(upEnabled)
    }


    override fun onDestroy() {
        basePresenter?.onDestroy()
        super.onDestroy()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            basePresenter?.doActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        basePresenter?.doRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //Site
    open fun setSiteContent(site: ArchaeoModel, editmode: Boolean=false) {}
    open fun setSiteContent(site: Any) {}
    open fun displayImageByPosition(site: ArchaeoModel,num: Int){}

    //Displaylist
    open fun showSites(sites: List<ArchaeoModel>) {}
    open fun showLocation(location : Location) {}
    open fun showHillfortList(hillfortList:List<HillfortModel>){}

    open fun showProgress() {}
    open fun hideProgress() {}

    open fun checkLocationSetting(builder: LocationSettingsRequest.Builder) {}

    open fun showVisiblility(){}
    open fun hideVisibility(){}

}
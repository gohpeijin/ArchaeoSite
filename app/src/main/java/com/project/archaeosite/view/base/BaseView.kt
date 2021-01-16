package com.project.archaeosite.view.base

import android.content.Intent
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.models.Location
import com.project.archaeosite.view.displayList.DisplayListView
import com.project.archaeosite.view.location.EditLocationView
import com.project.archaeosite.view.login.LoginView
import com.project.archaeosite.view.map.SiteMapView
import com.project.archaeosite.view.site.SiteView
import kotlinx.android.synthetic.main.nav_header_main.*
import org.jetbrains.anko.AnkoLogger

val IMAGE_REQUEST = 1
val LOCATION_REQUEST = 2

enum class VIEW {
    LOCATION, SITE, MAPS, LIST, LOGIN
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
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user != null) {
//            useremail.text =user.email
//           // toolbar.title = "${title}: ${user.email}"
//        }
    }


    override fun onDestroy() {
        basePresenter?.onDestroy()
        super.onDestroy()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            basePresenter?.doActivityResult(requestCode, resultCode, data!!)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        basePresenter?.doRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //Site
    //show placemark
    open fun setSiteContent(site: ArchaeoModel, editmode: Boolean=false) {}
    open fun displayImageByPosition(site: ArchaeoModel,num: Int){}

    //Displaylist
    //showplacemarks
    open fun showSites(sites: List<ArchaeoModel>) {}
    open fun showLocation(location : Location) {}
   // open fun showLocation(latitude : Double, longitude : Double) {}
    open fun showProgress() {}
    open fun hideProgress() {}
}
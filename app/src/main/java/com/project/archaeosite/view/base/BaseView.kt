package com.project.archaeosite.view.base

import android.content.Intent
import android.os.Parcelable
import android.view.Display
import androidx.appcompat.app.AppCompatActivity
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.view.displayList.DisplayListView
import com.project.archaeosite.view.location.EditLocationView
import com.project.archaeosite.view.map.SiteMapView
import org.jetbrains.anko.AnkoLogger

val IMAGE_REQUEST = 1
val LOCATION_REQUEST = 2

enum class VIEW {
    LOCATION, SITE, MAPS, LIST
}


open abstract class BaseView() : AppCompatActivity(), AnkoLogger {

    var basePresenter: BasePresenter? = null

    fun navigateTo(view: VIEW, code: Int = 0, key: String = "", value: Parcelable? = null) {
        var intent = Intent(this, DisplayListView::class.java)
        when (view) {
            VIEW.LOCATION -> intent = Intent(this, EditLocationView::class.java)
            VIEW.SITE -> intent = Intent(this, ArchaeoModel::class.java)
            VIEW.MAPS -> intent = Intent(this, SiteMapView::class.java)
            VIEW.LIST -> intent = Intent(this, DisplayListView::class.java)
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


    override fun onDestroy() {
        basePresenter?.onDestroy()
        super.onDestroy()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            basePresenter?.doActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        basePresenter?.doRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    open fun setSiteContent(site: ArchaeoModel, editmode: Boolean) {}
    open fun displayImageByPosition(site: ArchaeoModel,num: Int){}
    open fun showSites(placemarks: List<ArchaeoModel>) {}
    open fun showProgress() {}
    open fun hideProgress() {}
}
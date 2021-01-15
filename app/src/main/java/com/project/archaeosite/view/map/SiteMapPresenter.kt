package com.project.archaeosite.view.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.project.archaeosite.main.MainApp
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SiteMapPresenter (view: BaseView) : BasePresenter(view)  {

    fun doReadSiteLocationToMap(map: GoogleMap,sites :List<ArchaeoModel>) {
        map.uiSettings.setZoomControlsEnabled(true) //enable the zoom the plus and minus button

        sites.forEach {
            val loc = LatLng(it.location.lat, it.location.lng)
            val options = MarkerOptions().title(it.title).position(loc)
            map.addMarker(options).tag = it //tag as id
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.location.zoom))//enable app zoom in to the last added list
        }
    }

    fun doMarkerSelected (marker: Marker){
        val site = marker.tag as ArchaeoModel
        //val tag = marker.tag as Long //read tag id
        doAsync {
           // val site = app.sites.findById(tag)
            uiThread {
                if (site!=null){
                    view?.setSiteContent(site)
                }
            }
        }
    }

    fun loadSitesList() {
        doAsync {
            val sites =app.sites.findAll()
            uiThread {
                view?.showSites(sites)
            }
        }
    }
}
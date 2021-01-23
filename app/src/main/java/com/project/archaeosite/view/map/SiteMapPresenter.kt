package com.project.archaeosite.view.map

import android.os.Parcelable
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.project.archaeosite.models.ArchaeoModel
import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.models.firebase.FirebaseRepo_Hillfort
import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView
import com.project.archaeosite.view.base.VIEW
import kotlinx.android.parcel.RawValue
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SiteMapPresenter (view: BaseView) : BasePresenter(view)  {

    lateinit var site : Parcelable

    fun doReadSiteLocationToMap(map: GoogleMap,sites :List<ArchaeoModel>) {
        map.uiSettings.isZoomControlsEnabled = true //enable the zoom the plus and minus button
        sites.forEach {
            val loc = LatLng(it.location.lat, it.location.lng)
            val options = MarkerOptions().title(it.title).position(loc).snippet("GPS : $loc")

            map.addMarker(options).tag = it //tag as id
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.location.zoom))//enable app zoom in to the last added list
        }
    }

    fun doReadHillfortLocationToMap(map: GoogleMap, hillfortList: List<HillfortModel>) {

        hillfortList.forEach{
            val loc = LatLng(it.Location!!.latitude, it.Location!!.longitude)
            val options = MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(it.Title)
                    .snippet("GPS : $loc")
                    .position(loc)
            map.addMarker(options).tag = it
        }
    }

    fun doMarkerSelected (marker: Marker){
            site = (marker.tag as Parcelable?)!!
//        val site = marker.tag
        marker.showInfoWindow()
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

    fun doLoadHillfortSite(){
        app.hillfortlist.loadHillfortData(object: FirebaseRepo_Hillfort.MyCallback{
            override fun onCallback(hillfortlist: List<HillfortModel>) {
                view?.showHillfortList(hillfortlist)
            }
        })
    }

    fun doNavigator(){
        view?.navigateTo(VIEW.SITE,0,"site_navigate",site)
    }

}
package com.project.archaeosite.view.navigator

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.project.archaeosite.models.ForNavigate
import com.project.archaeosite.models.Location
import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView

class NavigatorPresenter(view: BaseView) : BasePresenter(view) {

    var siteNavi = ForNavigate()
    var location = Location()

    var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view)
//    val locationRequest = createLocationRequest()
    var mlocationCallback: LocationCallback? = null

    init {
        siteNavi = view.intent.extras?.getParcelable<ForNavigate>("site_navigate")!!

    }

    fun initialiseMap(map: GoogleMap) {
        val loc = LatLng(siteNavi.lat, siteNavi.lng)
        val options = MarkerOptions()
            .title(siteNavi.title)
            .snippet("GPS : $loc")
            .position(loc)
        map.addMarker(options)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, location.zoom))
    }


}
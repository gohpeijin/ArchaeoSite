package com.project.archaeosite.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.project.archaeosite.R
import com.project.archaeosite.helpers.readImageFromPath
import com.project.archaeosite.main.MainApp
import kotlinx.android.synthetic.main.activity_sites_maps.*

class SitesMapsActivity : AppCompatActivity(),GoogleMap.OnMarkerClickListener {

    lateinit var app: MainApp
    lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sites_maps)

        mapView.onCreate(savedInstanceState)
        app = application as MainApp

        mapView.getMapAsync {
            map = it
            configureMap()
        }

        item_back_sitemaps.setOnClickListener() {
            finish()
        }
    }

    fun configureMap() {
        map.uiSettings.setZoomControlsEnabled(true) //enable the zoom the plus and minus button
        map.setOnMarkerClickListener(this)

        app.sites.findAll().forEach {
            val loc = LatLng(it.lat, it.lng)
            val options = MarkerOptions().title(it.title).position(loc)
            map.addMarker(options).tag = it.id  //tag as id
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.zoom))//enable app zoom in to the last added list
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val tag = marker.tag as Long //read tag id
        val site = app.sites.findById(tag)
        currentTitle.text = site!!.title
        currentDescription.text = site!!.description
        if(site.image.isNotEmpty())
             currentImage.setImageBitmap(readImageFromPath(this, site.image.get(0)))
        else
            currentImage.setImageResource(android.R.color.transparent)
        return true
    }
}


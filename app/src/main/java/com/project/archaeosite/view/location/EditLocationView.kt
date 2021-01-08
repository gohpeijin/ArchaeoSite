package com.project.archaeosite.view.location

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.project.archaeosite.R
import com.project.archaeosite.models.Location

class EditLocationView : AppCompatActivity(),  GoogleMap.OnMarkerDragListener,GoogleMap.OnMarkerClickListener  {


    lateinit var map: GoogleMap
    lateinit var presenter: EditLocationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        presenter = EditLocationPresenter(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync{
            map = it
            map.setOnMarkerDragListener(this)
            map.setOnMarkerClickListener(this)
            presenter.initialiseMap(map)
        }
    }

    override fun onMarkerDragStart(marker: Marker) {}

    override fun onMarkerDrag(marker: Marker) {}

    override fun onMarkerDragEnd(marker: Marker) {
        presenter.doUpdateLocation(marker.position.latitude,marker.position.longitude,map.cameraPosition.zoom)
    }

    override fun onBackPressed() {
        presenter.doOnBackPressed()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
       presenter.doUpdateMarker(marker)
        return false
    }

}
package com.project.archaeosite.view.location

import android.os.Bundle

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.project.archaeosite.R
import com.project.archaeosite.models.Location
import com.project.archaeosite.view.base.BaseView
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.activity_map.item_save
import kotlinx.android.synthetic.main.activity_map.lat
import kotlinx.android.synthetic.main.activity_map.lng
import kotlinx.android.synthetic.main.activity_map.mapView
import kotlinx.android.synthetic.main.activity_map.mytoolbar
import kotlinx.android.synthetic.main.activity_site.*

class EditLocationView : BaseView(),  GoogleMap.OnMarkerDragListener,GoogleMap.OnMarkerClickListener  {

    lateinit var presenter: EditLocationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        presenter = EditLocationPresenter(this)

        super.init(mytoolbar, true)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync{
            it.setOnMarkerDragListener(this)
            it.setOnMarkerClickListener(this)
            presenter.initialiseMap(it)
        }

        item_save.setOnClickListener{
            presenter.doSave()
        }
    }


    override fun onMarkerDragStart(marker: Marker) {}

    override fun onMarkerDrag(marker: Marker) {
        lat.text = "%.6f".format(marker.position.latitude)
        lng.text = "%.6f".format(marker.position.longitude)
    }

    override fun showLocation(location : Location) {
        lat.text = "%.6f".format(location.lat)
        lng.text = "%.6f".format(location.lng)
    }

    override fun onMarkerDragEnd(marker: Marker) {
        presenter.doUpdateLocation(marker.position.latitude,marker.position.longitude)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
       presenter.doUpdateMarker(marker)
        return false
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

}
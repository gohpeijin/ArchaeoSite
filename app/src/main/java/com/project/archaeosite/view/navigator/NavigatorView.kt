package com.project.archaeosite.view.navigator

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.project.archaeosite.R
import com.project.archaeosite.view.base.BaseView
import com.project.archaeosite.view.base.UPDATE_LOCATION_REQUEST
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.activity_sites_maps.*
import kotlinx.android.synthetic.main.activity_sites_maps.mapView
import kotlinx.android.synthetic.main.activity_sites_maps.mytoolbar
import org.jetbrains.anko.toast


class NavigatorView : BaseView() ,OnMapReadyCallback {

    lateinit var presenter: NavigatorPresenter
    lateinit var map : GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigator_view)

        presenter = initPresenter(NavigatorPresenter(this)) as NavigatorPresenter

        super.init(mytoolbar, true)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap?) {
        this.map=map!!
        presenter.initialiseMap(map)
        checkLocationSetting(presenter.builder)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        presenter.doRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.doActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun checkLocationSetting(builder: LocationSettingsRequest.Builder) {
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this, OnSuccessListener {
            presenter.startLocationUpdates()
            return@OnSuccessListener
        })
        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
                builder1.setTitle("Continious Location Request for navigation")
                builder1.setMessage("This request is essential to get location update continiously")
                builder1.create()
                builder1.setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, which ->
                        try {
                            e.startResolutionForResult(this, UPDATE_LOCATION_REQUEST)
                        } catch (e1: SendIntentException) {
                            e1.printStackTrace()
                        }
                    })
                builder1.setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, which -> toast("Location update permission not granted") })
                builder1.show()
            }
        }
    }
    override fun onDestroy() {
        presenter.stopLocationUpdates()
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
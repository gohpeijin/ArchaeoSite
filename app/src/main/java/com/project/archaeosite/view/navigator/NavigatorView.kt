package com.project.archaeosite.view.navigator

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson
import com.project.archaeosite.R
import com.project.archaeosite.helpers.GoogleMapDTO
import com.project.archaeosite.view.base.BaseView
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.activity_sites_maps.*
import kotlinx.android.synthetic.main.activity_sites_maps.mapView
import kotlinx.android.synthetic.main.activity_sites_maps.mytoolbar
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.toast


class NavigatorView : BaseView() ,OnMapReadyCallback {

    lateinit var presenter: NavigatorPresenter
    lateinit var map : GoogleMap
    var fusedLocationClient: FusedLocationProviderClient? = null
    var mLocationRequest: LocationRequest? = null
//    private var fusedLocationClient=LocationServices.getFusedLocationProviderClient(this)
//    private var mLocationRequest = createDefaultLocationRequest()
    var mlocationCallback: LocationCallback? = null
    var builder: LocationSettingsRequest.Builder? = null
    val REQUEST_CHECK_SETTINGS = 102
    val CURRENT_LOCATION_REQUEST=123

//    var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//    val locationRequest = createDefaultLocationRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigator_view)

        presenter = initPresenter(NavigatorPresenter(this)) as NavigatorPresenter

        super.init(mytoolbar, true)
        mapView.onCreate(savedInstanceState)
//        mapView.getMapAsync{
//            map=it
//            presenter.initialiseMap(it)
//        }

        mapView.getMapAsync(this)
    }
    fun createLocationRequest(): LocationRequest? {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 2000
        mLocationRequest.fastestInterval = 2000
        mLocationRequest.smallestDisplacement = 10f
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return mLocationRequest
    }

    override fun onMapReady(p0: GoogleMap?) {
        map=p0!!
        init()
    }


    @SuppressLint("MissingPermission")
    private fun init() {
        fetchLastLocation()
        mLocationRequest=createLocationRequest()
        presenter.initialiseMap(map)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        var initialLocMarker=true

        mlocationCallback= object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
               super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    val loc = LatLng(location.latitude, location.longitude)
                    if(initialLocMarker){
                        val options = MarkerOptions()
                            .snippet("GPS : $loc")
                            .title("Initiallocation")
                            .position(loc)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        map.addMarker(options)
                        initialLocMarker=false
                    }

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 18f))

                    val cameraPosition = CameraPosition.Builder()
                        .target(loc)
                        .bearing(location.bearing)
                        .zoom(18f)
                        .build()

                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                   // map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 18f))
                    Log.e("CONTINIOUSLOC: ", location.toString())

                    val locdestination=LatLng(presenter.siteNavi.lat,presenter.siteNavi.lng)
                    val url=getDirectionURL(loc,locdestination)
                    Log.e("CONTINIOUSLOC: ", url)
                    GetDirection(url).execute()
                }

            }
        }
        builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest!!)
        checkLocationSetting(builder!!)
    }

    fun fetchLastLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                showPermissionAlert()
                return
            }
        }
        map.isMyLocationEnabled=true
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isMyLocationButtonEnabled=true
        map.setOnMyLocationClickListener {
            fusedLocationClient!!.lastLocation.addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    val loc=LatLng(location.latitude, location.longitude)

                    val options = MarkerOptions()
                        .snippet("GPS : $loc")
                        .title("Lastlocation")
                        .position(loc)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    map.addMarker(options)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 18f))

                    Log.e("LAST LOCATION: ", location.toString()) // You will get your last location here
                }
            }
        }
    }

    fun showPermissionAlert() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                CURRENT_LOCATION_REQUEST
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CURRENT_LOCATION_REQUEST -> {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showPermissionAlert()
                } else {
                    if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fetchLastLocation()
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                startLocationUpdates()
            } else {
                checkLocationSetting(builder!!)
            }
        }
    }

    fun checkLocationSetting(builder: LocationSettingsRequest.Builder) {
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this, OnSuccessListener {
            startLocationUpdates()
            return@OnSuccessListener
        })
        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
                builder1.setTitle("Continious Location Request")
                builder1.setMessage("This request is essential to get location update continiously")
                builder1.create()
                builder1.setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, which ->
                        try {
                            e.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
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

    fun startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }
        fusedLocationClient!!.requestLocationUpdates(
            mLocationRequest,
            mlocationCallback,
            Looper.myLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient!!.removeLocationUpdates(mlocationCallback)
    }

    fun getDirectionURL(origin: LatLng, dest: LatLng):String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}&key=${R.string.google_maps_key}"
//        return "https://maps.googleapis.com/maps/api/directions/json?origin=" +
//                "${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=false&mode=driving"
    }

    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()
            Log.d("GoogleMap", " data : $data")
            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)

                val path = ArrayList<LatLng>()

                for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices) {
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            map.addPolyline(lineoption)
        }
    }

      fun decodePolyline(encoded: String): List<LatLng> {

            val poly = ArrayList<LatLng>()
            var index = 0
            val len = encoded.length
            var lat = 0
            var lng = 0

            while (index < len) {
                var b: Int
                var shift = 0
                var result = 0
                do {
                    b = encoded[index++].toInt() - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lat += dlat

                shift = 0
                result = 0
                do {
                    b = encoded[index++].toInt() - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lng += dlng

                val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
                poly.add(latLng)
            }

            return poly
        }


    override fun onDestroy() {
        stopLocationUpdates()
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
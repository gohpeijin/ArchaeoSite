package com.project.archaeosite.view.navigator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.project.archaeosite.R
import com.project.archaeosite.helpers.GoogleMapDTO
import com.project.archaeosite.helpers.createLocationRequest
import com.project.archaeosite.models.ForNavigate
import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView
import com.project.archaeosite.view.base.UPDATE_LOCATION_REQUEST
import okhttp3.OkHttpClient
import okhttp3.Request

class NavigatorPresenter(view: BaseView) : BasePresenter(view) {

    var siteNavi = ForNavigate()

    var fusedLocationClient: FusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(view)
    var mLocationRequest: LocationRequest = createLocationRequest()
    var  GOOGLE_API_KEY = view.resources.getString(R.string.google_maps_key)
    var map: GoogleMap? = null
    val CURRENT_LOCATION_PERMISSION_REQUEST_CODE=123
    var builder: LocationSettingsRequest.Builder =LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest!!)


    var mlocationCallback: LocationCallback? = null

    init {
        siteNavi = view.intent.extras?.getParcelable<ForNavigate>("site_navigate")!!

    }
    fun initialiseMap(map: GoogleMap) {
        this.map=map
        val loc = LatLng(siteNavi.lat, siteNavi.lng)
        val options = MarkerOptions()
                .title(siteNavi.title)
                .snippet("GPS : $loc")
                .position(loc)
        map.addMarker(options)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 17f))
        fetchLastLocation()
        locationcallback()
    }

    fun fetchLastLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    view?.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                showPermissionAlert()
                return
            }
        }
        map!!.isMyLocationEnabled=true
        map!!.uiSettings.isCompassEnabled = true
        map!!.uiSettings.isMyLocationButtonEnabled=true

       fusedLocationClient!!.lastLocation.addOnSuccessListener {
           location ->
            if (location != null) {
                // Zoom to the user last location
                val loc=LatLng(location.latitude, location.longitude)
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 18f))
            }
        }
    }

    fun locationcallback (){
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
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                        map?.addMarker(options)
                        initialLocMarker=false
                    }

                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 18f))

                    val cameraPosition = CameraPosition.Builder()
                            .target(loc)
                            .bearing(location.bearing)
                            .zoom(18f)
                            .build()

                    map?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                    // map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 18f))
                    Log.e("CONTINIOUSLOC: ", location.toString())

                    val locdestination=LatLng(siteNavi.lat, siteNavi.lng)

                    GetDirection(getDirectionURL(loc, locdestination)).execute()
                }

            }
        }
    }

    override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UPDATE_LOCATION_REQUEST) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                startLocationUpdates()
            } else {
                view?.checkLocationSetting(builder)
            }
        }
    }

    override fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CURRENT_LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showPermissionAlert()
                } else {
                    if (ActivityCompat.checkSelfPermission(view!!.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(view!!.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fetchLastLocation()
                    }
                }
            }
        }
    }

    fun showPermissionAlert() {
        if (ActivityCompat.checkSelfPermission(view!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(view!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(view!!, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                    CURRENT_LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient!!.removeLocationUpdates(mlocationCallback)
    }

    fun startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    view?.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }
        fusedLocationClient!!.requestLocationUpdates(mLocationRequest, mlocationCallback, Looper.myLooper())
    }

    fun getDirectionURL(origin: LatLng, dest: LatLng):String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}&key=${GOOGLE_API_KEY}"
    }

    private inner class GetDirection(val url: String) : AsyncTask<Void, Void, List<List<LatLng>>>() {
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
            map?.addPolyline(lineoption)
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

            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }
}
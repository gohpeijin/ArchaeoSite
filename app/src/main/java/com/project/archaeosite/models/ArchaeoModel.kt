package com.peoject.archaeo10.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


//we need a unique way of identifying placemarks - this is usually via an id.
@Parcelize
data class ArchaeoModel(
    var id: Long = 0,
    var title: String = "",
    var description: String="",
    var image: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var zoom: Float = 0f
) : Parcelable

//We would like to include the location into our model, so we can record the latitude/longitude the user selects
//We are still keeping Location model for use with the MapsActivity
@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable
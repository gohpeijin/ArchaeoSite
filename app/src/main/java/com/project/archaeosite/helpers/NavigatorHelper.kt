package com.project.archaeosite.helpers

import com.google.android.gms.location.LocationRequest


fun createLocationRequest(): LocationRequest {
    val mLocationRequest = LocationRequest.create()
    mLocationRequest.interval = 2000
    mLocationRequest.fastestInterval = 2000
    mLocationRequest.smallestDisplacement = 10f
    mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    return mLocationRequest
}
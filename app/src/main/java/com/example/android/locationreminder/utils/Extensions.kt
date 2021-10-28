package com.example.android.locationreminder.utils

fun createTitle(lat: Double, lon: Double) : String{
    val titleLat = "%.2f".format(lat)
    val titleLng = "%.2f".format(lon)
    return "Coordinates: $titleLat, $titleLng"
}
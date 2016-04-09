package com.mccauley.driverlog.database

import android.location.Location

class LocationLite(var latitude: Double = 0.0, var longitude: Double = 0.0, var time: Long = 0) {

  def this(location: Location) {
    this()
    latitude = location.getLatitude
    longitude = location.getLongitude
    time = location.getTime
  }

  def toLocation(): Location = {
    val location = new Location("")
    location.setLatitude(latitude)
    location.setLongitude(longitude)
    location.setTime(time)
    location
  }
}

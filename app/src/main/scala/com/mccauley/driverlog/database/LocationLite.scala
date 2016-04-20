package com.mccauley.driverlog.database

import android.location.Location

class LocationLite(var latitude: Double = 0.0, var longitude: Double = 0.0, var time: Long = 0, var description: String = null) {

  def this(location: Location, description: String) {
    this()
    this.latitude = location.getLatitude
    this.longitude = location.getLongitude
    this.time = location.getTime
    this.description = description
  }

  def toLocation(): Location = {
    val location = new Location("")
    location.setLatitude(latitude)
    location.setLongitude(longitude)
    location.setTime(time)
    location
  }
}

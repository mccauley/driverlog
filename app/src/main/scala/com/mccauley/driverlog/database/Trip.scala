package com.mccauley.driverlog.database

import android.location.Location
import org.joda.time.Duration

class Trip(_startLocation: Location, _endLocation: Location, _distance: Double) {
  def distance() = _distance / 1609.344

  def duration() = new Duration(_endLocation.getTime - _startLocation.getTime)

  def startLocation = _startLocation

  def endLocation = _endLocation
}

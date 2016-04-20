package com.mccauley.driverlog.database

import java.util

import _root_.android.content.Context
import _root_.android.location.{Address, Geocoder, Location}
import com.couchbase.lite._
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.joda.time.DateTime

object TripHelper {
  private val ADDRESS_FORMAT = "%1$s, %2$s"
  val START_LOCATION_FIELD: String = "startLocation"
  val END_LOCATION_FIELD: String = "endLocation"
  val DISTANCE_FIELD: String = "distance"
  val DOC_TYPE = "trip"
  val VIEW_NAME = "trips"

  private val gson = new Gson()

  def query(database: Database): LiveQuery = {
    val view: View = database.getView(VIEW_NAME)
    if (view.getMap == null) {
      val mapper: Mapper = new Mapper() {
        def map(document: java.util.Map[String, AnyRef], emitter: Emitter) {
          val docType: String = document.get("type").asInstanceOf[String]
          if (DOC_TYPE == docType) {
            emitter.emit(document.get("created_at"), document)
          }
        }
      }
      view.setMap(mapper, "1")
    }

    view.createQuery.toLiveQuery
  }

  def saveTrip(database: Database, startLocation: Location, endLocation: Location, distance: Double, geocoder: Geocoder): Document = {
    val document: Document = database.createDocument
    val revision: UnsavedRevision = document.createRevision
    val properties = new java.util.HashMap[String, AnyRef]
    val startLocationLite = new LocationLite(startLocation, getLocationDescription(startLocation, geocoder))
    val endLocationLite = new LocationLite(endLocation, getLocationDescription(endLocation, geocoder))
    properties.put("type", TripHelper.DOC_TYPE)
    properties.put("log_id", document.getId)
    properties.put("created_at", String.valueOf(DateTime.now().getMillis))
    properties.put(START_LOCATION_FIELD, gson.toJson(startLocationLite))
    properties.put(END_LOCATION_FIELD, gson.toJson(endLocationLite))
    properties.put(DISTANCE_FIELD, double2Double(distance))
    revision.setUserProperties(properties)
    revision.save()
    document
  }

  def createTrip(document: Document): Trip = {
    if (document == null || document.getCurrentRevision == null) {
      return null
    }
    val properties: util.Map[String, AnyRef] = document.getCurrentRevision.getProperties
    val locationType = new TypeToken[LocationLite] {}.getType
    val startLocation: LocationLite = gson.fromJson(properties.get(START_LOCATION_FIELD).asInstanceOf[String], locationType).asInstanceOf[LocationLite]
    val endLocation: LocationLite = gson.fromJson(properties.get(END_LOCATION_FIELD).asInstanceOf[String], locationType).asInstanceOf[LocationLite]
    val distance: Double = properties.get(DISTANCE_FIELD).asInstanceOf[Double]
    new Trip(startLocation.toLocation(), startLocation.description, endLocation.toLocation(), endLocation.description, distance)
  }

  private def getLocationDescription(location: Location, geocoder: Geocoder): String = {
    if (geocoder != null) {
      val addresses: java.util.List[Address] = geocoder.getFromLocation(location.getLatitude, location.getLongitude, 1)
      if (addresses != null && !addresses.isEmpty) {
        val address = addresses.get(0)
        if (address.getAddressLine(0) != null) {
          return String.format(ADDRESS_FORMAT, address.getAddressLine(0), address.getAddressLine(1))
        }
      }
    }
    String.format(ADDRESS_FORMAT, String.valueOf(location.getLatitude), String.valueOf(location.getLongitude))
  }
}

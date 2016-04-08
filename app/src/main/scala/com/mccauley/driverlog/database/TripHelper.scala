package com.mccauley.driverlog.database

import java.util

import _root_.android.location.Location
import com.couchbase.lite._
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.joda.time.DateTime

object TripHelper {
  val LOCATION_FIELD: String = "location"
  val DOC_TYPE = "trip"
  val VIEW_NAME = "trips"

  private val gson = new Gson()

  def query(database: Database) = {
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

    view.createQuery
  }

  def saveTrip(database: Database, startLocation: Location, endLocation: Location): Document = {
    val log = new Trip(startLocation, endLocation)
    val document: Document = database.createDocument
    val revision: UnsavedRevision = document.createRevision
    val properties = new java.util.HashMap[String, AnyRef]
    properties.put("type", TripHelper.DOC_TYPE)
    properties.put("log_id", document.getId)
    properties.put("created_at", String.valueOf(DateTime.now().getMillis))
    properties.put(LOCATION_FIELD, gson.toJson(log))
    revision.setUserProperties(properties)
    revision.save()
    document
  }

  def createTrip(document: Document): Trip = {
    if (document == null || document.getCurrentRevision == null) {
      return null
    }
    val properties: util.Map[String, AnyRef] = document.getCurrentRevision.getProperties
    val tripType = new TypeToken[Trip] {}.getType
    gson.fromJson(properties.get(LOCATION_FIELD).asInstanceOf[String], tripType).asInstanceOf[Trip]
  }
}

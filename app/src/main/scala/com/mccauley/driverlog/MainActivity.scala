package com.mccauley.driverlog

import android.content.{Context, SharedPreferences}
import android.location.{Criteria, Location, LocationListener, LocationManager}
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.{FloatingActionButton, Snackbar}
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.{Menu, MenuItem, View}
import com.google.gson.Gson
import com.mccauley.driverlog.database.TripHelper

class MainActivity extends AppCompatActivity {
  val START_LOCATION_KEY = "START_LOCATION"
  val LAST_LOCATION_KEY = "LAST_LOCATION"
  val LAST_DISTANCE_KEY = "LAST_DISTANCE"
  val TRIP_BEING_LOGGED_KEY = "TRIP_BEING_LOGGED"
  var tripIsBeingLogged = false

  private def database = {
    val application = getApplication.asInstanceOf[DriverLogApplication]
    application.getDatabase()
  }

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val toolbar = findViewById(R.id.toolbar).asInstanceOf[Toolbar]
    setSupportActionBar(toolbar)
    val fab: FloatingActionButton = findViewById(R.id.fab).asInstanceOf[FloatingActionButton]

    val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    tripIsBeingLogged = Option(sharedPreferences.getString(LAST_LOCATION_KEY, null)).isDefined
    val locationManager = getSystemService(Context.LOCATION_SERVICE).asInstanceOf[LocationManager]
    val listener = new MyLocationListener(fab, locationManager)
    fab.setOnClickListener(new View.OnClickListener() {
      def onClick(view: View) {
        val criteria = new Criteria()
        criteria.setAccuracy(Criteria.ACCURACY_FINE)
        val provider = locationManager.getBestProvider(criteria, true)
        if (!tripIsBeingLogged) {
          if (provider != null) {
            tripIsBeingLogged = true
            locationManager.requestLocationUpdates(provider, 30000, 0, listener, getMainLooper)
          } else {
            Snackbar.make(view, "no location stored, location is not enabled", Snackbar.LENGTH_LONG).show
          }
        } else {
          tripIsBeingLogged = false
          locationManager.removeUpdates(listener)
          locationManager.requestSingleUpdate(provider, listener, getMainLooper)
        }
      }
    })
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.menu_main, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    val id: Int = item.getItemId
    if (id == R.id.action_settings) {
      return true
    }
    super.onOptionsItemSelected(item)
  }

  class MyLocationListener(view: View, locationManager: LocationManager) extends LocationListener {
    override def onProviderEnabled(s: String): Unit = {}

    override def onStatusChanged(s: String, i: Int, bundle: Bundle): Unit = {}

    override def onLocationChanged(currentLocation: Location): Unit = {
      val gson: Gson = new Gson()
      val locationJson = gson.toJson(currentLocation)
      val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
      val startLocationJson = Option(sharedPreferences.getString(START_LOCATION_KEY, null))
      if (startLocationJson.isEmpty) {
        sharedPreferences.edit().putString(START_LOCATION_KEY, locationJson).putString(LAST_LOCATION_KEY, locationJson).apply
        Snackbar.make(view, "Stored starting location", Snackbar.LENGTH_LONG).show
      }
      var currentDistance = sharedPreferences.getFloat(LAST_DISTANCE_KEY, 0.0f)
      val previousLocationJson = Option(sharedPreferences.getString(LAST_LOCATION_KEY, null))
      if (previousLocationJson.isDefined) {
        val previousLocation: Location = gson.fromJson(previousLocationJson.get, currentLocation.getClass)
        currentDistance += previousLocation.distanceTo(currentLocation)
      }
      if (tripIsBeingLogged) {
        sharedPreferences.edit().putString(LAST_LOCATION_KEY, gson.toJson(currentLocation)).putFloat(LAST_DISTANCE_KEY, currentDistance).apply()
      } else {
        val startLocation: Location = gson.fromJson(startLocationJson.get, currentLocation.getClass)
        TripHelper.saveTrip(database, startLocation, currentLocation, currentDistance)
        sharedPreferences.edit().remove(START_LOCATION_KEY).remove(LAST_LOCATION_KEY).remove(LAST_DISTANCE_KEY).apply
        Snackbar.make(view, "Made new trip entry", Snackbar.LENGTH_LONG).show
      }
    }

    override def onProviderDisabled(s: String): Unit = {}
  }

}

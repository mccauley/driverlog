package com.mccauley.driverlog

import java.lang.reflect.Type

import android.content.{SharedPreferences, Context}
import android.location.{LocationListener, Location, Criteria, LocationManager}
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.{Snackbar, FloatingActionButton}
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.{MenuItem, Menu, View}
import com.couchbase.lite.{UnsavedRevision, Document}
import com.google.gson.Gson

class MainActivity extends AppCompatActivity {
  val LAST_LOCATION_KEY = "LAST_LOCATION"
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val toolbar = findViewById(R.id.toolbar).asInstanceOf[Toolbar]
    setSupportActionBar(toolbar)
    val fab: FloatingActionButton = findViewById(R.id.fab).asInstanceOf[FloatingActionButton]

    val locationManager = getSystemService(Context.LOCATION_SERVICE).asInstanceOf[LocationManager]
    val listener = new MyLocationListener(fab, locationManager)
    fab.setOnClickListener(new View.OnClickListener() {
      def onClick(view: View) {
        val criteria = new Criteria()
        criteria.setAccuracy(Criteria.ACCURACY_COARSE)
        val provider = locationManager.getBestProvider(criteria, true)
        if (provider != null) {
          locationManager.requestLocationUpdates(provider, 1000, 0, listener, getMainLooper)
        } else {
          Snackbar.make(view, "no location stored, location is not enabled", Snackbar.LENGTH_LONG).show
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
    override def onProviderEnabled(s: String): Unit = ???

    override def onStatusChanged(s: String, i: Int, bundle: Bundle): Unit = ???

    override def onLocationChanged(knownLocation: Location): Unit = {
      val gson: Gson = new Gson()
      val locationJson = gson.toJson(knownLocation)
      val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
      val lastLocationJson = Option(sharedPreferences.getString(LAST_LOCATION_KEY, null))
      if (lastLocationJson.isDefined) {
        val lastLocation: Location = gson.fromJson(lastLocationJson.get, knownLocation.getClass)
        val application = getApplication.asInstanceOf[DriverLogApplication]
        val document: Document = application.getDatabase.createDocument
        val revision: UnsavedRevision = document.createRevision
        val properties = new java.util.HashMap[String, AnyRef]
        properties.put("type", "log")
        properties.put("log_id", (application.getDatabase.getLastSequenceNumber + 1).toString)
        properties.put("start_location", lastLocation)
        properties.put("end_location", knownLocation)
        revision.setUserProperties(properties)
        revision.save()
        Snackbar.make(view, "Made new log entry", Snackbar.LENGTH_LONG).show
      } else {
        sharedPreferences.edit().putString(LAST_LOCATION_KEY, locationJson).apply()
        Snackbar.make(view, "Stored location", Snackbar.LENGTH_LONG).show
      }
      locationManager.removeUpdates(this)
    }

    override def onProviderDisabled(s: String): Unit = ???
  }
}

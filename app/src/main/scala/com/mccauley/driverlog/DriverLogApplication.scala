package com.mccauley.driverlog

import java.io.IOException

import android.app.Application
import android.util.Log
import com.couchbase.lite.{CouchbaseLiteException, Database, Manager}
import com.couchbase.lite.android.AndroidContext

class DriverLogApplication extends Application {
  var manager: Manager = null
  var database: Database = null

  override def onCreate() = {
    try {
      Manager.enableLogging("Database Manager", com.couchbase.lite.util.Log.VERBOSE)
      Manager.enableLogging(com.couchbase.lite.util.Log.TAG, com.couchbase.lite.util.Log.VERBOSE)
      Manager.enableLogging(com.couchbase.lite.util.Log.TAG_SYNC_ASYNC_TASK, com.couchbase.lite.util.Log.VERBOSE)
      Manager.enableLogging(com.couchbase.lite.util.Log.TAG_SYNC, com.couchbase.lite.util.Log.VERBOSE)
      Manager.enableLogging(com.couchbase.lite.util.Log.TAG_QUERY, com.couchbase.lite.util.Log.VERBOSE)
      Manager.enableLogging(com.couchbase.lite.util.Log.TAG_VIEW, com.couchbase.lite.util.Log.VERBOSE)
      Manager.enableLogging(com.couchbase.lite.util.Log.TAG_DATABASE, com.couchbase.lite.util.Log.VERBOSE)
      manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS)
    } catch {
      case e: IOException => Log.e("DriverLogApplication", "Cannot create Manager instance", e)
      case e: CouchbaseLiteException => Log.e("DriverLogApplication", "Cannot open database", e)
    }
  }

  def getDatabase() = {
    manager.getDatabase("driverlog")
  }
}

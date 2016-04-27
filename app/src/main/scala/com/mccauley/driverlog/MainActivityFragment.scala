package com.mccauley.driverlog

import android.app.Fragment
import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.ListView
import com.couchbase.lite.LiveQuery.{ChangeEvent, ChangeListener}
import com.couchbase.lite.{Database, LiveQuery}
import com.mccauley.driverlog.database.TripHelper

import scala.ref.WeakReference

class MainActivityFragment extends Fragment {

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val view: View = inflater.inflate(R.layout.fragment_main, container, false)
    val tripList: ListView = view.findViewById(R.id.trip_list).asInstanceOf[ListView]
    val query: LiveQuery = TripHelper.query(database)
    val adapter = new TripAdapter(getActivity, query)
    tripList.setAdapter(adapter)
    database.addChangeListener(new DatabaseChangeListener(adapter))
    view
  }

  def database = getActivity.getApplicationContext.asInstanceOf[DriverLogApplication].getDatabase

  class DatabaseChangeListener extends Database.ChangeListener {
    var _adapter: WeakReference[TripAdapter] = new WeakReference[TripAdapter](null);

    def this(adapter: TripAdapter) {
      this()
      _adapter = new WeakReference[TripAdapter](adapter)
    }

    override def changed(event: Database.ChangeEvent): Unit = {
      if (event.isExternal && _adapter.get.isDefined) {
        val adapter = _adapter.get.get
        adapter.updateQueryToShowConflictingRevisions(event)
      }
    }
  }
}
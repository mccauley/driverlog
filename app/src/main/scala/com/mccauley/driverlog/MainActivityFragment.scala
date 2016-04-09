package com.mccauley.driverlog

import android.app.Fragment
import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.ListView
import com.couchbase.lite.LiveQuery.{ChangeEvent, ChangeListener}
import com.couchbase.lite.{LiveQuery, Database}
import com.mccauley.driverlog.database.TripHelper

class MainActivityFragment extends Fragment {

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val view: View = inflater.inflate(R.layout.fragment_main, container, false)
    val tripList: ListView = view.findViewById(R.id.trip_list).asInstanceOf[ListView]
    val query: LiveQuery = TripHelper.query(database)
    val adapter = new TripAdapter(getActivity, query)
    tripList.setAdapter(adapter)
    database.addChangeListener(new Database.ChangeListener() {
      def changed(event: Database.ChangeEvent) {
        if (event.isExternal) {
          adapter.updateQueryToShowConflictingRevisions(event)
        }
      }
    })
    view
  }

  def database = getActivity.getApplicationContext.asInstanceOf[DriverLogApplication].getDatabase
}
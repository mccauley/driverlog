package com.mccauley.driverlog

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.{View, ViewGroup}
import android.widget.BaseAdapter
import com.couchbase.lite.{Database, LiveQuery, QueryEnumerator}

class LiveQueryAdapter extends BaseAdapter {
  private var query: LiveQuery = null
  private var enumerator: QueryEnumerator = null
  private var context: Context = null

  def this(context: Context, query: LiveQuery) {
    this()
    this.context = context
    this.query = query
    query.addChangeListener(new LiveQueryChangeListener(context.asInstanceOf[Activity], this))
    query.start
  }

  def setEnumerator(queryEnumerator: QueryEnumerator): Unit = {
    enumerator = queryEnumerator
  }

  def getCount: Int = {
    if (enumerator != null) enumerator.getCount else 0
  }

  def getItem(i: Int): AnyRef = {
    if (enumerator != null) enumerator.getRow(i).getDocument else null
  }

  def getItemId(i: Int): Long = {
    enumerator.getRow(i).getSequenceNumber
  }

  def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    null
  }

  def invalidate {
    if (query != null) query.stop
  }

  def updateQueryToShowConflictingRevisions(event: Database.ChangeEvent) {
    context.asInstanceOf[Activity].runOnUiThread(new Runnable {
      override def run(): Unit = {
        Log.d("LiveQueryAdapter", "updateQueryToShow...")
        query.stop
        Log.d("LiveQueryAdapter", "stopped query")
        enumerator = query.getRows
        Log.d("LiveQueryAdapter", "got rows")
        notifyDataSetChanged
        Log.d("LiveQueryAdapter", "notified changed")
      }
    })
  }
}

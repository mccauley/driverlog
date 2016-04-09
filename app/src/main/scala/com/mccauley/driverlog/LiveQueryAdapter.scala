package com.mccauley.driverlog

import android.app.Activity
import android.content.Context
import android.view.{ViewGroup, View}
import android.widget.BaseAdapter
import com.couchbase.lite.{Database, QueryEnumerator, LiveQuery}

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
    (LiveQueryAdapter.this.context.asInstanceOf[Activity]).runOnUiThread(new Runnable() {
      def run {
        query.stop
        enumerator = query.getRows
        notifyDataSetChanged
      }
    })
  }
}

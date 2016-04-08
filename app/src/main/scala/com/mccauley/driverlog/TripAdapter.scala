package com.mccauley.driverlog

import android.content.Context
import android.location.{Address, Geocoder, Location}
import android.view.{LayoutInflater, ViewGroup, View}
import android.widget.TextView
import com.couchbase.lite.{LiveQuery, Document}
import com.mccauley.driverlog.database.TripHelper

class TripAdapter(context: Context, query: LiveQuery) extends LiveQueryAdapter {
  var geocoder: Geocoder = null

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    var view = convertView
    if (convertView == null) {
      val inflater: LayoutInflater = parent.getContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]
      view = inflater.inflate(R.layout.list_item_trip, null)
    }

    val document = getItem(position).asInstanceOf[Document]
    val trip = TripHelper.createTrip(document)
    if (trip != null) {
      val durationView = view.findViewById(R.id.duration).asInstanceOf[TextView]
      val distanceView = view.findViewById(R.id.distance).asInstanceOf[TextView]
      val startLocationView = view.findViewById(R.id.start_location).asInstanceOf[TextView]
      val endLocationView = view.findViewById(R.id.end_location).asInstanceOf[TextView]
      durationView.setText(trip.duration().toString)
      distanceView.setText(trip.distance().toString)
      startLocationView.setText(getLocationDescription(view.getContext, trip.startLocation))
      endLocationView.setText(getLocationDescription(view.getContext, trip.endLocation))
    }
    view
  }

  def getLocationDescription(context: Context, location: Location): String = {
    if (geocoder == null) {
      geocoder = new Geocoder(context)
    }
    val addresses: java.util.List[Address] = geocoder.getFromLocation(location.getLatitude, location.getLongitude, 1)
    if (addresses != null && !addresses.isEmpty) {
      val address = addresses.get(0)
      return String.format("%1$s, %2$s, %3$s", address.getAddressLine(0), address.getLocality, address.getSubLocality)
    }
    ""
  }

}

package com.mccauley.driverlog

import android.content.Context
import android.location.{Address, Geocoder, Location}
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import com.couchbase.lite.{Document, LiveQuery}
import com.mccauley.driverlog.database.TripHelper
import org.joda.time.Duration

import scala.math.BigDecimal.RoundingMode

class TripAdapter(context: Context, query: LiveQuery) extends LiveQueryAdapter(context, query) {
  private val ADDRESS_FORMAT = "%1$s, %2$s"
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
      durationView.setText(getDurationDescription(trip.duration))
      distanceView.setText(getDistanceDescription(trip.distance))
      startLocationView.setText(getLocationDescription(view.getContext, trip.startLocation))
      endLocationView.setText(getLocationDescription(view.getContext, trip.endLocation))
    }
    view
  }

  def getDistanceDescription(distance: Double): String = {
    BigDecimal.apply(distance).setScale(1, RoundingMode.DOWN).toString().concat("mi")
  }

  def getDurationDescription(duration: Duration): String = {
    val hours = duration.getStandardHours
    val minutes = duration.getStandardMinutes
    var durationDesc = ""
    if (hours > 0) {
      durationDesc = String.valueOf(hours).concat("h ")
    }
    if (minutes > 0) {
      durationDesc = durationDesc.concat(String.valueOf(minutes)).concat("m")
    }
    durationDesc.trim
  }

  def getLocationDescription(context: Context, location: Location): String = {
    if (geocoder == null) {
      geocoder = new Geocoder(context)
    }
    val addresses: java.util.List[Address] = geocoder.getFromLocation(location.getLatitude, location.getLongitude, 1)
    if (addresses != null && !addresses.isEmpty) {
      val address = addresses.get(0)
      if (address.getAddressLine(0) != null) {
        return String.format(ADDRESS_FORMAT, address.getAddressLine(0), address.getAddressLine(1))
      }
    }
    String.format(ADDRESS_FORMAT, String.valueOf(location.getLatitude), String.valueOf(location.getLongitude))
  }

}

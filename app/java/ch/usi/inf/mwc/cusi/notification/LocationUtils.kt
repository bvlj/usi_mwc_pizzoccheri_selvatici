package ch.usi.inf.mwc.cusi.notification

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import java.util.*
import kotlin.math.*

object LocationUtils {
    private const val LOWER_LEFT_LATITUDE = 45.848586
    private const val LOWER_LEFT_LONGITUDE = 8.921750
    private const val UPPER_RIGHT_LATITUDE = 46.251246
    private const val UPPER_RIGHT_LONGITUDE = 9.059947

    private const val EARTH_RADIUS = 6_371_000 // meters

    fun addressFromString(context: Context, address: String): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocationName(
            address,
            1,
            LOWER_LEFT_LATITUDE,
            LOWER_LEFT_LONGITUDE,
            UPPER_RIGHT_LATITUDE,
            UPPER_RIGHT_LONGITUDE
        ).firstOrNull()
    }

    fun getLastGoodLocation(context: Context): Location? {
        val lm = context.getSystemService(LocationManager::class.java) ?: return null
        return try {
            lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        } catch (e: SecurityException) {
            null
        }
    }

    /**
     * Compute distance between two points using
     * haversine formula.
     */
    operator fun Location.minus(address: Address): Double {
        // https://en.wikipedia.org/wiki/Haversine_formula
        val toRadians = PI / 180.0
        val lon1 = longitude * toRadians
        val lat1 = latitude * toRadians
        val lon2 = address.longitude * toRadians
        val lat2 = address.latitude * toRadians

        val deltaLon = lon1 - lon2
        val deltaLat = lat1 - lat2
        val a = sin(deltaLat / 2.0).pow(2.0) +
                cos(lat1) *
                cos(lat2) *
                sin(deltaLon / 2.0).pow(2.0)
        val c = 2 * asin(sqrt(a))
        return c * EARTH_RADIUS
    }
}
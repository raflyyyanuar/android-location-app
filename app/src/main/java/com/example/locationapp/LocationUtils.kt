package com.example.locationapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class LocationUtils(val context : Context) {
    private val _fusedLocationClient : FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("Missing")
    fun requestLocationUpdates(viewModel: LocationViewModel) {
        // Implement an abstract class to update location
        // to the current latitude and longitude
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    val location = LocationData(it.latitude, it.longitude)
                    viewModel.updateLocation(location)
                }
            }
        }

        // A location request object to get high accuracy location
        // that gets updated every 1000 ms
        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .build()

        // Actually request location updates
        // on the Fused Location Provider Client object
        _fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper(),
        )
    }

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat
            .checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                &&
            ContextCompat
            .checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    // Reverse latitude and longitude to a human-readable address
    fun reverseGeocodeLocation(location: LocationData) : String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val coordinate = LatLng(location.latitude, location.longitude)

        // Get max result of 1 address from the current coordinate
        val addresses : MutableList<Address>? = geocoder.getFromLocation(
            coordinate.latitude,
            coordinate.longitude,
            1,
        )

        return if(addresses?.isNotEmpty() == true) {
            addresses[0].getAddressLine(0)
        }
        else {
            "No addresses found!"
        }
    }
}
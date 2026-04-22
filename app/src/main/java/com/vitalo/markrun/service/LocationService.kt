package com.vitalo.markrun.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    var onLocationUpdate: ((Location) -> Unit)? = null

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        1000L
    ).setMinUpdateDistanceMeters(2f)
        .setWaitForAccurateLocation(false)
        .build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                _currentLocation.value = location
                onLocationUpdate?.invoke(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startUpdating() {
        if (_isUpdating.value) return
        _isUpdating.value = true
        fusedClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopUpdating() {
        _isUpdating.value = false
        fusedClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(callback: (Location?) -> Unit) {
        fusedClient.lastLocation.addOnSuccessListener { location ->
            callback(location)
        }.addOnFailureListener {
            callback(null)
        }
    }
}

package com.example.cheapchomp.network

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LocationService(private val context: Context) {
    // Initialize the FusedLocationProviderClient, which provides access location using GPS, Wi-Fi, and cellular network
    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    //Function to retrieve the current location
    suspend fun getCurrentLocation(): LatLng = suspendCoroutine { continuation ->
        try {
            // Check permissions first
            if (hasLocationPermissions()) {
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location ->
                        location?.let {
                            continuation.resume(LatLng(it.latitude, it.longitude))
                        } ?: run {
                            // Fallback location if we can't get the current location
                            continuation.resume(LatLng(37.7749, -122.4194))
                        }
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWithException(e)
                    }
            } else {
                continuation.resumeWithException(SecurityException("Location permissions not granted"))
            }
        } catch (e: SecurityException) {
            continuation.resumeWithException(e)
        }
    }
    //Check if permissions were granted
    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
    //Default location in case we cannot access the current location
    companion object {
        val FALLBACK_LOCATION = LatLng(37.7749, -122.4194) // San Francisco
    }
}
package com.example.cheapchomp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.launch
import android.content.Context
import androidx.compose.runtime.rememberCoroutineScope

// LocationService.kt
class LocationService(private val context: Context) {
    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

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

}

@Composable
fun GoogleMapScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    latitude: Double = 0.0,
    longitude: Double = 0.0
) {
    val context = LocalContext.current
    val locationService = remember { LocationService(context) }
    var currentLocation by remember { mutableStateOf(LatLng(latitude, longitude)) }
    val coroutineScope = rememberCoroutineScope()

    // Add this permissions array back
    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsGranted ->
        if (permissionsGranted.all { it.value }) {
            coroutineScope.launch {
                try {
                    val location = locationService.getCurrentLocation()
                    currentLocation = location
                    navController.navigate("KrogerProductScreen/${location.latitude}/${location.longitude}")
                } catch (e: Exception) {
                    Log.e("Location", "Error getting location", e)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (permissions.any {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }) {
            launcher.launch(permissions)
        } else {
            try {
                val location = locationService.getCurrentLocation()
                currentLocation = location
                navController.navigate("KrogerProductScreen/${location.latitude}/${location.longitude}")
            } catch (e: Exception) {
                Log.e("Location", "Error getting location", e)
            }
        }
    }

    // Substitute location in case we cannot use the API
    val fallbackLocation = LatLng(37.7749, -122.4194) //San Francisco

    // Update cameraPositionState on location change
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            currentLocation.takeIf { it != LatLng(0.0, 0.0) } ?: fallbackLocation, 15f
        )
    }

    // If currentLocation is updated, make sure camera is updated as well
    LaunchedEffect(currentLocation) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
    }

    // Creating a MarkerState for the current location or fallback location
    val markerState = remember { mutableStateOf(MarkerState(position = fallbackLocation)) }

    // Update marker position when location is updated
    LaunchedEffect(currentLocation) {
        markerState.value = MarkerState(position = currentLocation)
    }

    // Display Google Map with Marker
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = markerState.value,  // This marker pinpoints the current location (it's the red arrow)
            title = "Current Location",
            snippet = "You are here!"
        )
    }
}


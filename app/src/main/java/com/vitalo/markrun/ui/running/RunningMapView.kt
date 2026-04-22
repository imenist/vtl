package com.vitalo.markrun.ui.running

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.vitalo.markrun.ui.theme.GradientGreenStart

@Composable
fun RunningMapView(
    locations: List<LatLng>,
    modifier: Modifier = Modifier,
    isFollowingUser: Boolean = true,
    fitToRoute: Boolean = false
) {
    val cameraPositionState = rememberCameraPositionState {
        if (locations.isNotEmpty()) {
            position = CameraPosition.fromLatLngZoom(locations.last(), 16f)
        }
    }

    LaunchedEffect(locations.lastOrNull(), isFollowingUser) {
        if (isFollowingUser && locations.isNotEmpty()) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(locations.last(), 16f),
                durationMs = 300
            )
        }
    }

    LaunchedEffect(fitToRoute, locations.size) {
        if (fitToRoute && locations.size >= 2) {
            val boundsBuilder = LatLngBounds.builder()
            locations.forEach { boundsBuilder.include(it) }
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 48),
                durationMs = 300
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = false
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false,
            compassEnabled = false
        )
    ) {
        if (locations.size >= 2) {
            Polyline(
                points = locations,
                color = GradientGreenStart,
                width = 8f
            )
        }

        if (locations.isNotEmpty()) {
            Marker(
                state = MarkerState(position = locations.last()),
                title = "Current Position"
            )
        }

        if (locations.size >= 2) {
            Marker(
                state = MarkerState(position = locations.first()),
                title = "Start",
                alpha = 0.7f
            )
        }
    }
}

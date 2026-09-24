package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DriverLocationData
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.LogisticsBlue
import com.example.ui.theme.SuccessGreen
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun GoogleMapsView(
    driverLocation: DriverLocationData?,
    modifier: Modifier = Modifier,
    pickupLatLng: LatLng = LatLng(12.9784, 77.6408), // Indiranagar 100ft Rd
    dropoffLatLng: LatLng = LatLng(12.9352, 77.6245), // Koramangala 4th Block
    pickupTitle: String = "Pickup: Indiranagar",
    dropoffTitle: String = "Drop-off: Koramangala",
    isInteractive: Boolean = true
) {
    val coroutineScope = rememberCoroutineScope()

    val currentDriverLatLng = remember(driverLocation?.latitude, driverLocation?.longitude) {
        if (driverLocation != null) {
            LatLng(driverLocation.latitude, driverLocation.longitude)
        } else {
            LatLng(12.9710, 77.6350) // Default midpoint along freight route
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentDriverLatLng, 14.5f)
    }

    // Auto-pan camera smoothly when driver moves in interactive mode
    LaunchedEffect(currentDriverLatLng) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLng(currentDriverLatLng),
            durationMs = 800
        )
    }

    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var isTrafficEnabled by remember { mutableStateOf(true) }

    val mapProperties = remember(mapType, isTrafficEnabled) {
        MapProperties(
            mapType = mapType,
            isTrafficEnabled = isTrafficEnabled,
            isMyLocationEnabled = false
        )
    }

    val mapUiSettings = remember(isInteractive) {
        MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = true,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false,
            scrollGesturesEnabled = isInteractive,
            zoomGesturesEnabled = isInteractive,
            tiltGesturesEnabled = isInteractive,
            rotationGesturesEnabled = isInteractive
        )
    }

    // Route points from pickup -> live driver location -> dropoff
    val routePoints = remember(currentDriverLatLng) {
        listOf(
            pickupLatLng,
            LatLng(12.9650, 77.6380),
            currentDriverLatLng,
            LatLng(12.9480, 77.6310),
            dropoffLatLng
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(18.dp))
            .testTag("google_maps_view_container")
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings
        ) {
            // Pickup Marker
            Marker(
                state = MarkerState(position = pickupLatLng),
                title = pickupTitle,
                snippet = "Scheduled Goods Pickup",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )

            // Dropoff Marker
            Marker(
                state = MarkerState(position = dropoffLatLng),
                title = dropoffTitle,
                snippet = "Consignee Destination",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            )

            // Live Driver GPS Marker
            Marker(
                state = MarkerState(position = currentDriverLatLng),
                title = "Driver: Ravi Kumar (Tata Ace)",
                snippet = "Speed: ${driverLocation?.speedKmh ?: 24.5f} km/h • KA 05 MX 2190",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )

            // Accuracy Radius Circle
            Circle(
                center = currentDriverLatLng,
                radius = (driverLocation?.accuracyMeters?.toDouble() ?: 20.0).coerceAtLeast(35.0),
                fillColor = LogisticsBlue.copy(alpha = 0.2f),
                strokeColor = LogisticsBlue,
                strokeWidth = 2f
            )

            // Route Polyline Glow
            Polyline(
                points = routePoints,
                color = AmberPrimary.copy(alpha = 0.4f),
                width = 16f
            )

            // Main Polyline
            Polyline(
                points = routePoints,
                color = AmberPrimary,
                width = 8f
            )
        }

        // Overlay Controls (when interactive)
        if (isInteractive) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        mapType = if (mapType == MapType.NORMAL) MapType.HYBRID else MapType.NORMAL
                    },
                    containerColor = Color.White,
                    contentColor = AmberPrimary,
                    modifier = Modifier.testTag("toggle_map_type_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Map Style"
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(currentDriverLatLng, 16f),
                                durationMs = 600
                            )
                        }
                    },
                    containerColor = AmberPrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("recenter_driver_gps_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = "Recenter Driver GPS",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

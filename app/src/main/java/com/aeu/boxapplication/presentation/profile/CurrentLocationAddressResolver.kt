package com.aeu.boxapplication.presentation.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address as PlatformAddress
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

fun hasLocationPermission(context: Context): Boolean {
    val fineGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarseGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return fineGranted || coarseGranted
}

suspend fun resolveCurrentAddress(context: Context): Result<String> = runCatching {
    if (!Geocoder.isPresent()) {
        error("Address lookup is unavailable on this device.")
    }

    val location = withTimeoutOrNull(15_000L) {
        awaitCurrentLocation(context)
    } ?: error("Unable to determine your current location. Check GPS and try again.")

    reverseGeocode(context, location)
        ?.takeIf { it.isNotBlank() }
        ?: error("Unable to convert your current location into a street address.")
}

@SuppressLint("MissingPermission")
private suspend fun awaitCurrentLocation(context: Context): Location =
    suspendCancellableCoroutine { continuation ->
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            continuation.resumeWithException(
                IllegalStateException("Location service is unavailable.")
            )
            return@suspendCancellableCoroutine
        }

        val enabledProviders = listOf(
            LocationManager.NETWORK_PROVIDER,
            LocationManager.GPS_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        ).filter { provider ->
            runCatching { locationManager.isProviderEnabled(provider) }.getOrDefault(false)
        }

        val lastKnownLocation = enabledProviders
            .mapNotNull { provider ->
                runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull()
            }
            .maxByOrNull { it.time }

        if (lastKnownLocation != null) {
            continuation.resume(lastKnownLocation)
            return@suspendCancellableCoroutine
        }

        val provider = enabledProviders.firstOrNull()
        if (provider == null) {
            continuation.resumeWithException(
                IllegalStateException("Turn on location services and try again.")
            )
            return@suspendCancellableCoroutine
        }

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (!continuation.isActive) {
                    return
                }
                locationManager.removeUpdates(this)
                continuation.resume(location)
            }
        }

        continuation.invokeOnCancellation {
            locationManager.removeUpdates(listener)
        }

        runCatching {
            @Suppress("DEPRECATION")
            locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
        }.onFailure { error ->
            locationManager.removeUpdates(listener)
            if (continuation.isActive) {
                continuation.resumeWithException(error)
            }
        }
    }

private suspend fun reverseGeocode(
    context: Context,
    location: Location
): String? {
    val geocoder = Geocoder(context, Locale.getDefault())

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        suspendCancellableCoroutine { continuation ->
            geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<PlatformAddress>) {
                        if (continuation.isActive) {
                            continuation.resume(
                                addresses.firstOrNull()?.toSingleLineAddress()
                            )
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        if (continuation.isActive) {
                            continuation.resumeWithException(
                                IllegalStateException(
                                    errorMessage ?: "Unable to read your current address."
                                )
                            )
                        }
                    }
                }
            )
        }
    } else {
        @Suppress("DEPRECATION")
        geocoder.getFromLocation(location.latitude, location.longitude, 1)
            ?.firstOrNull()
            ?.toSingleLineAddress()
    }
}

private fun PlatformAddress.toSingleLineAddress(): String {
    val firstLine = runCatching { getAddressLine(0) }.getOrNull()
    if (!firstLine.isNullOrBlank()) {
        return firstLine
    }

    val parts = buildList {
        thoroughfare?.takeIf { it.isNotBlank() }?.let(::add)
        subLocality?.takeIf { it.isNotBlank() }?.let(::add)
        locality?.takeIf { it.isNotBlank() }?.let(::add)
        adminArea?.takeIf { it.isNotBlank() }?.let(::add)
        postalCode?.takeIf { it.isNotBlank() }?.let(::add)
        countryName?.takeIf { it.isNotBlank() }?.let(::add)
    }

    return parts.joinToString(", ")
}

package com.aeu.boxapplication.core.notifications

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * In-process broadcast channel that lets BoxFirebaseMessagingService signal
 * the running UI to refresh data when a shipment status change notification arrives,
 * including when the app is already in the foreground.
 */
object AppRefreshChannel {
    private val _shipmentUpdated = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val shipmentUpdated: SharedFlow<Unit> = _shipmentUpdated.asSharedFlow()

    fun notifyShipmentUpdated() {
        _shipmentUpdated.tryEmit(Unit)
    }
}

package com.aeu.boxapplication.domain.repository

import com.aeu.boxapplication.core.network.NetworkResult
import com.aeu.boxapplication.domain.model.Shipment

interface ShipmentRepository {
    suspend fun getShipments(page: Int): NetworkResult<List<Shipment>>
    suspend fun getShipment(shipmentId: String): NetworkResult<Shipment>
}

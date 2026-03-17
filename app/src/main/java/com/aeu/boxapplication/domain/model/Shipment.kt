package com.aeu.boxapplication.domain.model

data class Shipment(
    val id: String,
    val subscriptionId: String,
    val shipmentDate: String,
    val status: String, // PENDING, SHIPPED, DELIVERED
    val trackingNumber: String?
)

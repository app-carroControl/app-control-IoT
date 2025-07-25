package com.IoT.carrobot.domain.model

data class Device(
    val id: String,
    val name: String,
    val address: String,
    val type: ConnectionType,
    val isConnected: Boolean = false,
    val signalStrength: Int? = null
)
package com.IoT.carrobot.domain.model

data class ConnectionState(
    val status: ConnectionStatus,
    val device: Device? = null,
    val errorMessage: String? = null
)

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}
package com.IoT.carrobot.domain.model

data class RobotState(
    // Estados básicos
    val isMoving: Boolean = false,
    val currentSpeed: Int = 0,

    // Estados de LEDs
    val frontLedOn: Boolean = false,
    val backLedOn: Boolean = false,
    val leftLedOn: Boolean = false,
    val rightLedOn: Boolean = false,

    // Estados de sonido
    val hornActive: Boolean = false,

    // Sensores
    val ultrasonicDistance: Float? = null,

    // ================================
    // NUEVAS PROPIEDADES DE AUTOMATIZACIÓN
    // ================================

    // Modos automáticos
    val obstacleAvoidanceOn: Boolean = false,
    val lineFollowingOn: Boolean = false,
    val autoLightsOn: Boolean = false,

    // Estados adicionales
    val batteryLevel: Int = 100,
    val temperatureCelsius: Float? = null,
    val lightLevel: Int? = null, // Para las luces automáticas
    val isCalibrating: Boolean = false
)

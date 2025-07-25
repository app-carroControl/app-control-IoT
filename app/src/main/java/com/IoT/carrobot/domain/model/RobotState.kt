package com.IoT.carrobot.domain.model

data class RobotState(
    val isMoving: Boolean = false,
    val currentSpeed: Int = 0,
    val frontLedOn: Boolean = false,
    val backLedOn: Boolean = false,
    val leftLedOn: Boolean = false,
    val rightLedOn: Boolean = false,
    val hornActive: Boolean = false,
    val ultrasonicDistance: Float? = null,
    val servoAngle: Int = 90
)
package com.IoT.carrobot.domain.model

data class RobotCommand(
    val action: RobotAction,
    val speed: Int = 0,
    val value: Boolean = false // Para LEDs, bocina, etc.
)

enum class RobotAction {
    // Movimiento
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    STOP,

    // LEDs
    LED_FRONT_ON,
    LED_FRONT_OFF,
    LED_BACK_ON,
    LED_BACK_OFF,
    LED_LEFT_ON,
    LED_LEFT_OFF,
    LED_RIGHT_ON,
    LED_RIGHT_OFF,

    // Sonido
    HORN_ON,
    HORN_OFF,
    BEEP,

    // Sensores
    ULTRASONIC_READ,
    SERVO_ROTATE,

    // Cámara
    CAMERA_UP,
    CAMERA_DOWN,
    CAMERA_LEFT,
    CAMERA_RIGHT,
    CAMERA_CENTER
}
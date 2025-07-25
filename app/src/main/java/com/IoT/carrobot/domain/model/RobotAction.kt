package com.IoT.carrobot.domain.model

enum class RobotAction {
    // Acciones de movimiento
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    STOP,

    // Acciones de LEDs
    LED_FRONT_ON,
    LED_FRONT_OFF,
    LED_BACK_ON,
    LED_BACK_OFF,
    LED_LEFT_ON,
    LED_LEFT_OFF,
    LED_RIGHT_ON,
    LED_RIGHT_OFF,

    // Acciones de sonido
    HORN_ON,
    HORN_OFF,
    BEEP,

    // Sensores
    ULTRASONIC_READ,

    // ================================
    // NUEVAS ACCIONES DE AUTOMATIZACIÓN
    // ================================

    // Automatización
    OBSTACLE_AVOIDANCE_ON,
    OBSTACLE_AVOIDANCE_OFF,
    LINE_FOLLOWING_ON,
    LINE_FOLLOWING_OFF,
    AUTO_LIGHTS_ON,
    AUTO_LIGHTS_OFF,

    // Sistema
    CALIBRATE_SENSORS,
    GET_STATUS,
    EMERGENCY_STOP,

    // Futuras expansiones
    SET_CRUISE_CONTROL,
    CAMERA_UP,
    CAMERA_DOWN,
    CAMERA_LEFT,
    CAMERA_RIGHT,
    CAMERA_CENTER
}
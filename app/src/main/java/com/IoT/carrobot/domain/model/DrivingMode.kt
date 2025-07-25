package com.IoT.carrobot.domain.model

enum class DrivingMode {
    MANUAL,      // Control manual completo
    ECO,         // Velocidad limitada, movimientos suaves
    SPORT,       // Respuesta rápida, velocidad máxima
    AUTONOMOUS   // Robot decide basándose en sensores
}
package com.IoT.carrobot.domain.model

data class RobotCommand(
    val action: RobotAction,
    val speed: Int = 0,
    val value: Any? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Convierte el comando a un string para envío por WiFi/Bluetooth
     * Formato: "ACTION:SPEED:VALUE:TIMESTAMP"
     */
    fun toCommandString(): String {
        return "${action.name}:$speed:${value ?: ""}:$timestamp"
    }

    companion object {
        /**
         * Crea un comando desde un string recibido
         */
        fun fromString(commandString: String): RobotCommand? {
            return try {
                val parts = commandString.split(":")
                if (parts.size >= 2) {
                    RobotCommand(
                        action = RobotAction.valueOf(parts[0]),
                        speed = parts.getOrNull(1)?.toIntOrNull() ?: 0,
                        value = parts.getOrNull(2),
                        timestamp = parts.getOrNull(3)?.toLongOrNull() ?: System.currentTimeMillis()
                    )
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
}
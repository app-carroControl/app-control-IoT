// Actualizar: app/src/main/java/com/IoT/carrobot/presentation/viewmodel/MainViewModel.kt
package com.IoT.carrobot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.IoT.carrobot.domain.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel : ViewModel() {

    private val _connectionState = MutableStateFlow(
        ConnectionState(status = ConnectionStatus.DISCONNECTED)
    )
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _robotState = MutableStateFlow(RobotState())
    val robotState: StateFlow<RobotState> = _robotState.asStateFlow()

    private val _speed = MutableStateFlow(50)
    val speed: StateFlow<Int> = _speed.asStateFlow()

    private val _commandResult = MutableStateFlow("")
    val commandResult: StateFlow<String> = _commandResult.asStateFlow()

    private var currentDevice: Device? = null

    fun connectWifi(ipAddress: String) {
        viewModelScope.launch {
            try {
                Timber.d("Conectando a WiFi: $ipAddress")
                _connectionState.value = ConnectionState(status = ConnectionStatus.CONNECTING)

                delay(2000) // Simular conexión

                val device = Device(
                    id = "wifi_$ipAddress",
                    name = "CarroBot WiFi",
                    address = ipAddress,
                    type = ConnectionType.WIFI,
                    isConnected = true
                )

                currentDevice = device
                _connectionState.value = ConnectionState(
                    status = ConnectionStatus.CONNECTED,
                    device = device
                )

                Timber.d("Conectado por WiFi exitosamente")

            } catch (e: Exception) {
                Timber.e(e, "Error conectando por WiFi")
                _connectionState.value = ConnectionState(
                    status = ConnectionStatus.ERROR,
                    errorMessage = "Error WiFi: ${e.message}"
                )
            }
        }
    }

    fun connectBluetooth() {
        viewModelScope.launch {
            try {
                Timber.d("Conectando por Bluetooth")
                _connectionState.value = ConnectionState(status = ConnectionStatus.CONNECTING)

                delay(3000) // Simular conexión

                val device = Device(
                    id = "bt_carrobot",
                    name = "CarroBot BT",
                    address = "00:11:22:33:44:55",
                    type = ConnectionType.BLUETOOTH,
                    isConnected = true
                )

                currentDevice = device
                _connectionState.value = ConnectionState(
                    status = ConnectionStatus.CONNECTED,
                    device = device
                )

                Timber.d("Conectado por Bluetooth exitosamente")

            } catch (e: Exception) {
                Timber.e(e, "Error conectando por Bluetooth")
                _connectionState.value = ConnectionState(
                    status = ConnectionStatus.ERROR,
                    errorMessage = "Error Bluetooth: ${e.message}"
                )
            }
        }
    }

    fun setSpeed(newSpeed: Int) {
        _speed.value = newSpeed.coerceIn(0, 100)
    }

    // Funciones de movimiento
    fun moveForward() = sendMovementCommand(RobotAction.FORWARD)
    fun moveBackward() = sendMovementCommand(RobotAction.BACKWARD)
    fun turnLeft() = sendMovementCommand(RobotAction.LEFT)
    fun turnRight() = sendMovementCommand(RobotAction.RIGHT)
    fun stopMovement() = sendMovementCommand(RobotAction.STOP)

    private fun sendMovementCommand(action: RobotAction) {
        viewModelScope.launch {
            if (!isConnected()) return@launch

            val command = RobotCommand(action = action, speed = _speed.value)
            sendCommand(command)

            _robotState.value = _robotState.value.copy(
                isMoving = action != RobotAction.STOP,
                currentSpeed = if (action != RobotAction.STOP) _speed.value else 0
            )
        }
    }

    // Funciones de LEDs
    fun toggleFrontLed() {
        viewModelScope.launch {
            if (!isConnected()) return@launch

            val newState = !_robotState.value.frontLedOn
            val action = if (newState) RobotAction.LED_FRONT_ON else RobotAction.LED_FRONT_OFF

            sendCommand(RobotCommand(action = action, value = newState))
            _robotState.value = _robotState.value.copy(frontLedOn = newState)
        }
    }

    fun toggleBackLed() {
        viewModelScope.launch {
            if (!isConnected()) return@launch

            val newState = !_robotState.value.backLedOn
            val action = if (newState) RobotAction.LED_BACK_ON else RobotAction.LED_BACK_OFF

            sendCommand(RobotCommand(action = action, value = newState))
            _robotState.value = _robotState.value.copy(backLedOn = newState)
        }
    }

    fun toggleLeftLed() {
        viewModelScope.launch {
            if (!isConnected()) return@launch

            val newState = !_robotState.value.leftLedOn
            val action = if (newState) RobotAction.LED_LEFT_ON else RobotAction.LED_LEFT_OFF

            sendCommand(RobotCommand(action = action, value = newState))
            _robotState.value = _robotState.value.copy(leftLedOn = newState)
        }
    }

    fun toggleRightLed() {
        viewModelScope.launch {
            if (!isConnected()) return@launch

            val newState = !_robotState.value.rightLedOn
            val action = if (newState) RobotAction.LED_RIGHT_ON else RobotAction.LED_RIGHT_OFF

            sendCommand(RobotCommand(action = action, value = newState))
            _robotState.value = _robotState.value.copy(rightLedOn = newState)
        }
    }

    // ================================
    // NUEVAS FUNCIONES DE AUTOMATIZACIÓN
    // ================================

    /**
     * Activa/desactiva el modo de evitar obstáculos
     */
    fun toggleObstacleAvoidance() {
        viewModelScope.launch {
            if (!isConnected()) return@launch

            val newState = !_robotState.value.obstacleAvoidanceOn
            val action = if (newState) RobotAction.OBSTACLE_AVOIDANCE_ON else RobotAction.OBSTACLE_AVOIDANCE_OFF

            sendCommand(RobotCommand(action = action, value = newState))
            _robotState.value = _robotState.value.copy(
                obstacleAvoidanceOn = newState,
                // Si se activa evitar obstáculos, desactivar seguir línea
                lineFollowingOn = if (newState) false else _robotState.value.lineFollowingOn
            )

            Timber.d("Evitar obstáculos: ${if (newState) "ACTIVADO" else "DESACTIVADO"}")
        }
    }

    /**
     * Activa/desactiva el modo de seguir línea
     */
    fun toggleLineFollowing() {
        viewModelScope.launch {
            if (!isConnected()) return@launch

            val newState = !_robotState.value.lineFollowingOn
            val action = if (newState) RobotAction.LINE_FOLLOWING_ON else RobotAction.LINE_FOLLOWING_OFF

            sendCommand(RobotCommand(action = action, value = newState))
            _robotState.value = _robotState.value.copy(
                lineFollowingOn = newState,
                // Si se activa seguir línea, desactivar evitar obstáculos
                obstacleAvoidanceOn = if (newState) false else _robotState.value.obstacleAvoidanceOn
            )

            Timber.d("Seguir línea: ${if (newState) "ACTIVADO" else "DESACTIVADO"}")
        }
    }

    /**
     * Activa/desactiva las luces automáticas
     */
    fun toggleAutoLights() {
        viewModelScope.launch {
            if (!isConnected()) return@launch

            val newState = !_robotState.value.autoLightsOn
            val action = if (newState) RobotAction.AUTO_LIGHTS_ON else RobotAction.AUTO_LIGHTS_OFF

            sendCommand(RobotCommand(action = action, value = newState))
            _robotState.value = _robotState.value.copy(autoLightsOn = newState)

            Timber.d("Luces automáticas: ${if (newState) "ACTIVADAS" else "DESACTIVADAS"}")
        }
    }

    /**
     * Calibra todos los sensores del robot
     */
    fun calibrateSensors() {
        viewModelScope.launch {
            if (!isConnected()) return@launch

            Timber.d("Iniciando calibración de sensores...")
            sendCommand(RobotCommand(action = RobotAction.CALIBRATE_SENSORS))

            // Simular proceso de calibración
            _commandResult.value = "🔄 Calibrando sensores..."
            delay(3000)
            _commandResult.value = "✅ Sensores calibrados"

            Timber.d("Calibración completada")
        }
    }

    // ================================
    // FUNCIONES DE SONIDO (sin cambios)
    // ================================

    /**
     * Activa la bocina por 1 segundo
     */
    fun activateHorn() {
        viewModelScope.launch {
            if (!isConnected()) return@launch

            sendCommand(RobotCommand(action = RobotAction.HORN_ON))
            _robotState.value = _robotState.value.copy(hornActive = true)

            delay(1000) // Horn por 1 segundo
            sendCommand(RobotCommand(action = RobotAction.HORN_OFF))
            _robotState.value = _robotState.value.copy(hornActive = false)
        }
    }

    /**
     * Emite un beep corto
     */
    fun beep() {
        viewModelScope.launch {
            if (!isConnected()) return@launch
            sendCommand(RobotCommand(action = RobotAction.BEEP))
        }
    }

    // ================================
    // FUNCIÓN PARA LEER ULTRASONIDO
    // ================================

    /**
     * Lee la distancia del sensor ultrasónico
     */
    fun readUltrasonic() {
        viewModelScope.launch {
            if (!isConnected()) return@launch

            sendCommand(RobotCommand(action = RobotAction.ULTRASONIC_READ))

            // Simular lectura de sensor
            delay(100)
            val distance = (5..200).random().toFloat()
            _robotState.value = _robotState.value.copy(ultrasonicDistance = distance)
        }
    }

    // ================================
    // FUNCIONES PRIVADAS Y UTILITARIAS
    // ================================

    private suspend fun sendCommand(command: RobotCommand) {
        try {
            val commandString = "${command.action.name}:${command.speed}:${command.value}"
            Timber.d("Enviando comando: $commandString")

            // Aquí implementaremos el envío real por WiFi/Bluetooth
            delay(50) // Simular latencia

            _commandResult.value = "✓ ${command.action.name}"

        } catch (e: Exception) {
            Timber.e(e, "Error enviando comando")
            _commandResult.value = "✗ Error: ${e.message}"
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            currentDevice = null
            _connectionState.value = ConnectionState(status = ConnectionStatus.DISCONNECTED)
            _robotState.value = RobotState() // Reset robot state
        }
    }

    private fun isConnected(): Boolean {
        return _connectionState.value.status == ConnectionStatus.CONNECTED
    }

    // ================================
    // FUNCIONES ADICIONALES PARA FUTURAS MEJORAS
    // ================================

    /**
     * Función para obtener el estado completo del robot
     */
    fun refreshRobotStatus() {
        viewModelScope.launch {
            if (!isConnected()) return@launch

            sendCommand(RobotCommand(action = RobotAction.GET_STATUS))
            // Aquí podrías actualizar el estado completo del robot
            // basándote en la respuesta del dispositivo
        }
    }

    /**
     * Función de emergencia - detiene todo
     */
    fun emergencyStop() {
        viewModelScope.launch {
            if (!isConnected()) return@launch

            // Detener movimiento
            sendCommand(RobotCommand(action = RobotAction.STOP))

            // Desactivar automatización
            sendCommand(RobotCommand(action = RobotAction.OBSTACLE_AVOIDANCE_OFF))
            sendCommand(RobotCommand(action = RobotAction.LINE_FOLLOWING_OFF))

            // Apagar sonidos
            sendCommand(RobotCommand(action = RobotAction.HORN_OFF))

            // Actualizar estado
            _robotState.value = _robotState.value.copy(
                isMoving = false,
                currentSpeed = 0,
                hornActive = false,
                obstacleAvoidanceOn = false,
                lineFollowingOn = false
            )

            Timber.d("🚨 PARADA DE EMERGENCIA ACTIVADA")
            _commandResult.value = "🚨 PARADA DE EMERGENCIA"
        }
    }
}
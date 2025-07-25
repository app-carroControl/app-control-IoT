package com.IoT.carrobot.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.IoT.carrobot.domain.model.ConnectionState
import com.IoT.carrobot.domain.model.ConnectionStatus
import com.IoT.carrobot.domain.model.RobotState
import com.IoT.carrobot.presentation.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarroBotApp(viewModel: MainViewModel) {
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val robotState by viewModel.robotState.collectAsStateWithLifecycle()
    val speed by viewModel.speed.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        LeftControlPanel(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            robotState = robotState,
            onFrontLedToggle = viewModel::toggleFrontLed,
            onBackLedToggle = viewModel::toggleBackLed,
            onLeftLedToggle = viewModel::toggleLeftLed,
            onRightLedToggle = viewModel::toggleRightLed,
            onHornActivate = viewModel::activateHorn,
            onBeep = viewModel::beep,
            onReadUltrasonic = viewModel::readUltrasonic
        )

        // Panel central - Conexión y controles principales
        CenterControlPanel(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight(),
            connectionState = connectionState,
            speed = speed,
            onWifiConnect = { viewModel.connectWifi("192.168.1.100") },
            onBluetoothConnect = viewModel::connectBluetooth,
            onDisconnect = viewModel::disconnect,
            onSpeedChange = viewModel::setSpeed,
            onForward = viewModel::moveForward,
            onBackward = viewModel::moveBackward,
            onLeft = viewModel::turnLeft,
            onRight = viewModel::turnRight,
            onStop = viewModel::stopMovement
        )

        // Panel derecho - Estado y información
        RightInfoPanel(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            robotState = robotState,
            connectionState = connectionState
        )
    }
}
@Composable
fun LeftControlPanel(
    modifier: Modifier = Modifier,
    robotState: RobotState,
    onFrontLedToggle: () -> Unit,
    onBackLedToggle: () -> Unit,
    onLeftLedToggle: () -> Unit,
    onRightLedToggle: () -> Unit,
    onHornActivate: () -> Unit,
    onBeep: () -> Unit,
    onReadUltrasonic: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🔧 Controles",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // LEDs Section
            Text(
                text = "💡 LEDs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LedButton(
                text = "LED Frontal",
                isOn = robotState.frontLedOn,
                onClick = onFrontLedToggle,
                icon = Icons.Default.Lightbulb
            )

            LedButton(
                text = "LED Trasero",
                isOn = robotState.backLedOn,
                onClick = onBackLedToggle,
                icon = Icons.Default.Lightbulb
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LedButton(
                    modifier = Modifier.weight(1f),
                    text = "LED Izq",
                    isOn = robotState.leftLedOn,
                    onClick = onLeftLedToggle,
                    icon = Icons.Default.TurnLeft
                )

                LedButton(
                    modifier = Modifier.weight(1f),
                    text = "LED Der",
                    isOn = robotState.rightLedOn,
                    onClick = onRightLedToggle,
                    icon = Icons.Default.TurnRight
                )
            }

            // Sonido Section
            Text(
                text = "🔊 Sonido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onHornActivate,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (robotState.hornActive) Color.Red else MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Bocina")
            }

            OutlinedButton(
                onClick = onBeep,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Beep")
            }

            // Sensores Section
            Text(
                text = "📡 Sensores",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onReadUltrasonic,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Icon(Icons.Default.Radar, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Leer Distancia")
            }

            if (robotState.ultrasonicDistance != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Text(
                        text = "📏 ${String.format("%.1f", robotState.ultrasonicDistance)} cm",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun LedButton(
    modifier: Modifier = Modifier,
    text: String,
    isOn: Boolean,
    onClick: () -> Unit,
    icon: ImageVector
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isOn) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            contentColor = if (isOn) Color.Black else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(4.dp))
        Text(text)
    }
}

@Composable
fun CenterControlPanel(
    modifier: Modifier = Modifier,
    connectionState: ConnectionState,
    speed: Int,
    onWifiConnect: () -> Unit,
    onBluetoothConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onSpeedChange: (Int) -> Unit,
    onForward: () -> Unit,
    onBackward: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onStop: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con estado de conexión
            ConnectionStatusHeader(connectionState, onWifiConnect, onBluetoothConnect, onDisconnect)

            // Control de velocidad
            SpeedControl(speed, onSpeedChange)

            // Controles direccionales
            DirectionalControls(
                enabled = connectionState.status == ConnectionStatus.CONNECTED,
                onForward = onForward,
                onBackward = onBackward,
                onLeft = onLeft,
                onRight = onRight,
                onStop = onStop
            )
        }
    }
}

@Composable
fun ConnectionStatusHeader(
    connectionState: ConnectionState,
    onWifiConnect: () -> Unit,
    onBluetoothConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "🤖 CarroBot Controller",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Estado de conexión
        val (statusText, statusColor) = when (connectionState.status) {
            ConnectionStatus.CONNECTED -> "✅ Conectado" to Color(0xFF4CAF50)
            ConnectionStatus.CONNECTING -> "🔄 Conectando..." to Color(0xFFFF9800)
            ConnectionStatus.DISCONNECTED -> "❌ Desconectado" to Color(0xFFF44336)
            ConnectionStatus.ERROR -> "⚠️ Error" to Color(0xFFF44336)
        }

        Text(
            text = statusText,
            color = statusColor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        connectionState.device?.let { device ->
            Text(
                text = "${device.name} (${device.address})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Botones de conexión
        if (connectionState.status != ConnectionStatus.CONNECTED) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onWifiConnect,
                    enabled = connectionState.status != ConnectionStatus.CONNECTING
                ) {
                    Icon(Icons.Default.Wifi, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("WiFi")
                }

                Button(
                    onClick = onBluetoothConnect,
                    enabled = connectionState.status != ConnectionStatus.CONNECTING
                ) {
                    Icon(Icons.Default.Bluetooth, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Bluetooth")
                }
            }
        } else {
            OutlinedButton(onClick = onDisconnect) {
                Icon(Icons.Default.Close, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Desconectar")
            }
        }
    }
}

@Composable
fun SpeedControl(
    speed: Int,
    onSpeedChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "⚡ Velocidad: $speed%",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Slider(
            value = speed.toFloat(),
            onValueChange = { onSpeedChange(it.toInt()) },
            valueRange = 0f..100f,
            steps = 9,
            modifier = Modifier.width(200.dp)
        )
    }
}

@Composable
fun DirectionalControls(
    enabled: Boolean,
    onForward: () -> Unit,
    onBackward: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onStop: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "🎮 Control Direccional",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Botón Adelante
        ControlButton(
            onClick = onForward,
            enabled = enabled,
            icon = Icons.Default.KeyboardArrowUp,
            text = "↑",
            size = 80.dp
        )

        // Fila central: Izquierda, Stop, Derecha
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlButton(
                onClick = onLeft,
                enabled = enabled,
                icon = Icons.Default.KeyboardArrowLeft,
                text = "←",
                size = 80.dp
            )

            ControlButton(
                onClick = onStop,
                enabled = enabled,
                icon = Icons.Default.Stop,
                text = "⏹",
                size = 80.dp,
                color = MaterialTheme.colorScheme.error
            )

            ControlButton(
                onClick = onRight,
                enabled = enabled,
                icon = Icons.Default.KeyboardArrowRight,
                text = "→",
                size = 80.dp
            )
        }

        // Botón Atrás
        ControlButton(
            onClick = onBackward,
            enabled = enabled,
            icon = Icons.Default.KeyboardArrowDown,
            text = "↓",
            size = 80.dp
        )
    }
}

@Composable
fun ControlButton(
    onClick: () -> Unit,
    enabled: Boolean,
    icon: ImageVector,
    text: String,
    size: androidx.compose.ui.unit.Dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(size),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RightInfoPanel(
    modifier: Modifier = Modifier,
    robotState: RobotState,
    connectionState: ConnectionState
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📊 Estado del Robot",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Estado de movimiento
            InfoCard(
                title = "Movimiento",
                value = if (robotState.isMoving) "🏃 En movimiento" else "⏸️ Detenido",
                subtitle = "Velocidad: ${robotState.currentSpeed}%"
            )

            // Estado de LEDs
            InfoCard(
                title = "LEDs Activos",
                value = buildString {
                    val activeLeds = mutableListOf<String>()
                    if (robotState.frontLedOn) activeLeds.add("Frontal")
                    if (robotState.backLedOn) activeLeds.add("Trasero")
                    if (robotState.leftLedOn) activeLeds.add("Izq")
                    if (robotState.rightLedOn) activeLeds.add("Der")

                    append(if (activeLeds.isEmpty()) "🔴 Ninguno" else "💡 ${activeLeds.joinToString(", ")}")
                }
            )

            // Sensor ultrasónico
            if (robotState.ultrasonicDistance != null) {
                InfoCard(
                    title = "Distancia",
                    value = "📏 ${String.format("%.1f", robotState.ultrasonicDistance)} cm",
                    subtitle = when {
                        robotState.ultrasonicDistance < 10 -> "⚠️ Muy cerca"
                        robotState.ultrasonicDistance < 30 -> "🔶 Cerca"
                        else -> "✅ Distancia segura"
                    }
                )
            }

            // Información de conexión
            if (connectionState.device != null) {
                InfoCard(
                    title = "Conexión",
                    value = connectionState.device.name,
                    subtitle = "${connectionState.device.type.name}: ${connectionState.device.address}"
                )
            }

            Spacer(Modifier.weight(1f))

            // Logo o información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🤖",
                        fontSize = 48.sp
                    )
                    Text(
                        text = "CarroBot v1.0",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Control IoT",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    subtitle: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
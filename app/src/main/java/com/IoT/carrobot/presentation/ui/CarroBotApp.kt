package com.IoT.carrobot.presentation.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.IoT.carrobot.domain.model.ConnectionState
import com.IoT.carrobot.domain.model.ConnectionStatus
import com.IoT.carrobot.domain.model.RobotState
import com.IoT.carrobot.presentation.viewmodel.MainViewModel

@Composable
fun CarroBotApp(viewModel: MainViewModel) {
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val robotState by viewModel.robotState.collectAsStateWithLifecycle()
    val speed by viewModel.speed.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2C3E50),
                        Color(0xFF34495E),
                        Color(0xFF3F4E62),
                        Color(0xFF2C3E50)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            LeftMovementPanel(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                enabled = connectionState.status == ConnectionStatus.CONNECTED,
                speed = speed,
                onSpeedChange = viewModel::setSpeed,
                onForward = viewModel::moveForward,
                onBackward = viewModel::moveBackward,
                onLeft = viewModel::turnLeft,
                onRight = viewModel::turnRight,
                onStop = viewModel::stopMovement
            )

            CenterCameraPanel(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
                connectionState = connectionState,
                robotState = robotState,
                onWifiConnect = { viewModel.connectWifi("192.168.1.100") },
                onBluetoothConnect = viewModel::connectBluetooth,
                onDisconnect = viewModel::disconnect,
                onReadUltrasonic = viewModel::readUltrasonic
            )
            RightControlPanel(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                robotState = robotState,
                enabled = connectionState.status == ConnectionStatus.CONNECTED,
                onFrontLedToggle = viewModel::toggleFrontLed,
                onBackLedToggle = viewModel::toggleBackLed,
                onObstacleAvoidanceToggle = viewModel::toggleObstacleAvoidance,
                onLineFollowingToggle = viewModel::toggleLineFollowing,
                onAutoLightsToggle = viewModel::toggleAutoLights,
                onCalibrateSensors = viewModel::calibrateSensors,
                onHornActivate = viewModel::activateHorn,
                onBeep = viewModel::beep
            )
        }
    }
}

@Composable
fun LeftMovementPanel(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    speed: Int,
    onSpeedChange: (Int) -> Unit,
    onForward: () -> Unit,
    onBackward: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onStop: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Text(
                text = "🎮 MOVIMIENTO",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E3A59)
            )

            SpeedControlSection(speed, onSpeedChange, enabled)

            MovementDPad(
                enabled = enabled,
                onForward = onForward,
                onBackward = onBackward,
                onLeft = onLeft,
                onRight = onRight,
                onStop = onStop
            )

            Spacer(modifier = Modifier.weight(1f))

            StatusIndicator(enabled)
        }
    }
}

@Composable
fun SpeedControlSection(
    speed: Int,
    onSpeedChange: (Int) -> Unit,
    enabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "⚡ VELOCIDAD",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E3A59)
        )

        Text(
            text = "$speed%",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF34495E)
        )

        Slider(
            value = speed.toFloat(),
            onValueChange = { if (enabled) onSpeedChange(it.toInt()) },
            valueRange = 0f..100f,
            steps = 9,
            enabled = enabled,
            modifier = Modifier.width(200.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF34495E),
                activeTrackColor = Color(0xFF34495E),
                inactiveTrackColor = Color(0xFFBDC3C7)
            )
        )
    }
}

@Composable
fun MovementDPad(
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

        MovementButton(
            onClick = onForward,
            enabled = enabled,
            icon = Icons.Default.KeyboardArrowUp,
            color = Color(0xFF27AE60)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            MovementButton(
                onClick = onLeft,
                enabled = enabled,
                icon = Icons.Default.KeyboardArrowLeft,
                color = Color(0xFF5D6D7E)
            )

            Button(
                onClick = onStop,
                enabled = enabled,
                modifier = Modifier.size(70.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE74C3C),
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                ),
                shape = CircleShape,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "STOP",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            MovementButton(
                onClick = onRight,
                enabled = enabled,
                icon = Icons.Default.KeyboardArrowRight,
                color = Color(0xFF5D6D7E)
            )
        }

        MovementButton(
            onClick = onBackward,
            enabled = enabled,
            icon = Icons.Default.KeyboardArrowDown,
            color = Color(0xFF27AE60)
        )
    }
}

@Composable
fun MovementButton(
    onClick: () -> Unit,
    enabled: Boolean,
    icon: ImageVector,
    color: Color
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
        ),
        shape = CircleShape,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = Color.White
        )
    }
}

@Composable
fun CenterCameraPanel(
    modifier: Modifier = Modifier,
    connectionState: ConnectionState,
    robotState: RobotState,
    onWifiConnect: () -> Unit,
    onBluetoothConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onReadUltrasonic: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ConnectionHeader(connectionState, onWifiConnect, onBluetoothConnect, onDisconnect)

            CameraViewPanel()

            RobotInfoSection(robotState, onReadUltrasonic, connectionState.status == ConnectionStatus.CONNECTED)
        }
    }
}

@Composable
fun ConnectionHeader(
    connectionState: ConnectionState,
    onWifiConnect: () -> Unit,
    onBluetoothConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🤖 CARROBOT",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E3A59)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = when (connectionState.status) {
                    ConnectionStatus.CONNECTED -> Color(0xFF4CAF50)
                    ConnectionStatus.CONNECTING -> Color(0xFFFF9800)
                    ConnectionStatus.DISCONNECTED -> Color(0xFFF44336)
                    ConnectionStatus.ERROR -> Color(0xFFF44336)
                }
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = when (connectionState.status) {
                    ConnectionStatus.CONNECTED -> "✅ CONECTADO"
                    ConnectionStatus.CONNECTING -> "🔄 CONECTANDO..."
                    ConnectionStatus.DISCONNECTED -> "❌ DESCONECTADO"
                    ConnectionStatus.ERROR -> "⚠️ ERROR"
                },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        if (connectionState.status != ConnectionStatus.CONNECTED) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onWifiConnect,
                    enabled = connectionState.status != ConnectionStatus.CONNECTING,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF34495E)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("WiFi", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onBluetoothConnect,
                    enabled = connectionState.status != ConnectionStatus.CONNECTING,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5D6D7E)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(Icons.Default.Bluetooth, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Bluetooth", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            OutlinedButton(
                onClick = onDisconnect,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFF44336))
            ) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFFF44336))
                Spacer(Modifier.width(8.dp))
                Text("Desconectar", color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CameraViewPanel() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = "📹 VISTA DE CÁMARA",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Transmisión en vivo aquí",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun RobotInfoSection(
    robotState: RobotState,
    onReadUltrasonic: () -> Unit,
    enabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "📊 INFORMACIÓN",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E3A59)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoChip(
                label = "Velocidad",
                value = "${robotState.currentSpeed}%",
                icon = Icons.Default.Speed,
                color = Color(0xFF27AE60)
            )

            Button(
                onClick = onReadUltrasonic,
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8E44AD)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Sensor", fontWeight = FontWeight.Bold)
            }
        }

        if (robotState.ultrasonicDistance != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "📏 Distancia: ${String.format("%.1f", robotState.ultrasonicDistance)} cm",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }
        }
    }
}

@Composable
fun InfoChip(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = color
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = color.copy(alpha = 0.7f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

@Composable
fun RightControlPanel(
    modifier: Modifier = Modifier,
    robotState: RobotState,
    enabled: Boolean,
    onFrontLedToggle: () -> Unit,
    onBackLedToggle: () -> Unit,
    onObstacleAvoidanceToggle: () -> Unit,
    onLineFollowingToggle: () -> Unit,
    onAutoLightsToggle: () -> Unit,
    onCalibrateSensors: () -> Unit,
    onHornActivate: () -> Unit,
    onBeep: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header compacto
            Text(
                text = "🤖 INTELIGENCIA",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E3A59)
            )

            // Automatización (compacta)
            CompactAutomationSection(
                robotState = robotState,
                enabled = enabled,
                onObstacleAvoidanceToggle = onObstacleAvoidanceToggle,
                onLineFollowingToggle = onLineFollowingToggle,
                onAutoLightsToggle = onAutoLightsToggle,
                onCalibrateSensors = onCalibrateSensors
            )

            // Control Manual de Luces (compacto)
            CompactManualLightsSection(
                robotState = robotState,
                enabled = enabled,
                onFrontLedToggle = onFrontLedToggle,
                onBackLedToggle = onBackLedToggle
            )

            // Controles de Sonido (compacto)
            CompactSoundControlsSection(
                robotState = robotState,
                enabled = enabled,
                onHornActivate = onHornActivate,
                onBeep = onBeep
            )

            // Spacer flexible
            Spacer(modifier = Modifier.weight(1f))

            // Estado del Sistema (compacto)
            CompactSystemStatusCard(robotState)
        }
    }
}

@Composable
fun CompactAutomationSection(
    robotState: RobotState,
    enabled: Boolean,
    onObstacleAvoidanceToggle: () -> Unit,
    onLineFollowingToggle: () -> Unit,
    onAutoLightsToggle: () -> Unit,
    onCalibrateSensors: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header compacto
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color(0xFF2E3A59)
            )
            Text(
                text = "AUTOMATIZACIÓN",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2E3A59),
                letterSpacing = 0.3.sp
            )
        }

        // Grid compacto
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(120.dp)
        ) {
            item {
                CompactAutomationButton(
                    icon = "🚧",
                    title = "EVITAR",
                    subtitle = "OBSTÁCULOS",
                    isActive = robotState.obstacleAvoidanceOn,
                    onClick = onObstacleAvoidanceToggle,
                    enabled = enabled,
                    color = Color(0xFFE67E22),
                    activeIcon = "⚡"
                )
            }

            item {
                CompactAutomationButton(
                    icon = "🛤️",
                    title = "SEGUIR",
                    subtitle = "LÍNEA",
                    isActive = robotState.lineFollowingOn,
                    onClick = onLineFollowingToggle,
                    enabled = enabled,
                    color = Color(0xFF2980B9),
                    activeIcon = "🎯"
                )
            }

            item {
                CompactAutomationButton(
                    icon = "💡",
                    title = "LUCES",
                    subtitle = "AUTO",
                    isActive = robotState.autoLightsOn,
                    onClick = onAutoLightsToggle,
                    enabled = enabled,
                    color = Color(0xFF8E44AD),
                    activeIcon = "✨"
                )
            }

            item {
                CompactCalibrateButton(
                    onClick = onCalibrateSensors,
                    enabled = enabled
                )
            }
        }

        // Indicador compacto
        if (robotState.obstacleAvoidanceOn || robotState.lineFollowingOn || robotState.autoLightsOn) {
            CompactActiveModeIndicator(robotState)
        }
    }
}

@Composable
fun CompactAutomationButton(
    icon: String,
    title: String,
    subtitle: String,
    isActive: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    color: Color,
    activeIcon: String
) {
    val animatedElevation by animateDpAsState(
        targetValue = if (isActive) 8.dp else 3.dp,
        animationSpec = tween(200)
    )

    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive && enabled) {
                color.copy(alpha = 0.9f)
            } else {
                Color(0xFFF8F9FA)
            },
            disabledContainerColor = Color.Gray.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation),
        border = if (isActive) {
            BorderStroke(2.dp, color)
        } else {
            BorderStroke(1.dp, Color(0xFFE1E8ED))
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 6.dp)
            ) {
                Text(
                    text = if (isActive) activeIcon else icon,
                    fontSize = 14.sp
                )

                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = title,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isActive) Color.White else Color(0xFF2E3A59),
                        letterSpacing = 0.2.sp
                    )
                    Text(
                        text = subtitle,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isActive) {
                            Color.White.copy(alpha = 0.9f)
                        } else {
                            Color(0xFF6C757D)
                        }
                    )
                }
            }

            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color.White, shape = CircleShape)
                        .align(Alignment.TopEnd)
                        .offset((-6).dp, 6.dp)
                )
            }
        }
    }
}

@Composable
fun CompactCalibrateButton(
    onClick: () -> Unit,
    enabled: Boolean
) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF16A085), Color(0xFF1ABC9C))
    )

    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )

                Column {
                    Text(
                        text = "CALIBRAR",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "SENSORES",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun CompactActiveModeIndicator(robotState: RobotState) {
    val activeModes = buildList {
        if (robotState.obstacleAvoidanceOn) add("🚧")
        if (robotState.lineFollowingOn) add("🛤️")
        if (robotState.autoLightsOn) add("💡")
    }

    if (activeModes.isNotEmpty()) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4CAF50).copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(Color(0xFF4CAF50), shape = CircleShape)
                )

                Text(
                    text = "Activos: ${activeModes.joinToString(" ")}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E7D32),
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun CompactManualLightsSection(
    robotState: RobotState,
    enabled: Boolean,
    onFrontLedToggle: () -> Unit,
    onBackLedToggle: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header compacto
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF2E3A59)
            )
            Text(
                text = "CONTROL MANUAL",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2E3A59),
                letterSpacing = 0.3.sp
            )
        }

        // Controles compactos
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CompactLedButton(
                modifier = Modifier.weight(1f),
                icon = "🔆",
                label = "FRONTALES",
                isOn = robotState.frontLedOn,
                onClick = onFrontLedToggle,
                enabled = enabled && !robotState.autoLightsOn,
                color = Color(0xFF2980B9)
            )

            CompactLedButton(
                modifier = Modifier.weight(1f),
                icon = "🔴",
                label = "TRASERAS",
                isOn = robotState.backLedOn,
                onClick = onBackLedToggle,
                enabled = enabled && !robotState.autoLightsOn,
                color = Color(0xFFE74C3C)
            )
        }

        // Aviso compacto
        if (robotState.autoLightsOn) {
            Text(
                text = "⚠️ Modo automático activo",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8E44AD),
                fontWeight = FontWeight.Medium,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun CompactLedButton(
    modifier: Modifier = Modifier,
    icon: String,
    label: String,
    isOn: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    color: Color
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(40.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOn && enabled) {
                color.copy(alpha = 0.9f)
            } else {
                Color(0xFFF8F9FA)
            },
            disabledContainerColor = Color.Gray.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isOn) 6.dp else 2.dp
        ),
        border = if (isOn && enabled) {
            BorderStroke(1.5.dp, color)
        } else {
            BorderStroke(1.dp, Color(0xFFE1E8ED))
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = icon, fontSize = 12.sp)
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = if (isOn && enabled) Color.White else Color(0xFF2E3A59)
                )
            }
        }
    }
}

@Composable
fun CompactSoundControlsSection(
    robotState: RobotState,
    enabled: Boolean,
    onHornActivate: () -> Unit,
    onBeep: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header compacto
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = "🔊", fontSize = 14.sp)
            Text(
                text = "SONIDO",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E3A59)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Botón de Bocina compacto
            Card(
                onClick = onHornActivate,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (robotState.hornActive) Color(0xFFE74C3C) else Color(0xFF8E44AD)
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (robotState.hornActive) 8.dp else 4.dp
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📢", fontSize = 14.sp)
                        Text(
                            text = "BOCINA",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Botón de Beep compacto
            Card(
                onClick = onBeep,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF16A085)
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🔔", fontSize = 14.sp)
                        Text(
                            text = "BEEP",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompactSystemStatusCard(robotState: RobotState) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3E5F5)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "📊 ESTADO DEL SISTEMA",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7B1FA2)
            )

            val activeFeatures = buildList {
                if (robotState.obstacleAvoidanceOn) add("Evitar obstáculos")
                if (robotState.lineFollowingOn) add("Seguir línea")
                if (robotState.autoLightsOn) add("Luces auto")
                if (robotState.frontLedOn) add("LED frontales")
                if (robotState.backLedOn) add("LED traseras")
            }

            Text(
                text = if (activeFeatures.isEmpty())
                    "🟡 Modo manual"
                else
                    "🟢 ${activeFeatures.size} funciones activas",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF7B1FA2),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun StatusIndicator(enabled: Boolean) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color(0xFF27AE60) else Color(0xFFE74C3C)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Text(
            text = if (enabled) "🟢 ROBOT LISTO" else "🔴 DESCONECTADO",
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

// Enum para los modos de conducción (por si quieres implementarlo más adelante)
enum class DrivingMode {
    ECO, SPORT, AUTONOMOUS
}
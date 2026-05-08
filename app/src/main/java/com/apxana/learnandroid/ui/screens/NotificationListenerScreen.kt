package com.apxana.learnandroid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apxana.learnandroid.viewmodel.NotificationListenerViewModel
import com.apxana.learnandroid.viewmodel.NotificationServiceState

/**
 * Pantalla de configuración del NotificationListener
 * Permite al usuario habilitar el servicio de notificaciones
 */
@Composable
fun NotificationListenerScreen(
    viewModel: NotificationListenerViewModel = viewModel()
) {
    val serviceState by viewModel.serviceState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val testResult by viewModel.testResult.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.checkServiceState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Notificaciones",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Configura el servicio para recibir notificaciones de tus apps bancarias",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Estado del servicio
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when (serviceState) {
                    is NotificationServiceState.Enabled -> 
                        MaterialTheme.colorScheme.primaryContainer
                    is NotificationServiceState.Disabled -> 
                        MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = when (serviceState) {
                        is NotificationServiceState.Enabled -> Icons.Default.Notifications
                        is NotificationServiceState.Disabled -> Icons.Default.Warning
                        else -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = when (serviceState) {
                        is NotificationServiceState.Enabled -> 
                            MaterialTheme.colorScheme.primary
                        is NotificationServiceState.Disabled -> 
                            MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (serviceState) {
                            is NotificationServiceState.Enabled -> "Servicio Activo"
                            is NotificationServiceState.Disabled -> "Servicio Deshabilitado"
                            is NotificationServiceState.NotConfigured -> "Sin Configurar"
                            is NotificationServiceState.Error -> "Error"
                            else -> "Verificando..."
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = when (serviceState) {
                            is NotificationServiceState.Enabled -> 
                                "Estás recibiendo notificaciones"
                            is NotificationServiceState.Disabled -> 
                                "Necesitas habilitar el acceso"
                            is NotificationServiceState.NotConfigured -> 
                                "Configura el acceso a notificaciones"
                            is NotificationServiceState.Error -> 
                                (serviceState as NotificationServiceState.Error).message
                            else -> ""
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        // Apps objetivo
        Text(
            text = "Apps Monitoreadas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        viewModel.targetPackages.forEach { (packageName, appName) ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = appName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Instrucciones
        if (serviceState !is NotificationServiceState.Enabled) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Cómo habilitar el servicio:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "1. Toca el botón 'Habilitar Acceso' abajo",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "2. Busca 'LearnAndroid' en la lista",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "3. Actívalo marcando la casilla",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "4. Regresa a la app",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.openNotificationAccessSettings() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Habilitar Acceso a Notificaciones")
            }
        } else {
            // SECCIÓN DE PRUEBA -Solo visible cuando el servicio está habilitado
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Modo Prueba",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Text(
                        text = "Usa estos botones para probar el envío de notificaciones al webhook sin necesidad de esperar una notificación real.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    // Botones de prueba
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.quickTest() },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Prueba Rápida")
                        }
                        
                        OutlinedButton(
                            onClick = { viewModel.checkServiceState() },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verificar")
                        }
                    }
                    
                    // Botones de prueba específicos por app
                    Text(
                        text = "Probar notificación específica:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { 
                                viewModel.sendTestNotification(
                                    "com.nequi.nequi",
                                    "Transferencia recibida",
                                    "Recibiste \$50.000 COP de Juan Pérez"
                                )
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Text("Nequi", style = MaterialTheme.typography.labelSmall)
                        }
                        
                        OutlinedButton(
                            onClick = { 
                                viewModel.sendTestNotification(
                                    "com.nuapp.br",
                                    "Transferência recebida",
                                    "Você recebeu R\$ 150,00 de Maria Silva"
                                )
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Text("Nubank", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            
            // Resultado de la prueba
            if (testResult != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (testResult?.startsWith("✅") == true) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (testResult?.startsWith("✅") == true) 
                                Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (testResult?.startsWith("✅") == true) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = testResult ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { viewModel.clearTestResult() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Info del webhook
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Información",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Las notificaciones se enviarán a tu email automáticamente cuando recibas dinero o envies dinero en tus cuentas de Nequi o Nubank.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
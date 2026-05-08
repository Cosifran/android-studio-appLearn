package com.apxana.learnandroid.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.apxana.learnandroid.data.model.NotificationData
import com.apxana.learnandroid.data.repository.WebhookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado del servicio de notificaciones
 */
sealed class NotificationServiceState {
    object NotConfigured : NotificationServiceState()
    object Enabled : NotificationServiceState()
    object Disabled : NotificationServiceState()
    data class Error(val message: String) : NotificationServiceState()
}

/**
 * Información de notificación para debug
 */
data class NotificationInfo(
    val packageName: String,
    val title: String,
    val text: String,
    val timestamp: Long,
    val appName: String
)

/**
 * ViewModel para la pantalla de configuración del NotificationListener
 */
class NotificationListenerViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val tag = "NotificationListenerVM"
    
    private val _serviceState = MutableStateFlow<NotificationServiceState>(
        NotificationServiceState.NotConfigured
    )
    val serviceState: StateFlow<NotificationServiceState> = _serviceState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Lista de notificaciones activas para debug
    private val _activeNotifications = MutableStateFlow<List<NotificationInfo>>(emptyList())
    val activeNotifications: StateFlow<List<NotificationInfo>> = _activeNotifications.asStateFlow()

    // Estado del test de webhook
    private val _testResult = MutableStateFlow<String?>(null)
    val testResult: StateFlow<String?> = _testResult.asStateFlow()

    // Paquetes objetivo
    val targetPackages = listOf(
        "com.nequi.nequi" to "Nequi",
        "com.nu.production" to "Nubank"
    )

    // Referencia al repositorio
    private val webhookRepository: WebhookRepository?
        get() = (getApplication<Application>() as? com.apxana.learnandroid.LearnAndroidApplication)?.webhookRepository

    init {
        checkServiceState()
    }

    fun checkServiceState() {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val componentName = ComponentName(
                    context,
                    "com.apxana.learnandroid.service.NotificationInterceptor"
                )

                val enabledListeners = Settings.Secure.getString(
                    context.contentResolver,
                    "enabled_notification_listeners"
                )

                _isLoading.value = true

                if (enabledListeners != null) {
                    val isEnabled = enabledListeners.contains(componentName.flattenToString())
                    _serviceState.value = if (isEnabled) {
                        NotificationServiceState.Enabled
                    } else {
                        NotificationServiceState.Disabled
                    }
                    Log.d(tag, "Enabled listeners: $enabledListeners")
                    Log.d(tag, "Component: ${componentName.flattenToString()}")
                } else {
                    _serviceState.value = NotificationServiceState.Disabled
                }
                
                Log.d(tag, "Estado del servicio: ${_serviceState.value}")
            } catch (e: Exception) {
                Log.e(tag, "Error al verificar estado: ${e.message}")
                _serviceState.value = NotificationServiceState.Error(e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Obtiene las notificaciones activas del sistema a través del NotificationListenerService
     * Esto requiere que el servicio ya esté vinculado
     */
    fun fetchActiveNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            _testResult.value = "Buscando notificaciones..."
            
            try {
                // Intent de probar las notificaciones
                val context = getApplication<Application>()
                
                // Enviar un broadcast para que el servicio imprima las notificaciones activas
                val intent = Intent("com.apxana.learnandroid.DEBUG_NOTIFICATIONS")
                context.sendBroadcast(intent)
                
                _testResult.value = "Revisa los logs en Logcat con el tag: NotificationInterceptor"
            } catch (e: Exception) {
                Log.e(tag, "Error al buscar notificaciones: ${e.message}")
                _testResult.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Envía una notificación de prueba directamente al webhook
     * Sin esperar a que llegue una notificación real
     */
    fun sendTestNotification(packageName: String, title: String, text: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _testResult.value = "Enviando prueba..."
            
            try {
                val appName = when (packageName) {
                    "com.nequi.nequi" -> "Nequi"
                    "com.nu.production" -> "Nubank"
                    else -> packageName.substringAfterLast(".")
                }
                
                val notificationData = NotificationData(
                    packageName = packageName,
                    title = title,
                    text = text,
                    timestamp = System.currentTimeMillis(),
                    appName = appName
                )
                
                Log.d(tag, "Enviando notificación de prueba: $appName - $title")
                
                val result = webhookRepository?.sendNotification(notificationData)
                
                if (result?.isSuccess == true) {
                    _testResult.value = "✅ Éxito! Notificación enviada al webhook"
                    Log.d(tag, "Test enviado exitosamente")
                } else {
                    val errorMsg = result?.exceptionOrNull()?.message ?: "Error desconocido"
                    _testResult.value = "❌ Error: $errorMsg"
                    Log.e(tag, "Error al enviar test: $errorMsg")
                }
            } catch (e: Exception) {
                Log.e(tag, "Excepción: ${e.message}")
                _testResult.value = "❌ Excepción: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Prueba rápida con datos hardcoded
     */
    fun quickTest() {
        sendTestNotification("com.nu.production", "Test desde Money Wallet", "Esta es una prueba de funcionamiento")
    }

    fun clearTestResult() {
        _testResult.value = null
    }

    fun isNotificationAccessGranted(): Boolean {
        return try {
            val context = getApplication<Application>()
            val componentName = ComponentName(
                context,
                "com.apxana.learnandroid.service.NotificationInterceptor"
            )

            val enabledListeners = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            )

            enabledListeners?.contains(componentName.flattenToString()) == true
        } catch (e: Exception) {
            Log.e(tag, "Error al verificar permisos: ${e.message}")
            false
        }
    }

    fun openNotificationAccessSettings() {
        try {
            val context = getApplication<Application>()
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(tag, "Error al abrir configuración: ${e.message}")
        }
    }
}
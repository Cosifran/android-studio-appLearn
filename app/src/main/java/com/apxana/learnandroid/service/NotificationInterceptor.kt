package com.apxana.learnandroid.service

import android.app.Application
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.apxana.learnandroid.data.model.NotificationData
import com.apxana.learnandroid.data.repository.WebhookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Servicio que intercepta notificaciones del sistema
 * Extiende NotificationListenerService para recibir todas las notificaciones
 */
class NotificationInterceptor : NotificationListenerService() {

    // Referencia al repositorio - se obtiene de la Application
    private val webhookRepository: WebhookRepository?
        get() = (application as? com.apxana.learnandroid.LearnAndroidApplication)?.webhookRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val tag = "NotificationInterceptor"

    // Paquetes objetivo - solo procesamos notificaciones de estas apps
    private val targetPackages = setOf(
        "com.nequi.nequi",
        "com.nu.production"
    )

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "=== NotificationInterceptor CREADO ===")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        Log.d(tag, "=== onNotificationPosted RECIBIDO ===")
        Log.d(tag, "Package: ${sbn?.packageName}")
        Log.d(tag, "Tag: ${sbn?.tag}")
        Log.d(tag, "Key: ${sbn?.key}")
        
        sbn?.let { notification ->
            Log.d(tag, "Notificación recibida de: ${notification.packageName}")
            
            // Primero procesar TODAS las notificaciones para debug
            val extras = notification.notification.extras
            val title = extras.getCharSequence("android.title")?.toString() ?: "(sin título)"
            val text = extras.getCharSequence("android.text")?.toString() ?: "(sin texto)"
            
            Log.d(tag, "   Title: $title")
            Log.d(tag, "   Text: $text")
            
            // Verificar si es una notificación de las apps objetivo
            if (notification.packageName in targetPackages) {
                Log.d(tag, "==> ES DE APP OBJETIVO - PROCESANDO")
                processNotification(notification)
            } else {
                Log.d(tag, "==> NO es de app objetivo - IGNORANDO")
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn?.let {
            Log.d(tag, "Notificación eliminada de: ${it.packageName}")
            if (it.packageName in targetPackages) {
                Log.d(tag, "Notificación de app objetivo eliminada")
            }
        }
    }

    private fun processNotification(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        
        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val timestamp = sbn.postTime

        val appName = getAppDisplayName(sbn.packageName)

        val notificationData = NotificationData(
            packageName = sbn.packageName,
            title = title,
            text = text,
            timestamp = timestamp,
            appName = appName
        )

        Log.d(tag, "=== PROCESANDO NOTIFICACIÓN ===")
        Log.d(tag, "App: $appName")
        Log.d(tag, "Title: $title")
        Log.d(tag, "Text: $text")

        // Enviar al webhook de forma asíncrona
        serviceScope.launch {
            try {
                Log.d(tag, "Enviando al webhook...")
                val result = webhookRepository?.sendNotification(notificationData)
                if (result?.isSuccess == true) {
                    Log.d(tag, "=== NOTIFICACIÓN ENVIADA EXITOSAMENTE ===")
                } else {
                    val error = result?.exceptionOrNull()?.message ?: "error desconocido"
                    Log.e(tag, "=== ERROR AL ENVIAR: $error ===")
                }
            } catch (e: Exception) {
                Log.e(tag, "=== EXCEPCIÓN: ${e.message} ===")
            }
        }
    }

    private fun getAppDisplayName(packageName: String): String {
        return when (packageName) {
            "com.nequi.nequi" -> "Nequi"
            "com.nu.production" -> "Nubank"
            else -> packageName.substringAfterLast(".")
        }
    }
}
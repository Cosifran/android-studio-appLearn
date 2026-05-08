package com.apxana.learnandroid.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.apxana.learnandroid.firstapp.FirstAppActivity

/**
 * Foreground Service que mantiene el NotificationInterceptor activo
 * y muestra una notificación persistente al usuario
 * 
 * Este servicio se inicia cuando la app se abre y mantiene vivo el proceso
 */
class NotificationForwarderService : Service() {

    private val tag = "NotificationForwarder"
    private val channelId = "notification_listener_channel"
    private val notificationId = 1001

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(tag, "=== SERVICIO CREADO ===")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(tag, "=== onStartCommand RECIBIDO ===")
        
        // Iniciar como foreground service con notificación persistente
        startForeground(notificationId, createNotification())
        
        Log.d(tag, "=== SERVICIO INICIADO EN FOREGROUND ===")
        
        // START_STICKY indica que el servicio debe reiniciarse si se mata
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "=== SERVICIO DESTRUIDO ===")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(tag, "=== onTaskRemoved - La app fue cerrada ===")
        
        // Intentar reiniciar el servicio cuando la app se cierra
        // Esto ayuda a mantener el NotificationListener activo
        val restartIntent = Intent(this, NotificationForwarderService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(restartIntent)
        } else {
            startService(restartIntent)
        }
        
        Log.d(tag, "=== SERVICIO REINICIADO ===")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Money Wallet Listener",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Mantiene el servicio de notificaciones activo"
                setShowBadge(false)
                // Configurar para que no se pueda desactivar fácilmente
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            Log.d(tag, "=== CANAL DE NOTIFICACIÓN CREADO ===")
        }
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, FirstAppActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Notificación más visible para que el usuario sepa que está activo
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("💰 Money Wallet")
            .setContentText("Escuchando transacciones de Nequi y Nubank...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }
}
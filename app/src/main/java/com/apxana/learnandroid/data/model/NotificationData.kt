package com.apxana.learnandroid.data.model

/**
 * Data class que representa una notificación interceptada
 * 
 * @property packageName Nombre del paquete de la app que generó la notificación
 * @property title Título de la notificación
 * @property text Cuerpo/texto de la notificación
 * @property timestamp Tiempo Unix en milisegundos cuando llegó la notificación
 * @property appName Nombre legible de la app (ej: "Nequi", "Nubank")
 */
data class NotificationData(
    val packageName: String,
    val title: String,
    val text: String,
    val timestamp: Long,
    val appName: String
)
package com.apxana.learnandroid.data.repository

import com.apxana.learnandroid.data.model.NotificationData
import com.apxana.learnandroid.data.model.webhook.Recipient
import com.apxana.learnandroid.data.model.webhook.WebhookPayload
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

/**
 * Interfaz para el repositorio de webhook
 */
interface WebhookRepository {
    suspend fun sendNotification(notification: NotificationData): Result<Boolean>
    suspend fun checkConnection(): Result<Boolean>
}

/**
 * Implementación del WebhookRepository que envía notificaciones al endpoint de n8n
 */
class WebhookRepositoryImpl(
    private val webhookApi: WebhookApi
) : WebhookRepository {

    private val recipientEmail = "fransiscoestrada706@gmail.com"

    override suspend fun sendNotification(notification: NotificationData): Result<Boolean> {
        return try {
            val appName = getAppDisplayName(notification.packageName)
            
            val payload = WebhookPayload(
                to = listOf(Recipient(email = recipientEmail)),
                subject = "Notificacion de $appName: ${notification.title}",
                html = buildHtmlBody(notification.title, notification.text)
            )

            val response = webhookApi.sendWebhook(payload)

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Webhook response: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkConnection(): Result<Boolean> {
        return try {
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getAppDisplayName(packageName: String): String {
        return when (packageName) {
            "com.nequi.nequi" -> "Nequi"
            "com.nuapp.br" -> "Nubank"
            else -> packageName.substringAfterLast(".")
        }
    }

    private fun buildHtmlBody(title: String, text: String): String {
        return """
            <h1>Asunto: $title</h1>
            <p>Mensaje: $text</p>
            <br><hr>
            <p>Enviado automáticamente por Money Wallet.</p>
        """.trimIndent()
    }
}

/**
 * Interfaz de Retrofit para el API del webhook
 */
interface WebhookApi {
    @POST("webhook/88d8b9df-a83e-4430-bef3-0df165d724f8")
    suspend fun sendWebhook(@Body payload: WebhookPayload): Response<Unit>
    
    companion object {
        private const val WEBHOOK_BASE_URL = "https://n8n.franciscodev.qzz.io/"
        
        fun create(): WebhookApi {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
            
            val retrofit = Retrofit.Builder()
                .baseUrl(WEBHOOK_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            
            return retrofit.create(WebhookApi::class.java)
        }
    }
}
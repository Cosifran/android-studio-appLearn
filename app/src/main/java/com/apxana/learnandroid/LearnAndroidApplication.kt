package com.apxana.learnandroid

import android.app.Application

/**
 * Clase Application simple - sin Hilt para evitar problemas de build
 */
class LearnAndroidApplication : Application() {
    
    // Singleton para inyección manual
    lateinit var webhookRepository: com.apxana.learnandroid.data.repository.WebhookRepository
        private set
    
    override fun onCreate() {
        super.onCreate()
        // Inicialización manual de dependencias
        webhookRepository = com.apxana.learnandroid.data.repository.WebhookRepositoryImpl(
            com.apxana.learnandroid.data.repository.WebhookApi.create()
        )
    }
}
package com.apxana.learnandroid.firstapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.apxana.learnandroid.service.NotificationForwarderService
import com.apxana.learnandroid.ui.screens.NotificationListenerScreen

class FirstAppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Iniciar el Foreground Service para mantener la app funcionando
        startNotificationService()
        
        // Usar Compose para la UI
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                NotificationListenerScreen()
            }
        }
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    
    private fun startNotificationService() {
        try {
            val intent = Intent(this, NotificationForwarderService::class.java)
            startForegroundService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
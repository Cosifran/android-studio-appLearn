package com.apxana.learnandroid.data.model.webhook

import com.google.gson.annotations.SerializedName

/**
 * Payload que se envía al webhook de n8n
 * 
 * @property to Lista de destinatarios con sus emails
 * @property subject Asunto del email
 * @property html Cuerpo del email en formato HTML
 */
data class WebhookPayload(
    @SerializedName("to")
    val to: List<Recipient>,
    
    @SerializedName("subject")
    val subject: String,
    
    @SerializedName("html")
    val html: String
)

/**
 * Representa un destinatario del email
 * 
 * @property email Email del destinatario
 */
data class Recipient(
    @SerializedName("email")
    val email: String
)
package com.codingraz.bootcamp.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ConversationModel(
    val userId: String,
    val userName: String,
    val userImage: String,
    val message: String,
    val imageMessage: String,
    @ServerTimestamp val timestamp: Date? = null,
    val temeStampLocal: String? =null
)

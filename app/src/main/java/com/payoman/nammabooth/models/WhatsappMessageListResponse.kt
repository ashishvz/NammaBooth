package com.payoman.nammabooth.models

import java.util.Date

data class WhatsappMessageListResponse(
    val data: MutableList<WhatsappMessage>,
    val status: String?
)

data class WhatsappMessage(
    val id: Int,
    val message_type: String,
    val message_content: String,
    val message_options: List<String>,
    val created_at: Date,
    val updated_at: Date,
    val default_reply_message: String
)
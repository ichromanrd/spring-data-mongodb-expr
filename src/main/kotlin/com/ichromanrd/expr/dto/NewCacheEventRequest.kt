package com.ichromanrd.expr.dto

import java.util.Date

data class NewCacheEventRequest(
    val accountKey: String,
    val eventDate: Date,
    val balance: Long,
    val lastTransactionId: String? = null,
)

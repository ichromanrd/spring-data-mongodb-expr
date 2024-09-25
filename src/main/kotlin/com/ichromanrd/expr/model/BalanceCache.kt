package com.ichromanrd.expr.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant

@Document("balance_cache")
data class BalanceCache(
    @Id
    val id: String,
    val eventDate: Instant? = null,
    val lastTransactionId: String? = null,
    val balance: Long? = 0,
    val updatedAt: Instant = Instant.now(),
    val createdAt: Instant = Instant.now(),
)

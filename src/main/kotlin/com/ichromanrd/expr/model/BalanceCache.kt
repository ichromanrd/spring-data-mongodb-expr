package com.ichromanrd.expr.model

import java.math.BigDecimal
import java.time.Instant

@Document("balance_cache")
data class BalanceCache(
    val id: String,
    val evenDate: Instant? = null,
    val lastTransactionId: String? = null,
    val balance: BigDecimal? = BigDecimal.ZERO,
    val updatedAt: Instant = Instant.now(),
    val createdAt: Instant = Instant.now(),
)
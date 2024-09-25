package com.ichromanrd.expr.dto

import java.math.BigDecimal
import java.util.Date

data class BalanceCacheModifyRequest(
    val accountKey: String,
    val balance: BigDecimal,
    val eventDate: Date,
    val lastTransactionId: String? = null,
)
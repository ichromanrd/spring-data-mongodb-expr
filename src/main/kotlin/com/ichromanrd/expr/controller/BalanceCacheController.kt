package com.ichromanrd.expr.controller

import com.ichromanrd.expr.dto.BalanceCacheModifyRequest
import com.ichromanrd.expr.dto.NewCacheEventRequest
import com.ichromanrd.expr.model.BalanceCache
import com.ichromanrd.expr.repository.BalanceCacheRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/balances")
class BalanceCacheController(
    private val repository: BalanceCacheRepository,
) {
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping
    suspend fun addEventBalanceUpdate(@RequestBody request: BalanceCacheModifyRequest) {
        repository.updateBalanceIfNecessary(request)
    }

    @PostMapping
    suspend fun createBalanceCache(@RequestBody request: NewCacheEventRequest): BalanceCache {
        return repository.insertCache(request)
    }
}
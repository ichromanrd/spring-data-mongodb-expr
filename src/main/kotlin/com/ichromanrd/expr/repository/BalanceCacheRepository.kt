package com.ichromanrd.expr.repository

import com.ichromanrd.expr.dto.BalanceCacheModifyRequest
import com.ichromanrd.expr.dto.NewCacheEventRequest
import com.ichromanrd.expr.model.BalanceCache
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.AggregationExpression
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate
import org.springframework.data.mongodb.core.aggregation.BooleanOperators.And
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Lt
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Lte
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators
import org.springframework.data.mongodb.core.aggregation.ConvertOperators.ToLong
import org.springframework.data.mongodb.core.aggregation.SetOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
class BalanceCacheRepository(
    private val mongoTemplate: ReactiveMongoTemplate,
) {

    companion object {
        private val upsertAndReturnNew = FindAndModifyOptions.options().upsert(true).returnNew(true)
    }

    private fun composeCondition(condition: AggregationExpression, fieldName: String, valueToSet: Any): ConditionalOperators.Cond {
        return ConditionalOperators
            .`when`(condition)
            .then(valueToSet)
            .otherwiseValueOf(fieldName)
    }

    private fun modifySet(condition: AggregationExpression, setOperation: SetOperation, fieldName: String, valueToSet: Any): SetOperation {
        return setOperation.set(
            fieldName,
            composeCondition(condition, fieldName, valueToSet)
        )
    }

    private fun getUpdateConditions(eventDate: Date, lastTransactionId: String? = null): AggregationExpression {
        val criteriaList: MutableList<AggregationExpression> = mutableListOf(
            Lte.valueOf(BalanceCache::eventDate.name).lessThanEqualToValue(eventDate),
        )

        lastTransactionId?.toLongOrNull()?.let { lastTrxIdNumerical ->
            val expr = Lt.valueOf(
                ToLong.toLong("\$${BalanceCache::lastTransactionId.name}"),
            ).lessThanValue(lastTrxIdNumerical)
            criteriaList.add(expr)
        }

        return And.and(*criteriaList.toTypedArray())
    }

    suspend fun updateBalanceIfNecessary(
        request: BalanceCacheModifyRequest
    ): BalanceCache {
        val condition = getUpdateConditions(request.eventDate, request.lastTransactionId)

        var setOps = SetOperation(BalanceCache::balance.name, condition)
        setOps = modifySet(condition, setOps, BalanceCache::eventDate.name, request.eventDate)
        setOps = modifySet(condition, setOps, BalanceCache::lastTransactionId.name, request.lastTransactionId!!)
        setOps = modifySet(condition, setOps, BalanceCache::createdAt.name, Instant.now())
        setOps = modifySet(condition, setOps, BalanceCache::updatedAt.name, Instant.now())

        val update = AggregationUpdate.update().set(setOps)

        return mongoTemplate.findAndModify(
            Query.query(Criteria.where("_id").`is`(request.accountKey)),
            update,
            upsertAndReturnNew,
            BalanceCache::class.java,
        ).awaitSingle()
    }

    suspend fun insertCache(request: NewCacheEventRequest): BalanceCache {
        return mongoTemplate.insert(
            BalanceCache(
                id = request.accountKey,
                balance = request.balance,
                lastTransactionId = request.lastTransactionId,
                eventDate = request.eventDate.toInstant()!!,
            )
        ).awaitSingle()
    }

}
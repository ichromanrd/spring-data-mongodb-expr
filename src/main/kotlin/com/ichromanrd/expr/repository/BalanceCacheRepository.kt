package com.ichromanrd.expr.repository

@Repository
class BalanceCacheRepository(
    private val mongoTemplate: ReactiveMongoTemplate,
) {

    suspend fun updateBalanceIfNecessary() {
        return mongoTemplate.findAndModify(
            Query(),
            Update.set(),

        )
    }

}
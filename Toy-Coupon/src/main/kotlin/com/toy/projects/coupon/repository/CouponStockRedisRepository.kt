package com.toy.projects.coupon.repository

import org.redisson.api.RedissonReactiveClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CouponStockRedisRepository(
    private val redissonClient: RedissonReactiveClient
) {

    private fun generateKey(id: Long) = "coupon:coupon:$id"

    fun setStock(id: Long, count: Long): Mono<Void> {
        return redissonClient.getAtomicLong(generateKey(id)).set(count).then()
    }

    fun getStock(id: Long): Mono<Long> {
        return redissonClient.getAtomicLong(generateKey(id)).get()
    }

    fun decreaseStock(id: Long, count: Long): Mono<Void> {
        return redissonClient.getAtomicLong(generateKey(id)).addAndGet(-count).then()
    }

    fun increaseStock(id: Long, count: Long): Mono<Void> {
        return redissonClient.getAtomicLong(generateKey(id)).addAndGet(count).then()
    }
}

package com.example.toyissuecoupon.repository

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

}

package com.toy.projects.coupon.repository

import org.redisson.api.RedissonReactiveClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CouponIssueHistoryRedisRepository(
    private val redissonClient: RedissonReactiveClient
) {

    private fun generateKey(couponId: Long) = "coupon:history:$couponId"

    fun issuedBefore(couponId: Long, userId: String): Mono<Boolean> {
        return redissonClient.getSet<String>(generateKey(couponId)).contains(userId)
    }

}

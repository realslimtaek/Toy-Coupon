package com.toy.projects.repository

import org.redisson.api.RedissonReactiveClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class CouponIssueHistoryRedisRepository(
    private val redissonClient: RedissonReactiveClient
) {

    private fun generateKey(couponId: Long) = "coupon:history:$couponId"

    fun setHistory(couponId: Long, users: Flux<String>): Mono<Void> {
        val set = redissonClient.getSet<String>(generateKey(couponId))

        return set.addAll(users).then()
    }

}

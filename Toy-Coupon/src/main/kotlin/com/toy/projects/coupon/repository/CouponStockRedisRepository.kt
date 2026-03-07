package com.toy.projects.coupon.repository

import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component

@Component
class CouponStockRedisRepository(
    private val redissonClient: RedissonClient
) {

    private fun generateKey(id: Long) = "coupon:coupon:$id"

    fun setStock(id: Long, count: Long) {
        redissonClient.getAtomicLong(generateKey(id)).set(count)
    }

    fun getStock(id: Long): Long {
        return redissonClient.getAtomicLong(generateKey(id)).get()
    }

    fun decreaseStock(id: Long, count: Long) {
        redissonClient.getAtomicLong(generateKey(id)).addAndGet(-count)
    }

    fun increaseStock(id: Long, count: Long) {
        redissonClient.getAtomicLong(generateKey(id)).addAndGet(count)
    }
}

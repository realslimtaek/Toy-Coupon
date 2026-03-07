package com.toy.projects.coupon.service

import com.toy.projects.coupon.repository.CouponStockRedisRepository
import org.springframework.stereotype.Service

@Service
class CouponService(
    private val redisRepository: CouponStockRedisRepository
) {

    fun test(userId: String, couponId: Long) {

        redisRepository.getStock(couponId).run {

            println("${couponId}, $this")
        }

        redisRepository.decreaseStock(couponId, 3)


        redisRepository.getStock(couponId).run {

            println("${couponId}, $this")
        }


    }

}
package com.toy.projects.coupon.service

import com.toy.projects.coupon.dto.MessageQueueEnum
import com.toy.projects.coupon.repository.CouponStockRedisRepository
import com.toy.projects.coupon.service.mq.MessageSender
import org.springframework.stereotype.Service

@Service
class CouponService(
    private val redisRepository: CouponStockRedisRepository,
    private val messageSender: MessageSender
) {

    fun test(userId: String, couponId: Long) {

        redisRepository.getStock(couponId).run {

            println("${couponId}, $this")
        }

        redisRepository.decreaseStock(couponId, 3)


        redisRepository.getStock(couponId).run {

            println("${couponId}, $this")
        }

        messageSender.send(MessageQueueEnum.ISSUE_COUPON, "test")


    }

}
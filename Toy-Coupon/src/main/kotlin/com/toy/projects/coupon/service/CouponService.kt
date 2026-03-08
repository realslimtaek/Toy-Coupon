package com.toy.projects.coupon.service

import com.toy.projects.coupon.dto.MessageQueueEnum
import com.toy.projects.coupon.repository.CouponIssueHistoryRedisRepository
import com.toy.projects.coupon.repository.CouponStockRedisRepository
import com.toy.projects.coupon.service.mq.MessageSender
import com.toy.projects.coupon.service.mq.dto.IssueCouponMessageDto
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CouponService(
    private val redisRepository: CouponStockRedisRepository,
    private val historyRedisRepository: CouponIssueHistoryRedisRepository,
    private val messageSender: MessageSender
) {
    fun test(userId: String, couponId: Long): Mono<Void> {
        // 발급한 쿠폰인지 확인
        return historyRedisRepository.add(couponId, userId)
            .flatMap { success ->
                if (success) {
                    redisRepository.decreaseStock(couponId, 1)
                        .flatMap { current ->
                            if (current < 0) {
                                redisRepository.increaseStock(couponId, 1)
                                    .then(Mono.error(RuntimeException("이미 발급 만료된 쿠폰입니다.")))

                            } else {
                                println("issue message")
                                messageSender.send(
                                    MessageQueueEnum.ISSUE_COUPON,
                                    IssueCouponMessageDto(couponId, userId)
                                )
                            }
                        }
                } else {
                    Mono.error(RuntimeException("이미 발급한 쿠폰입니다."))
                }
            }
    }
}
package com.toy.projects.coupon.service

import com.toy.projects.coupon.dto.MessageQueueEnum
import com.toy.projects.coupon.repository.CouponIssueHistoryRepository
import com.toy.projects.coupon.repository.CouponStockRedisRepository
import com.toy.projects.coupon.service.mq.MessageSender
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CouponService(
    private val redisRepository: CouponStockRedisRepository,
    private val couponIssueHistoryRepository: CouponIssueHistoryRepository,
    private val messageSender: MessageSender
) {
    fun test(userId: String, couponId: Long): Mono<Void> {
        // 발급한 쿠폰인지 확인
        return couponIssueHistoryRepository.existByCouponIdAndUserId(couponId, userId)
            // 발급 안 한 데이터만 확인
            .filter { !it }
            // 발급 했다면, 오류 발생
            .switchIfEmpty(Mono.error(RuntimeException("이미 발급한 쿠폰입니다.")))
            // 레디스에 쿠폰 수량 확인
            .flatMap { _ ->
                Mono.fromCallable { redisRepository.getStock(couponId) }
            }
            // 쿠폰 수량이 남아있는지 확인
            .filter { stock -> stock > 0 }
            // 쿠폰 수량이 없다면 에러 발생
            .switchIfEmpty(Mono.error(RuntimeException("이미 발급 만료된 쿠폰입니다.")))
            // 쿠폰 수량 차감
            .flatMap { _ ->
                Mono.fromCallable { redisRepository.decreaseStock(couponId, 1) }
            }
            // 차감 후 메세지 발생
            .doOnSuccess { _ ->
                messageSender.send(MessageQueueEnum.ISSUE_COUPON, "test")
            }
            .then()
    }
}
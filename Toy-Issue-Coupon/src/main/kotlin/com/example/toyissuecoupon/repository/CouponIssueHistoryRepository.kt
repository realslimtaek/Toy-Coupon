package com.example.toyissuecoupon.repository

import com.example.toyissuecoupon.entity.CouponIssueHistory
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface CouponIssueHistoryRepository: ReactiveCrudRepository<CouponIssueHistory, Long> {

    fun existByCouponIdAndUserId(couponId: Long, userId: String): Mono<Boolean>

}

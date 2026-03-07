package com.toy.projects.coupon.repository

import com.toy.projects.coupon.entity.CouponIssueHistory
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface CouponIssueHistoryRepository: ReactiveCrudRepository<CouponIssueHistory, Long> {

    fun existByCouponIdAndUserId(couponId: Long, userId: String): Mono<Boolean>

}

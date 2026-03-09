package com.toy.projects.repository

import com.toy.projects.entity.CouponIssueHistoryEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface CouponIssueHistoryRepository: ReactiveCrudRepository<CouponIssueHistoryEntity, Long> {
    fun findAllByCouponId(couponId: Long): Flux<CouponIssueHistoryEntity>
}

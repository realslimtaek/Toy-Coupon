package com.toy.projects.coupon.repository

import com.toy.projects.coupon.entity.CouponEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

interface CouponRepository: ReactiveCrudRepository<CouponEntity, Long> {

    fun findAllByExpiredAtGreaterThan(expired: LocalDateTime): Flux<CouponEntity>

}
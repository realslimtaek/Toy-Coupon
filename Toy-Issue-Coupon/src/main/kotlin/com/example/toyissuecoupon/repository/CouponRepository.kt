package com.example.toyissuecoupon.repository

import com.example.toyissuecoupon.entity.CouponEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

interface CouponRepository: ReactiveCrudRepository<CouponEntity, Long> {

    fun findAllByExpiredAtGreaterThan(expired: LocalDateTime): Flux<CouponEntity>

}
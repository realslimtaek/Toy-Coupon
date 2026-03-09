package com.toy.projects.repository

import com.toy.projects.entity.CouponEntity
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface CouponRepository: ReactiveCrudRepository<CouponEntity, Long>, CouponCustomRepository {

    fun findAllByExpiredAtGreaterThan(expired: LocalDateTime): Flux<CouponEntity>
}

interface CouponCustomRepository {
    fun updateStock(couponId: Long): Mono<Long>
    fun syncStock(couponId: Long, stock: Long): Mono<Long>
}

class CouponCustomRepositoryImpl(
    private val template: R2dbcEntityTemplate
): CouponCustomRepository {


    override fun updateStock(couponId: Long): Mono<Long> {
        return template.databaseClient
            .sql("""
                UPDATE COUPONS
                SET REMAIN = REMAIN - 1
                WHERE id = :id AND REMAIN > 0
            """.trimIndent())
            .bind("id", couponId)
            .fetch()
            .rowsUpdated()
    }

    override fun syncStock(couponId: Long, stock: Long): Mono<Long> {
        return template.databaseClient
            .sql("""
                UPDATE COUPONS
                SET REMAIN = :stock
                WHERE id = :id
            """.trimIndent())
            .bind("id", couponId)
            .bind("stock", stock)
            .fetch()
            .rowsUpdated()
    }

}
package com.toy.projects.scheduler

import com.toy.projects.repository.*
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class CouponStockSyncScheduler(
    private val couponRepository: CouponRepository,
    private val couponIssueHistoryRepository: CouponIssueHistoryRepository,
    private val couponStockRedisRepository: CouponStockRedisRepository,
    private val couponIssueHistoryRedisRepository: CouponIssueHistoryRedisRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Redis와 DB 간의 데이터 정합성을 맞춥니다.
     * initialDelay: 서버 시작 후 2분 대기 (Warmer 작업 시간 확보)
     * fixedDelay: 이전 작업 종료 후 5분 뒤 실행
     */
    @Scheduled(initialDelay = 1000 * 60 * 1, fixedDelay = 1000 * 60 * 5)
    fun syncStockAndHistory() {
        logger.info(">>> [Sync] Starting Stock and History Synchronization...")

        couponRepository.findAllByExpiredAtGreaterThan(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
            .flatMap { coupon ->
                val couponId = coupon.id!!

                // 1. [히스토리 동기화] DB -> Redis (중복 발급 방지 필터 보정)
                val historySync: Mono<Void> = couponIssueHistoryRepository.findAllByCouponId(couponId)
                    .map { it.userId }
                    .collectList()
                    .flatMap { userList ->
                        if (userList.isNotEmpty()) {
                            logger.debug("  -> [Sync History] Coupon {}: Syncing {} users from DB to Redis...", couponId, userList.size)
                            couponIssueHistoryRedisRepository.setHistory(couponId, Flux.fromIterable(userList))
                        } else {
                            Mono.empty()
                        }
                    }

                // 2. [재고 동기화] Redis -> DB (현재 시점의 정확한 재고 백업)
                val stockSync: Mono<Long> = couponStockRedisRepository.getStock(couponId)
                    .flatMap { redisStock ->
                        if (coupon.remain != redisStock) {
                            logger.info("  -> [Sync Stock] Coupon {}: DB({}) -> Redis({}). Updating DB...", couponId, coupon.remain, redisStock)
                            couponRepository.syncStock(couponId, redisStock)
                        } else {
                            Mono.empty()
                        }
                    }

                // 두 작업이 모두 완료될 때까지 기다림
                Mono.`when`(historySync, stockSync).thenReturn(coupon)
            }
            .doOnError { e -> logger.error("!!! [Sync] Error occurred: {}", e.message) }
            .doOnComplete { logger.info("<<< [Sync] Synchronization completed successfully.") }
            .subscribe()
    }
}

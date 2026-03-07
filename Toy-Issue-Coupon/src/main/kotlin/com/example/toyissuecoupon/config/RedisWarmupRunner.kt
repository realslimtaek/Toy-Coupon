package com.example.toyissuecoupon.config

import com.example.toyissuecoupon.repository.CouponIssueHistoryRedisRepository
import com.example.toyissuecoupon.repository.CouponIssueHistoryRepository
import com.example.toyissuecoupon.repository.CouponRepository
import com.toy.projects.coupon.repository.CouponStockRedisRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId

private val now get() = LocalDateTime.now(ZoneId.of("Asia/Seoul"))

@Component
class RedisWarmupRunner(
    private val couponRepository: CouponRepository,
    private val couponIssueHistoryRepository: CouponIssueHistoryRepository,
    private val redisRepository: CouponStockRedisRepository,
    private val historyRedisRepository: CouponIssueHistoryRedisRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        println("🚀 [Warmup] 쿠폰 데이터를 Redis로 로딩 시작...")

        couponRepository.findAllByExpiredAtGreaterThan(now)
            .mapNotNull { e ->
                println("${e.id} = ${e.remain}")
                e.id ?: return@mapNotNull null
                redisRepository.setStock(e.id!!, e.remain)

                e.id
            }
            .collectList()
            .subscribe(
                { ids -> println("✨ 총 ${ids.size}개의 쿠폰 로드 완료!") },
                { error -> println("❌ 에러: ${error.message}") }
            )

        couponIssueHistoryRepository.findAll()
            .groupBy { it.couponId }
            .mapNotNull {
                historyRedisRepository.setHistory(it.key(), it.map { e -> e.userId })
            }
            .collectList()
            .subscribe(
                { ids -> println("✨ 총 ${ids.size}개의 쿠폰 히스토리 로드 완료!") },
                { error -> println("❌ 에러: ${error.message}") }
            )
    }
}
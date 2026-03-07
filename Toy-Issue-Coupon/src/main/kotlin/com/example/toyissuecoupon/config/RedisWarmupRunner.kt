package com.example.toyissuecoupon.config

import com.example.toyissuecoupon.repository.CouponIssueHistoryRedisRepository
import com.example.toyissuecoupon.repository.CouponIssueHistoryRepository
import com.example.toyissuecoupon.repository.CouponRepository
import com.example.toyissuecoupon.repository.CouponStockRedisRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
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

        // mapNotNull 대신 flatMap을 사용합니다.
        couponRepository.findAllByExpiredAtGreaterThan(now)
            .flatMap { couponEntity ->
                // couponEntity나 id가 null일 경우를 안전하게 처리
                couponEntity?.id?.let { couponId ->
                    // 어떤 값으로 setStock이 호출되는지 로그를 남겨서 확인합니다.
                    println("  -> [Warmup] couponId=${couponId}, remain=${couponEntity.remain} 재고 로딩 중...")

                    // setStock이 반환하는 Mono를 flatMap이 구독하여 실행을 보장합니다.
                    redisRepository.setStock(couponId, couponEntity.remain)
                } ?: Mono.empty<Void>() // couponEntity나 id가 null이면 아무것도 하지 않음
            }
            .count() // 스트림의 총 개수를 셉니다.
            .subscribe(
                { count -> println("✨ 총 $count 개의 쿠폰 재고 로드 완료!") },
                { error -> println("❌ 재고 로딩 에러: ${error.message}") }
            )


        // 히스토리 로딩 부분도 flatMap으로 수정하는 것이 더 안전합니다.
        couponIssueHistoryRepository.findAll()
            .groupBy { it.couponId }
            .flatMap { groupedFlux ->
                // Long 타입의 userId Flux를 Repository에 전달합니다.
                historyRedisRepository.setHistory(groupedFlux.key(), groupedFlux.map { it.userId })
            }
            .count() // 몇 개의 쿠폰 히스토리가 있었는지 셉니다.
            .subscribe(
                { count -> println("✨ 총 $count 종류의 쿠폰 히스토리 로드 완료!") },
                { error -> println("❌ 히스토리 로딩 에러: ${error.message}") }
            )
    }
}
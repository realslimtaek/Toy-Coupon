package com.toy.projects.coupon.controller

import com.toy.projects.coupon.service.CouponService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.random.Random
import kotlin.random.nextLong

@RestController
@RequestMapping("/v1/coupon")
class CouponController(
    private val couponService: CouponService
) {

    /**
     * 테스트 편의성을 위해 userId는 입력할 수 있게 설정해두었습니다.
     */
    @PostMapping("/{userId}/{couponId}")
    fun issueCoupon(
        @PathVariable("userId") userId: String,
        @PathVariable("couponId") couponId: Long,
    ): Mono<Void> {
        return Flux.range(1, 10)
            // 각 숫자마다 couponService.test()를 실행 (병렬로 처리됨)
            .flatMap { i ->
                val randomValue = Random.nextLong(1L..100L)
                println(">>> $i 번째 요청 시작")
                couponService.test(randomValue.toString(), couponId)
                    .onErrorResume { e ->
                        // 한 요청이 실패해도 전체가 멈추지 않도록 에러 처리
                        println(">>> $i 번째 요청 실패: ${e.message}")
                        Mono.empty()
                    }
            }
            .then() // 모든 작업이 끝나면 종료
    }
}

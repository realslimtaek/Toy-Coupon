package com.toy.projects.coupon.controller

import com.toy.projects.coupon.service.CouponService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/coupon")
class CouponController(
    private val couponService: CouponService
) {

    /**
     * 테스트 편의성을 위해 userId는 사용할 수 있게 설정해두었습니다.
     */
    @PostMapping("/{userId}/{couponId}")
    fun issueCoupon(
        @PathVariable("userId") userId: String,
        @PathVariable("couponId") couponId: Long,
    ): Mono<Void> {
        return couponService.test(userId, couponId)

    }
}

package com.toy.projects.coupon.service.mq.dto

import java.time.LocalDateTime
import java.time.ZoneId

data class IssueCouponMessageDto(
    val couponId: Long,
    val userId: String,
    val issuedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
)

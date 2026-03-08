package com.toy.projects.service.mq.dto

import java.time.LocalDateTime

data class IssueCouponMessageDto(
    val couponId: Long,
    val userId: String,
    val issuedAt: LocalDateTime
)

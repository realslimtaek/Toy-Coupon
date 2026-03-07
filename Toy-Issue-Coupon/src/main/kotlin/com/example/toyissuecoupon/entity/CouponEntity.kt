package com.example.toyissuecoupon.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.time.ZoneId


@Table(name = "COUPONS")
class CouponEntity(

    @Id
    var id: Long? = null,

    @Column("COUPON_NAME")
    var couponName: String,

    @Column("REMAIN")
    var remain: Long = 0,

    @Column("CREATED_AT")
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul")),

    @Column("EXPIRED_AT")
    var expiredAt: LocalDateTime

)
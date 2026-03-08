package com.toy.projects.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.time.ZoneId


@Table(name = "COUPON_ISSUE_HISTORY")
class CouponIssueHistoryEntity (

    @Id
    var id: Long? = null,

    @Column("COUPON_ID")
    var couponId: Long,

    @Column("USER_ID")
    var userId: String,

    @Column("ISSUED_AT")
    var issuedAt: LocalDateTime

)

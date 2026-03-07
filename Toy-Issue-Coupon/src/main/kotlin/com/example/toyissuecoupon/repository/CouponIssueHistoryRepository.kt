package com.example.toyissuecoupon.repository

import com.example.toyissuecoupon.entity.CouponIssueHistory
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface CouponIssueHistoryRepository: ReactiveCrudRepository<CouponIssueHistory, Long>

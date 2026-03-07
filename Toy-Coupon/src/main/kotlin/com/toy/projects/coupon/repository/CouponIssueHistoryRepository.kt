package com.toy.projects.coupon.repository

import com.toy.projects.coupon.entity.CouponIssueHistory
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface CouponIssueHistoryRepository: ReactiveCrudRepository<CouponIssueHistory, Long> {

}

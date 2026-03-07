package com.toy.projects.repository

import com.toy.projects.entity.CouponIssueHistory
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface CouponIssueHistoryRepository: ReactiveCrudRepository<CouponIssueHistory, Long>

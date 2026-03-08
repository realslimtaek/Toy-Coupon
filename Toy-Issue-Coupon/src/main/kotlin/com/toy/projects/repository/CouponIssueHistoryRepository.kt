package com.toy.projects.repository

import com.toy.projects.entity.CouponIssueHistoryEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface CouponIssueHistoryRepository: ReactiveCrudRepository<CouponIssueHistoryEntity, Long>

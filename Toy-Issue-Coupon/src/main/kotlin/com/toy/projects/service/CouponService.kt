package com.toy.projects.service

import com.toy.projects.service.mq.dto.IssueCouponMessageDto
import com.toy.projects.entity.CouponIssueHistoryEntity
import com.toy.projects.repository.CouponIssueHistoryRedisRepository
import com.toy.projects.repository.CouponIssueHistoryRepository
import com.toy.projects.repository.CouponRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val couponHistoryRepository: CouponIssueHistoryRepository,
    private val couponIssueHistoryRedisRepository: CouponIssueHistoryRedisRepository
) {

    @Transactional
    fun handleMessage(dto: IssueCouponMessageDto): Mono<Void> {
        return couponRepository.updateStock(dto.couponId)
            .then(
                couponHistoryRepository.save(
                    CouponIssueHistoryEntity(
                        null,
                        dto.couponId,
                        dto.userId,
                        dto.issuedAt
                    )
                )
            )
            .then(
                couponIssueHistoryRedisRepository.setHistory(
                    dto.couponId,
                    Flux.just(dto.userId)
                )
            )
            .then()
    }
}

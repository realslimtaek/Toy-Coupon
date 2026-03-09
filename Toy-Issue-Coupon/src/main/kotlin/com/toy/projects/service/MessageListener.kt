package com.toy.projects.service

import com.toy.projects.config.RabbitConfig
import com.toy.projects.service.mq.dto.IssueCouponMessageDto
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class MessageConsumer(
    private val couponService: CouponService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [RabbitConfig.QUEUE_NAME])
    fun receiveMessages(dataList: List<IssueCouponMessageDto>): Mono<Void> {
        logger.info("<<< 메시지 배치 수신: ${dataList.size}개")
        return Flux.fromIterable(dataList)
            .flatMap({ data ->
                couponService.handleMessage(data)
            }, 10)
            .then()
            .doOnSuccess { logger.info("<<< 메시지 배치 처리 완료") }
            .doOnError { e -> logger.error("<<< 메시지 배치 처리 실패: ${e.message}") }
    }
}

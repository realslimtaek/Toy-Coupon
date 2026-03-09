package com.toy.projects.coupon.service.mq

import com.toy.projects.coupon.dto.MessageQueueEnum
import com.toy.projects.coupon.service.mq.dto.IssueCouponMessageDto
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class MessageSender(private val rabbitTemplate: RabbitTemplate) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun send(enum: MessageQueueEnum, data: IssueCouponMessageDto): Mono<Void> {
        return Mono.fromRunnable<Void> {
            logger.info(">>> 메시지 발행 시도: $data")
            rabbitTemplate.convertAndSend(enum.exchange, enum.routingKey, data)
            logger.info(">>> 메시지 발행 완료")
        }.subscribeOn(Schedulers.boundedElastic())
    }
}

package com.toy.projects.coupon.service.mq

import com.toy.projects.coupon.dto.MessageQueueEnum
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class MessageSender(private val rabbitTemplate: RabbitTemplate) {

    /**
     * @param exchange 메시지를 보낼 Exchange 이름
     * @param routingKey 메시지를 전달할 경로 (Routing Key)
     * @param data 보낼 데이터 (객체, 문자열 등)
     */
    fun send(enum: MessageQueueEnum, data: Any) {
        println(">>> 메시지 발행 시도: $data")
        
        rabbitTemplate.convertAndSend(enum.exchange, enum.routingKey, data)
        
        println(">>> 메시지 발행 완료")
    }
}

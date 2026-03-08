package com.toy.projects.service

import com.toy.projects.service.mq.dto.IssueCouponMessageDto
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux


@Service
class MessageConsumer(
    private val couponService: CouponService
) {

    @RabbitListener(queues = ["my.spring4.queue"])
    fun receiveMessages(dataList: List<IssueCouponMessageDto>) {
        println("<<< 메시지 배치 수신: ${dataList.size}개")
        
        // 10개를 비동기적으로 동시에 처리
        Flux.fromIterable(dataList)
            .flatMap({ data ->
                couponService.handleMessage(data)
            }, 10) // 병렬 처리 개수 설정
            .collectList()
            .block() // 배치가 완료될 때까지 대기하여 Ack를 보냄
            
        println("<<< 메시지 배치 처리 완료")
    }
}

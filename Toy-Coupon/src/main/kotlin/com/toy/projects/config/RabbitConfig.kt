package com.toy.projects.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.SimpleMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableRabbit // @RabbitListener를 활성화합니다.
class RabbitConfig {

    companion object {
        const val QUEUE_NAME = "my.spring4.queue"
        const val EXCHANGE_NAME = "coupon.exchange"
        const val ROUTING_KEY = "issue.key"
    }

    // 1. Connection 설정 (Docker에 설정한 admin/1234 사용)
    @Bean
    fun rabbitConnectionFactory(): ConnectionFactory {
        val factory = CachingConnectionFactory("localhost")
        factory.username = "admin"
        factory.setPassword("1234")
        factory.port = 5672
        return factory
    }

    // 2. Queue, Exchange, Binding 등록 (RabbitAdmin이 이 빈들을 보고 브로커에 생성함)
    @Bean
    fun amqpAdmin(): RabbitAdmin {
        return RabbitAdmin(rabbitConnectionFactory())
    }

    @Bean
    fun queue() = Queue(QUEUE_NAME, true)

    @Bean
    fun exchange() = DirectExchange(EXCHANGE_NAME)

    @Bean
    fun binding(): Binding {
        return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY)
    }

    // 3. 메시지 변환기 (JSON 사용)
    @Bean
    fun messageConverter() = SimpleMessageConverter()

    // 4. 메시지 전송용 템플릿
    @Bean
    fun rabbitTemplate(): RabbitTemplate {
        val template = RabbitTemplate(rabbitConnectionFactory())
        template.messageConverter = messageConverter()
        return template
    }

    // 5. 메시지 수신용 컨테이너 팩토리 (@RabbitListener가 사용)
    @Bean
    fun rabbitListenerContainerFactory(): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(rabbitConnectionFactory())
        factory.setMessageConverter(messageConverter())
        return factory
    }
}
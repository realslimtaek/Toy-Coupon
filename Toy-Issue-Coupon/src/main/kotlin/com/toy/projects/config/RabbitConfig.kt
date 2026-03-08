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
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableRabbit
class RabbitConfig(
    @Value("\${dv.mq.username}") private val user: String,
    @Value("\${dv.mq.password}") private val password: String,
) {

    companion object {
        const val QUEUE_NAME = "coupon.issue.queue"
        const val EXCHANGE_NAME = "coupon.exchange"
        const val ROUTING_KEY = "issue.key"
    }

    @Bean
    fun rabbitConnectionFactory(): ConnectionFactory {
        val factory = CachingConnectionFactory("localhost")
        factory.username = user
        factory.setPassword(password)
        factory.port = 5672
        return factory
    }

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

    @Bean
    fun messageConverter(): MessageConverter {
        val objectMapper = JsonMapper.builder()
            .addModule(kotlinModule())
            .build()
        return JacksonJsonMessageConverter(objectMapper)
    }

    @Bean
    fun rabbitTemplate(): RabbitTemplate {
        val template = RabbitTemplate(rabbitConnectionFactory())
        template.messageConverter = messageConverter()
        return template
    }

    @Bean
    fun rabbitListenerContainerFactory(): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(rabbitConnectionFactory())
        factory.setMessageConverter(messageConverter())
        factory.setBatchListener(true)
        factory.setConsumerBatchEnabled(true)
        factory.setBatchSize(10)
        return factory
    }
}
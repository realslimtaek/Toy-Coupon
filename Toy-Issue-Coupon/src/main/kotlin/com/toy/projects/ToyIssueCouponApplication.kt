package com.toy.projects

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableR2dbcRepositories
@EnableScheduling
class ToyIssueCouponApplication

fun main(args: Array<String>) {
    runApplication<ToyIssueCouponApplication>(*args)
}

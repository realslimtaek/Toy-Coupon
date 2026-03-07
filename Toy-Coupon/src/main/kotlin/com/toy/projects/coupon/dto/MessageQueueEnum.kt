package com.toy.projects.coupon.dto

enum class MessageQueueEnum(val exchange: String, val routingKey: String) {
    ISSUE_COUPON("coupon.exchange", "issue.key")
}
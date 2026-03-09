package com.toy.projects.coupon.service

import com.toy.projects.coupon.repository.CouponIssueHistoryRedisRepository
import com.toy.projects.coupon.repository.CouponStockRedisRepository
import com.toy.projects.coupon.service.mq.MessageSender
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class CouponServiceConcurrencyTest {

    private val redisRepository = mockk<CouponStockRedisRepository>()
    private val historyRedisRepository = mockk<CouponIssueHistoryRedisRepository>()
    private val messageSender = mockk<MessageSender>(relaxed = true)

    private val couponService = CouponService(redisRepository, historyRedisRepository, messageSender)

    @Test
    fun `100명 동시 쿠폰 발급 테스트 - 큐 발송 횟수 검증`() {
        val couponId = 1L
        val threadCount = 100
        val stockLimit = 50
        val executorService: ExecutorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(threadCount)

        val stock = AtomicInteger(stockLimit)
        
        // Mocking: historyRedisRepository.add always returns true (Assume different users)
        every { historyRedisRepository.add(couponId, any()) } returns Mono.just(true)

        // Mocking: decreaseStock logic
        every { redisRepository.decreaseStock(couponId, 1) } answers {
            Mono.just(stock.decrementAndGet().toLong())
        }
        
        // Mocking: rollback logic
        every { redisRepository.increaseStock(couponId, 1) } answers {
            stock.incrementAndGet()
            Mono.empty()
        }

        // Mocking: messageSender.send returns Mono.empty() (Void)
        every { messageSender.send(any(), any()) } returns Mono.empty()

        for (i in 1..threadCount) {
            val userId = "user$i"
            executorService.submit {
                try {
                    // Reactor의 스케줄러 영향 없이 block()으로 대기
                    couponService.test(userId, couponId).block()
                } catch (e: Exception) {
                    // "이미 발급 만료된 쿠폰입니다." 에러 발생 예상 (51번째부터)
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executorService.shutdown()

        // 큐 발송(messageSender.send)이 정확히 stockLimit(50) 만큼 호출되었는지 확인
        io.mockk.verify(exactly = stockLimit) {
            messageSender.send(any(), any())
        }

        println("Final stock count: ${stock.get()}")
        assert(stock.get() == 0) // 50개 성공, 50개 실패(및 롤백) -> 0
    }
}

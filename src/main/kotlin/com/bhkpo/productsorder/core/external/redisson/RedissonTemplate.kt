package com.bhkpo.productsorder.core.external.redisson

import com.bhkpo.productsorder.core.exception.LockAcquireException
import com.bhkpo.productsorder.core.external.redisson.callback.RedissonCallback
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedissonTemplate(private val redissonClient: RedissonClient) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun <T> lock(
        key: String,
        callback: RedissonCallback<T>,
        waitTime: Long = 5,
        leaseTime: Long = 5,
        unit: TimeUnit = TimeUnit.SECONDS
    ): T {
        val lock: RLock = redissonClient.getLock(key)

        try {
            val available: Boolean = lock.tryLock(waitTime, leaseTime, unit)
            if (!available) {
                log.error("Failed to acquire Lock. key=${key}")
                throw LockAcquireException()
            }
            return callback.call()

        } catch (e: Exception) {
            log.error("Raised ${e::class.simpleName}. error=${e.message}")
            throw e

        } finally {
            lock.unlock()
        }
    }
}

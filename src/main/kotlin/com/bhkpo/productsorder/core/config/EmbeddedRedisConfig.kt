package com.bhkpo.productsorder.core.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Primary
import redis.embedded.RedisServer

@Configuration
class EmbeddedRedisConfig : DisposableBean {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${spring.redis.port}")
    var redisPort: Int = 0

    @Value("\${spring.redis.host}")
    lateinit var redisHost: String

    @Value("\${spring.redis.maxheap}")
    lateinit var redisMaxHeap: String

    @Value("\${redis.redisson.connection-pool-size}")
    var redissonConnectionPoolSize: Int = 0

    lateinit var redisServer: RedisServer

    @Bean
    fun redisServer(): RedisServer {
        redisServer = RedisServer.builder()
            .port(redisPort)
            .setting("maxmemory $redisMaxHeap")
            .build()
        try {
            redisServer.start()
        } catch (e: Exception) {
            log.error("Redis Start Error. error=${e.message}")
        }

        return redisServer
    }

    @Bean
    @Primary
    @DependsOn("redisServer")
    fun redisClient(): RedissonClient {
        val config = Config()
        config.useSingleServer()
            .setConnectionPoolSize(redissonConnectionPoolSize)
            .setAddress("redis://${redisHost}:${redisPort}")

        return Redisson.create(config)
    }

    override fun destroy() {
        redisServer.stop()
    }
}

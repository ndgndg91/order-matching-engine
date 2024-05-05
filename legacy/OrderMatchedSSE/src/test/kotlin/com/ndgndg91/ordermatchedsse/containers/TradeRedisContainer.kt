package com.ndgndg91.ordermatchedsse.containers

import org.junit.jupiter.api.BeforeAll
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

interface TradeRedisContainer {

    companion object {

        @Container
        private val redis: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:7.0.0"))
            .withExposedPorts(6379)
            .withAccessToHost(true)
            .withReuse(true)

        @JvmStatic
        @DynamicPropertySource
        fun overrideProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.redis.host", redis::getHost)
            registry.add("spring.redis.port") { redis.getMappedPort(6379)}
            registry.add("spring.redis.timeout") { "5000" }
            registry.add("spring.redis") { "0" }
        }

        // withReuse 를 사용하기 위해서는 ~/.testcontainers.properties 의 testcontainers.reuse.enable=true 로 설정해야한다.
        @JvmStatic
        @BeforeAll
        fun setup() {
            redis.start()
        }

    }

}
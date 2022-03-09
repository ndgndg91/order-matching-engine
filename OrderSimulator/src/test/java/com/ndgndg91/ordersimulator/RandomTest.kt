package com.ndgndg91.ordersimulator

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import kotlin.random.Random

class RandomTest {

    private val log = LoggerFactory.getLogger("RandomTest")

    @Test
    fun random() {
        var i = 0;
        while (true) {
            val nextInt = Random.nextInt(2)
            log.info("{}", nextInt)
            i++;
            if (i == 10) {
                break
            }
        }

    }
}
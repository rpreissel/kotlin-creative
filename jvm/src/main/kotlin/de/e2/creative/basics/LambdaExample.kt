package de.e2.creative.basics.lambda

import mu.KLogging
import kotlin.math.PI

class CircleRaw1(val radius: Double) {
    fun diameter(): Double {
        try {
            logger.info("diameter - begin")
            return radius * 2
        } finally {
            logger.info("diameter - end")
        }
    }

    companion object : KLogging()
}

class CircleRaw2(val radius: Double) {
    fun diameter(): Double = try {
        logger.info("diameter - begin")
        radius * 2
    } finally {
        logger.info("diameter - end")
    }

    companion object : KLogging()
}

open class MyLogging : KLogging() {
    fun <T> withTracing(
        prefix: String,
        block: () -> T
    ) = try {
        logger.info("$prefix - begin")
        block()
    } finally {
        logger.info("$prefix - end")
    }

    fun <T> withTracingParameter(
        prefix: String,
        block: (String) -> T
    ): T = try {
        logger.info("$prefix - begin")
        block(prefix)
    } finally {
        logger.info("$prefix - end")
    }
}

class Circle(val radius: Double) {
    fun diameter(): Double = withTracing("diameter") {
        radius * 2
    }

    val area: Double
        get() = withTracing("area") {
            logger.info("area - before radius square")
            val r2 = radius * radius
            logger.info("area - after radius square")
            PI * r2
        }

    val areaPara: Double
        get() = withTracingParameter("area") { prefix ->
            logger.info("$prefix - before radius square")
            val r2 = radius * radius
            logger.info("$prefix - after radius square")
            PI * r2
        }

    companion object : MyLogging()
}

fun main(args: Array<String>) {
    Circle(42.0).diameter()
}
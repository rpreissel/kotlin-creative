package de.e2.creative.basic.lambdareceiver

import mu.KLogger
import mu.KLogging
import kotlin.math.PI


class TracingContext(val logger: KLogger, val prefix: String) {
    fun trace(msg: String) {
        logger.info("$prefix - $msg")
    }
}

open class MyLogging : KLogging() {
    fun <T> withTracing(
        prefix: String,
        block: TracingContext.() -> T
    ): T = try {
        logger.info("$prefix - begin")
        val context = TracingContext(logger, prefix)
        context.block()
    } finally {
        logger.info("$prefix - end")
    }
}

class Circle(val radius: Double) {
    val area: Double
        get() = withTracing("area") {
            trace("before radius square")
            val r2 = radius * radius
            trace("after radius square")
            PI * r2
        }

    companion object : MyLogging()
}


fun main(args: Array<String>) {
    Circle(42.0).area
}
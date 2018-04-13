package de.e2.creative.basic.lambdareceiverext

import mu.KLogger
import mu.KLogging
import kotlin.math.PI

class TracingContext(val logger: KLogger, val prefix: String) {
    fun trace(msg: String) {
        logger.info("$prefix - $msg")
    }

    fun <T> T.dump(msg: String = "dump"): T {
        trace("$msg: $this") // entspricht: this@TracingContext.log("$msg: $this")
        return this
    }
}

fun <T> KLogging.withTracing(
    prefix: String,
    block: TracingContext.() -> T
): T = try {
    logger.info("$prefix - begin")
    val context = TracingContext(logger, prefix)
    context.block()
} finally {
    logger.info("$prefix - end")
}


class Circle(val radius: Double) {
    val areaSimple: Double
        get() = withTracing("area") {
            val r2 = radius * radius
            trace("dump r2: $r2")
            PI * r2
        }

    val area: Double
        get() = withTracing("area") {
            PI * (radius * radius).dump("dump r2")
        }

    companion object : KLogging()
}


fun main(args: Array<String>) {
    Circle(42.0).area
}


package de.e2.creative.klogging

import mu.KLogging
import kotlin.math.PI

class CircleIntro(val radius: Double) {
    fun diameter(): Double {
        return radius * 2
    }

    val circumference: Double
        get() {
            return diameter() * PI
        }

    val area: Double
        get() = PI * radius * radius
}

class Circle(val radius: Double) {
    fun diameter(): Double {
        logger.info("compute diameter")
        return radius * 2
    }

    val circumference: Double
        get() {
            logger.info {
                "compute circumference"
            }
            return diameter() * PI
        }

    companion object : KLogging()
}

fun main(args: Array<String>) {
    val circle = Circle(42.0)
    println(circle.radius)
    println("Diameter: ${circle.diameter()} Circumference: ${circle.circumference}")

    Circle.logger.info("static access")
}
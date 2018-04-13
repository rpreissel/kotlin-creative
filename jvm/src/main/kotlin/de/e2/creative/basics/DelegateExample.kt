package de.e2.creative.basics

import kotlin.math.PI

class Circle(val radius: Double) {
    val diameter by lazy {
        println("compute diameter")
        radius * 2
    }
    val circumference by lazy {
        println("compute circumference")
        diameter * PI
    }
}

fun main(args: Array<String>) {
    val circle = Circle(10.0)
    println(circle.diameter)
    println(circle.circumference)
}
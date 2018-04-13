package de.e2.creative.basics.infix

class Circle(val x: Double, val y: Double, val radius: Double) {
    fun intersects(other: Circle): Boolean {
        val distanceX = this.x - other.x
        val distanceY = this.y - other.y
        val radiusSum = this.radius + other.radius
        return distanceX * distanceX + distanceY * distanceY <= radiusSum * radiusSum
    }

    infix fun intersectsi(other: Circle): Boolean {
        val distanceX = this.x - other.x
        val distanceY = this.y - other.y
        val radiusSum = this.radius + other.radius
        return distanceX * distanceX + distanceY * distanceY <= radiusSum * radiusSum
    }

    operator fun mod(other: Circle): Boolean = intersects(other)
}

fun main(args: Array<String>) {
    val c1 = Circle(x = 100.0, y = 100.0, radius = 50.0)
    val c2 = Circle(x = 75.0, y = 75.0, radius = 5.0)
    println(c1.intersects(c2))
    println(c1 intersectsi c2)
}
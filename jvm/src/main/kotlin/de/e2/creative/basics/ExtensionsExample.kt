package de.e2.creative.basics

import java.net.URL

object StringUtil {
    fun toURL(s: String) = URL(s)
}


fun String.toURL() = URL(this)

fun main(args: Array<String>) {
    val url1: URL = StringUtil.toURL("http://localhost:8080")
    val url2: URL = "http://localhost:8080".toURL()
}
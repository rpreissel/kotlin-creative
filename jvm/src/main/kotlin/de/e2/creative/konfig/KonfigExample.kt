package de.e2.creative.konfig


import com.natpryce.konfig.ConfigurationProperties.Companion.fromResource
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType


object server : PropertyGroup() {
    val port by intType
    val host by stringType
}

fun main(args: Array<String>) {

    val config = systemProperties() overriding
            EnvironmentVariables() overriding
//            fromFile(File("/etc/myservice.properties")) overriding
            fromResource("defaults.properties")

    val host = config[server.host]
    val port = config[server.port]

}
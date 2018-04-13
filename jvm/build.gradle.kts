import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.gradle.api.plugins.ExtensionAware
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.include
import org.jetbrains.kotlin.contracts.model.structure.UNKNOWN_COMPUTATION.type

import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension

val kotlinCoroutineVersion: String by extra { "0.22.5" }
val ktorVersion: String by extra { "0.9.1" }
val joobyVersion: String by extra { "1.2.3" }
val spekVersion: String by extra { "1.1.5" }


buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.3")
    }
}

apply {
    //    plugin("org.springframework.boot")
    plugin("org.junit.platform.gradle.plugin")
}

configure<JUnitPlatformExtension> {
    filters {
        engines {
            include("spek")
        }
    }
}

plugins {
    val kotlinVersion = "1.2.31"
    application

    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
}


group = "de.e2"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://dl.bintray.com/kotlin/kotlinx.html")
    maven("https://dl.bintray.com/kotlin/exposed")
    maven("https://dl.bintray.com/kotlin/squash")
    maven("https://dl.bintray.com/kodein-framework/Kodein-DI")
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-runtime")
    compile("org.jetbrains.kotlin:kotlin-stdlib")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk7")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("org.jetbrains.kotlin:kotlin-reflect")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutineVersion")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCoroutineVersion")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$kotlinCoroutineVersion")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutineVersion")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-io:$kotlinCoroutineVersion")
    compile("io.github.microutils:kotlin-logging:1.4.4")
    compile("ch.qos.logback:logback-classic:1.2.1")

    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.2")

    compile("io.ktor:ktor-server-core:$ktorVersion")
    compile("io.ktor:ktor-server-netty:$ktorVersion")
    compile("io.ktor:ktor-locations:$ktorVersion")
    compile("io.ktor:ktor-html-builder:$ktorVersion")
    compile("io.ktor:ktor-gson:$ktorVersion")

    compile("org.jooby:jooby-lang-kotlin:$joobyVersion")
    compile("org.jooby:jooby-netty:$joobyVersion")

    compile("org.jetbrains.exposed:exposed:0.9.1")
    compile("com.h2database:h2:1.4.196")
    compile("org.kodein.di:kodein-di-generic-jvm:5.0.0")
//    compile("com.github.salomonbrys.kodein:kodein:4.1.0")
    compile("com.beust:klaxon:2.1.6")
    compile("com.natpryce:konfig:1.6.1.0")

    compile("org.jetbrains.squash:squash:0.2.4")

    testCompile("org.jetbrains.kotlin:kotlin-test-junit")
    testCompile("org.jetbrains.spek:spek-api:$spekVersion")
    testRuntime("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
    testCompile("io.rest-assured:rest-assured:3.0.5")
    testCompile("org.amshove.kluent:kluent:1.23")
    testRuntime("org.glassfish.jaxb:jaxb-runtime:2.3.0")
}

kotlin {
    experimental.coroutines = Coroutines.ENABLE
}

application {
    mainClassName = "de.e2.creative..."
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

configurations.all {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jre7")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jre8")
}

val clean2 = tasks["clean"]
val clean by tasks


val helloTask2 = tasks.create("helloWorld2") {
    dependsOn(clean)
    doLast { println("Hello") }
}
val helloTask by tasks.creating {
    dependsOn(clean)
    doLast { println("Hello") }
}

tasks {
    "worldTask" {
        dependsOn(helloTask)
        doLast { println("World") }
    }
}

tasks.create("worldTask2") {
    dependsOn(helloTask)
    doLast { println("World") }
}

tasks {
    "worldTask3"(Zip::class) {
        dependsOn(helloTask)
        doLast { println("World") }
    }
}
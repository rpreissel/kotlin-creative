import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.extra
import org.jetbrains.kotlin.contracts.model.structure.UNKNOWN_COMPUTATION.type

import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension

val kotlinCoroutineVersion: String by extra { "0.22.5" }
val ktorVersion: String by extra { "0.9.1" }
val kodeinVersion: String by extra { "5.0.0" }


buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.3")
    }
}

plugins {
    val kotlinVersion = "1.2.31"

    id("org.jetbrains.kotlin.jvm") version kotlinVersion
}


group = "de.e2"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://dl.bintray.com/kotlin/kotlinx.html")
    maven("https://dl.bintray.com/kodein-framework/Kodein-DI")
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-runtime")
    compile("org.jetbrains.kotlin:kotlin-stdlib")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk7")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("org.jetbrains.kotlin:kotlin-reflect")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutineVersion")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutineVersion")

    compile("io.ktor:ktor-server-core:$ktorVersion")
    compile("io.ktor:ktor-server-netty:$ktorVersion")
    compile("io.ktor:ktor-locations:$ktorVersion")
    compile("io.ktor:ktor-html-builder:$ktorVersion")
    compile("io.ktor:ktor-gson:$ktorVersion")

    compile("org.kodein.di:kodein-di-generic-jvm:$kodeinVersion")
    compile("org.kodein.di:kodein-di-conf-jvm:$kodeinVersion")

    compile("com.h2database:h2:1.4.196")
}

kotlin {
    experimental.coroutines = Coroutines.ENABLE
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

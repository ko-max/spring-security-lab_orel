plugins {
    kotlin("jvm") version "1.9.23" apply false
    // Needed for Hibernate
    id("org.jetbrains.kotlin.plugin.noarg") version "1.9.23" apply false
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.23" apply false
    // Needed for Spring Data
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.23" apply false

    // Client
    id("org.jetbrains.compose") version "1.6.2" apply false
    kotlin("plugin.serialization") version "1.9.23" apply false
}

buildscript {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

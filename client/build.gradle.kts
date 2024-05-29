import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization")
}

group = "ua.kpi.its.lab.security"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}


dependencies {
    val kotlin = "1.9.23"
    val ktor = "2.3.10"
    val log4j = "2.23.1"
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlin}")

    implementation("io.ktor:ktor-client-okhttp-jvm:${ktor}")
    implementation("io.ktor:ktor-client-content-negotiation:${ktor}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor}")

    implementation("org.apache.logging.log4j:log4j-core:${log4j}")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:${log4j}")

    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    // Include the Test API
    testImplementation(compose.desktop.uiTestJUnit4)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "ua.kpi.its.lab.security.ClientApplicationKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ClientApplication"
            packageVersion = "1.0.0"
        }
    }
}
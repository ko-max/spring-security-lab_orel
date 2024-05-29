plugins {
    kotlin("jvm")
    kotlin("plugin.noarg")
    kotlin("plugin.spring")
    application
}

group = "ua.kpi.its.lab.security"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val kotlin = "1.9.23"
    val spring = "6.1.5"
    val log4j = "2.23.1"
    val jackson = "2.17.0"
    val security = "6.2.4"

    // Needed for Spring Data
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlin}")

    // Data
    implementation("org.springframework.data:spring-data-jpa:3.2.3")
    implementation("org.hsqldb:hsqldb:2.7.2")
    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")

    // Web
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    implementation("org.eclipse.jetty.ee10:jetty-ee10-webapp:12.0.8")
    implementation("org.springframework:spring-webmvc:${spring}")

    // Security
    implementation("org.springframework.security:spring-security-config:${security}")
    implementation("org.springframework.security:spring-security-web:${security}")
    implementation("org.springframework.security:spring-security-oauth2-resource-server:${security}")
    implementation("org.springframework.security:spring-security-oauth2-jose:${security}")

    // Serialization
    implementation("com.fasterxml.jackson.core:jackson-databind:${jackson}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${jackson}")

    implementation("org.apache.logging.log4j:log4j-core:${log4j}")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:${log4j}")

    testImplementation("org.jetbrains.kotlin:kotlin-test:${kotlin}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("ua.kpi.its.lab.security.MainKt")
}

noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
    invokeInitializers = true // Ensures that property initializers are called in the generated constructors.
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}
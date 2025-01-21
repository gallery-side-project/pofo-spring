import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.jlleitschuh.gradle.ktlint.tasks.GenerateReportsTask


plugins {
    val kotlinVersion = "2.1.0"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.lombok") version kotlinVersion
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
    id("io.freefair.lombok") version "8.10"
}

group = "org.pofo"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")
    implementation("com.opencsv:opencsv:5.9")
    implementation("jakarta.json:jakarta.json-api")
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework:spring-webflux")
    testImplementation("org.springframework.graphql:spring-graphql-test")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.fasterxml.jackson.core:jackson-core:2.18.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    runtimeOnly("org.postgresql:postgresql:42.7.4")
    runtimeOnly("com.h2database:h2:2.3.232")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework:spring-aspects")

    val queryDslVersion = "6.10.1"
    implementation("io.github.openfeign.querydsl:querydsl-core:$queryDslVersion")
    implementation("io.github.openfeign.querydsl:querydsl-jpa:$queryDslVersion")
    annotationProcessor("io.github.openfeign.querydsl:querydsl-apt:$queryDslVersion:jpa")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    testImplementation("io.mockk:mockk:1.13.14")
    testImplementation("com.ninja-squad:springmockk:4.0.2")

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    implementation("com.google.cloud.sql:postgres-socket-factory:1.21.2")
}

// --- QueryDSL Settings

val generatedDir =
    layout.buildDirectory
        .dir("src/main/generated")
        .get()
        .asFile

tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(generatedDir)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }

    sourceSets {
        main {
            java.srcDir(generatedDir)
        }
    }
}

tasks.named("clean") {
    doLast {
        generatedDir.deleteRecursively()
    }
}

// ---

kotlin {
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JvmTarget.JVM_17
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// --- ktlint Settings
ktlint {
    version.set("1.4.1")

    reporters {
        reporter(ReporterType.JSON)
    }
}

tasks.withType<GenerateReportsTask> {
    reportsOutputDirectory.set(
        rootProject.layout.buildDirectory.dir("reports/ktlint/${project.name}"),
    )
}

tasks.named("runKtlintCheckOverMainSourceSet") {
    dependsOn(tasks.named("compileJava"))
}
// ---

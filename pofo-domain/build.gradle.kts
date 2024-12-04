import org.springframework.boot.gradle.tasks.bundling.BootJar

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

dependencies {
    implementation(project(":pofo-common"))

    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.core:jackson-core:2.18.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    runtimeOnly("org.postgresql:postgresql:42.7.4")
    runtimeOnly("com.h2database:h2:2.3.232")

    // QueryDSL dependencies
    api("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    implementation("com.google.code.findbugs:jsr305:3.0.2")
}

// --------------------------------- QueryDSL settings -----------------------------------------------
val generatedDir =
    layout.buildDirectory
        .dir("generated/querydsl")
        .get()
        .asFile

sourceSets {
    named("main") {
        java.srcDir(generatedDir)
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.plusAssign("-s")
    options.compilerArgs.plusAssign(generatedDir.absolutePath)
}

tasks.named("clean") {
    delete(generatedDir)
}

// --------------------------------------------------------------------------------------

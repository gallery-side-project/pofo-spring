dependencies {
    implementation(project(":pofo-domain"))
    implementation(project(":pofo-common"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.session:spring-session-data-redis")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework:spring-webflux")
    testImplementation("org.springframework.graphql:spring-graphql-test")
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core:5.7.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    testImplementation("io.mockk:mockk:1.13.13")
}

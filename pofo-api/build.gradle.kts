dependencies {
    implementation(project(":pofo-domain"))
    implementation(project(":pofo-common"))
    implementation(project(":pofo-infra"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework:spring-webflux")
    testImplementation("org.springframework.graphql:spring-graphql-test")

    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    testImplementation("io.mockk:mockk:1.13.14")
    testImplementation("com.ninja-squad:springmockk:4.0.2")

    implementation("org.opensearch.client:opensearch-java:2.11.1")

    implementation("com.opencsv:opencsv:5.9")
    implementation("jakarta.json:jakarta.json-api")

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    implementation("com.google.cloud.sql:postgres-socket-factory:1.21.2")

    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework:spring-aspects")
}

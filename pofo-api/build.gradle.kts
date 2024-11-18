dependencies {
    implementation(project(":pofo-domain"))
    implementation(project(":pofo-common"))
    implementation(project(":pofo-elastic-search"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework:spring-webflux")
    testImplementation("org.springframework.graphql:spring-graphql-test")

    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")

    implementation("org.opensearch.client:spring-data-opensearch-starter:1.5.3") {
        exclude("org.opensearch.client", "opensearch-rest-high-level-client")
    }
    implementation("org.opensearch.client:opensearch-java:2.11.1")

    implementation("com.opencsv:opencsv:5.6")
    implementation("jakarta.json:jakarta.json-api")
}

tasks.register<Copy>("copyYmlInSubmodule") {
    copy {
        from("$rootDir/pofo-spring-submodule")
        include("*.yml")
        into("src/main/resources")
    }
}

tasks.named("build") {
    dependsOn("copyYmlInSubmodule")
}

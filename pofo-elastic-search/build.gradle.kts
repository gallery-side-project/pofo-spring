import org.springframework.boot.gradle.tasks.bundling.BootJar

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

dependencies {
//    implementation("org.opensearch.client:spring-data-opensearch-starter:1.5.3")
//    implementation("org.opensearch.client:opensearch-java:2.10.3")
//    implementation("jakarta.json:jakarta.json-api")

//    implementation("org.springframework.data:spring-data-opensearch-starter")
//    implementation("org.opensearch.client:opensearch-rest-client:2.18.0")
//    implementation("jakarta.json:jakarta.json-api")
//
//    implementation("org.opensearch.client:spring-data-opensearch-starter:1.2.0")
// //    implementation("org.opensearch.client:spring-data-opensearch:1.2.0")
//    implementation("org.opensearch.client:opensearch-java:2.6.0")

    // Spring Data OpenSearch Starter for Spring Boot integration
    implementation("org.opensearch.client:spring-data-opensearch-starter:1.5.3") {
        exclude("org.opensearch.client", "opensearch-rest-high-level-client")
    }

    // OpenSearch Java Client
    implementation("org.opensearch.client:opensearch-java:2.11.1")

    // Optional: Jakarta JSON API for JSON handling
    implementation("jakarta.json:jakarta.json-api")
}

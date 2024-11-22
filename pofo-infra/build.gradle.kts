import org.springframework.boot.gradle.tasks.bundling.BootJar

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

dependencies {
    api("org.opensearch.client:spring-data-opensearch-starter:1.5.3") {
        exclude("org.opensearch.client", "opensearch-rest-high-level-client")
    }
    implementation("jakarta.json:jakarta.json-api")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
}

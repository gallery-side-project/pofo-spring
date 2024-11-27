rootProject.name = "pofo-spring"

include(":pofo-api")
include(":pofo-common")
include(":pofo-domain")
include(":pofo-infra")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

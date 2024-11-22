rootProject.name = "pofo-spring"

include(":pofo-api")
include(":pofo-common")
include(":pofo-domain")
include(":pofo-elastic-search")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

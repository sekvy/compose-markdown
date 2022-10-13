
rootProject.name = "compose-markdown"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":test-app")
includeBuild("convention-plugins")

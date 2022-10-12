import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

plugins {
    // https://github.com/JetBrains/compose-jb/issues/2349
    // kotlin("multiplatform") version "1.7.20"
    // id("org.jetbrains.compose") version "1.2.0-beta03"

    kotlin("multiplatform") version "1.7.10"
    id("org.jetbrains.compose") version "1.2.0-rc02"
    id("org.jetbrains.dokka") version "1.7.10"
    id("convention.publication")
}

group = "se.sekvy"
version = "1.0.0-alpha01"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

// Support gradle version catalog for npm dependencies. Define npm dependencies as plugins.
fun KotlinDependencyHandler.npm(notation: Provider<PluginDependency>) : Dependency {
    return npm(notation.get().pluginId, notation.get().version.toString())
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        browser {
            testTask {
                testLogging.showStandardStreams = true
                useKarma {
                    useSafari()
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.bundles.commonmark)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm(libs.plugins.npm.commonmark.types))
                implementation(npm(libs.plugins.npm.commonmark.core))
            }
        }
        val jvmTest by getting
    }
}

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("convention.publication")
    alias(libs.plugins.benManesVersions)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.dokka)
}

group = "se.sekvy"
version = "1.0.2-alpha"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

// Support gradle version catalog for npm dependencies. Define npm dependencies as plugins.
fun KotlinDependencyHandler.npm(notation: Provider<PluginDependency>) : Dependency {
    return npm(notation.get().pluginId, notation.get().version.toString())
}

kotlin {
    jvmToolchain(11)
    jvm {
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) { browser {} }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)
                implementation(libs.jetbrains.markdown)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.runtime.compose)
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
        val wasmJsMain by getting
        val jvmTest by getting
    }
}

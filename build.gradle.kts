plugins {
    alias(libs.plugins.kover)
    alias(libs.plugins.ktlint)
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

subprojects {
    apply(plugin = "org.jetbrains.kotlinx.kover")
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        android.set(true)
        ignoreFailures.set(false)
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        }
        filter {
            exclude { element ->
                element.file.path.contains("/generated/") || element.file.path.contains("/build/")
            }
        }
    }
}

kover {
    reports {
        verify {
            rule {
                bound {
                    minValue = 60
                }
            }
        }
    }
}

dependencies {
    kover(project(":shared"))
    kover(project(":androidApp"))
}

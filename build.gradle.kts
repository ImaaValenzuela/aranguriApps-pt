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
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
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
                    // Temporarily set to 0% to avoid blocking CI until there is more test coverage
                    minValue = 0
                }
            }
        }
    }
}

dependencies {
    kover(project(":shared"))
    kover(project(":androidApp"))
}

evaluationDependsOn(":shared")
evaluationDependsOn(":androidApp")

tasks.register("prCheck") {
    group = "verification"
    description = "Ejecuta verificaciones locales de PR (formateo, tests, cobertura y lint)."

    dependsOn("ktlintFormat")
    dependsOn(":shared:allTests")
    dependsOn(":androidApp:test")
    dependsOn(":koverXmlReport")
    dependsOn(":androidApp:lint")
}

// Configurar el orden de ejecución
val ktlintFormatTask = tasks.named("ktlintFormat")
val sharedTestTask = project(":shared").tasks.named("allTests")
val androidAppTestTask = project(":androidApp").tasks.named("test")
val lintTask = project(":androidApp").tasks.named("lint")
val koverTask = tasks.named("koverXmlReport")

sharedTestTask.configure { mustRunAfter(ktlintFormatTask) }
androidAppTestTask.configure { mustRunAfter(ktlintFormatTask) }
lintTask.configure { mustRunAfter(ktlintFormatTask) }
koverTask.configure { mustRunAfter(sharedTestTask) }
koverTask.configure { mustRunAfter(androidAppTestTask) }

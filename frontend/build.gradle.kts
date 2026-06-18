plugins {
    kotlin("multiplatform")
}

kotlin {
    js(IR) {
        browser {
            binaries.executable()

            commonWebpackConfig {
                outputFileName = "app.js"
            }

            runTask {
                sourceMaps = false
            }
        }
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":shared"))
            }
        }
    }
}

tasks.register<Copy>("bundleFrontend") {
    dependsOn("jsBrowserDistribution")
    from(tasks.named("jsBrowserDistribution"))
    into("../build/dist/web")
}

tasks.register<Copy>("bundleDevelopmentFrontend") {
    dependsOn("jsBrowserDevelopmentExecutableDistribution")
    from(tasks.named("jsBrowserDevelopmentExecutableDistribution"))
    into("../build/dist/web")
}
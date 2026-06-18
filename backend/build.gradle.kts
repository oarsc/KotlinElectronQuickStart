plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    js(IR) {
        nodejs {
            binaries.executable()
        }
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":shared"))
//                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            }
        }
    }
}

tasks.register<Sync>("bundleDevelopmentBackend") {
    dependsOn("jsProductionExecutableCompileSync", "jsProcessResources")
    from(tasks.named("jsProductionExecutableCompileSync"))
    into("../build/dist/electron")
}

tasks.register<Exec>("bundleBackend") {
    dependsOn("jsProductionExecutableCompileSync", "bundleBackendResources")

    val filename = "app-backend.js"

    val compileTask = tasks.named("jsProductionExecutableCompileSync")
    val outputFile = layout.projectDirectory.file("../build/dist/electron/$filename")

    inputs.files(compileTask)
    outputs.file(outputFile)

    val inputFile = compileTask.get().outputs.files.asFileTree
        .matching { include("**/$filename") }
        .singleFile

    commandLine(
        "npx",
        "esbuild",
        inputFile.absolutePath,
        "--bundle",
        "--platform=node",
        "--minify",
        "--external:electron",
        "--outfile=${outputFile.asFile.absolutePath}"
    )
}

tasks.register<Sync>("bundleBackendResources") {
    dependsOn("jsProcessResources")
    from(tasks.named("jsProcessResources"))
    into("../build/dist/electron")
}

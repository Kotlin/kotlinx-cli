val kotlinVersion: String by project

plugins {
    kotlin("multiplatform") version "1.3.60-eap-76"
}

kotlin {
    /*infra {
        target("macosX64")
        target("linuxX64")
        target("mingwX64")
    }*/

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-common:$kotlinVersion")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlinVersion")
            }
        }
        val jvmMain by creating {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        // JVM-specific tests and their dependencies:
        val jvmTest by creating {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by creating {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        // JVM-specific tests and their dependencies:
        val jsTest by creating {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
            kotlin.srcDir("src/nativeMain/kotlin")
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }
    }

    jvm {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xuse-experimental=kotlinx.cli.ExperimentalCli")
                suppressWarnings = true
            }
        }
    }

    macosX64()
    linuxX64()
    mingwX64()

    configure(listOf(linuxX64(), macosX64(), mingwX64())) {
        compilations.all {
            kotlinOptions.freeCompilerArgs = listOf("-Xuse-experimental=kotlinx.cli.ExperimentalCli")
            kotlinOptions.suppressWarnings = true
        }
        compilations["main"].defaultSourceSet.dependsOn(sourceSets["nativeMain"])
        compilations["main"].defaultSourceSet.dependsOn(sourceSets["nativeTest"])
    }

    js {
        compilations.all {
            kotlinOptions {
                sourceMap = true
                moduleKind = "umd"
                metaInfo = true
                suppressWarnings = true
                freeCompilerArgs = listOf("-Xuse-experimental=kotlinx.cli.ExperimentalCli")
            }
        }
    }
}
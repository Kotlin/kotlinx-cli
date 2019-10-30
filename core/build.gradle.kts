plugins {
    kotlin("multiplatform")
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
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-common")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
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

    sourceSets.all {
        kotlin.setSrcDirs(listOf("$name/src"))
        languageSettings.useExperimentalAnnotation("kotlinx.cli.ExperimentalCli")
    }

    macosX64()
    linuxX64()
    mingwX64()

    configure(listOf(linuxX64(), macosX64(), mingwX64())) {
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
            }
        }
    }
}
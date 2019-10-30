plugins {
    kotlin("multiplatform")
}

kotlin {
    /*infra {
        target("macosX64")
        target("linuxX64")
        target("mingwX64")
    }*/

    macosX64()
    linuxX64()
    mingwX64()

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

    jvm()

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
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        // JVM-specific tests and their dependencies:
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        // JVM-specific tests and their dependencies:
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }
    }

    sourceSets.all {
        kotlin.setSrcDirs(listOf("$name/src"))
        resources.setSrcDirs(listOf("$name/resources"))
        languageSettings.useExperimentalAnnotation("kotlinx.cli.ExperimentalCli")
    }

    configure(listOf(linuxX64(), macosX64(), mingwX64())) {
        compilations["main"].defaultSourceSet.dependsOn(sourceSets["nativeMain"])
        compilations["main"].defaultSourceSet.dependsOn(sourceSets["nativeTest"])
    }
}
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
}


tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.allWarningsAsErrors = true
}

kotlin {
    infra {
        target("macosX64")
        target("macosArm64")
        target("linuxX64")
        target("mingwX64")
    }

    js {
        moduleName = "${rootProject.name}-${project.name}"
        nodejs {
        }
        compilations.all {
            kotlinOptions {
                sourceMap = true
                moduleKind = "umd"
                metaInfo = true
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
        // JS-specific tests and their dependencies:
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }

    sourceSets.all {
        kotlin.setSrcDirs(listOf("$name/src"))
        resources.setSrcDirs(listOf("$name/resources"))
        languageSettings.apply {
            useExperimentalAnnotation("kotlin.Experimental")
            useExperimentalAnnotation("kotlinx.cli.ExperimentalCli")
        }
    }
}
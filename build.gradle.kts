buildscript {
    repositories {
        mavenCentral()
    }
    val kotlinVersion: String by project
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

plugins {
    id("kotlinx.team.infra")
}

infra {
    teamcity {
        bintrayUser = "%env.BINTRAY_USER%"
        bintrayToken = "%env.BINTRAY_API_KEY%"
    }
    publishing {
        include(":kotlinx-cli")
        bintray {
            organization = "kotlin"
            repository = "kotlinx"
            library = "kotlinx.cli"
            username = findProperty("bintrayUser") as String?
            password = findProperty("bintrayApiKey") as String?
        }

        bintrayDev {
            organization = "kotlin"
            repository = "kotlin-dev"
            library = "kotlinx.cli"
            username = findProperty("bintrayUser") as String?
            password = findProperty("bintrayApiKey") as String?
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}
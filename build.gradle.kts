buildscript {
    repositories {
        mavenCentral()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.60-eap-76")
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
        maven("https://cache-redirector.jetbrains.com/jcenter")
        maven("https://dl.bintray.com/kotlin/kotlin-dev")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}
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
        libraryStagingRepoDescription = project.name
    }
    publishing {
        include(":kotlinx-cli")
        libraryRepoUrl = "https://github.com/Kotlin/kotlinx-cli"
        sonatype {}
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}
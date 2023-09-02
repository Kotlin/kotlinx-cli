/*
 * Copyright 2016-2020 JetBrains s.r.o.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

import jetbrains.buildServer.configs.kotlin.v2019_2.*

const val versionSuffixParameter = "versionSuffix"
const val teamcitySuffixParameter = "teamcitySuffix"
const val releaseVersionParameter = "releaseVersion"

const val bintrayUserName = "%env.BINTRAY_USER%"
const val bintrayToken = "%env.BINTRAY_API_KEY%"
const val libraryStagingRepoDescription = "Kotlinx-Cli-Library"

val platforms = Platform.values()
const val jdk = "JDK_18"

enum class Platform {
    Windows, LinuxX64, LinuxArm64, MacOSX64, MacosArm64;
}

fun Platform.nativeTaskPrefix(): String = when(this) {
    Platform.Windows -> "mingwX64"
    Platform.LinuxX64 -> "linuxX64"
    Platform.LinuxArm64 -> "linuxArm64"
    Platform.MacOSX64 -> "macosX64"
    Platform.MacosArm64 -> "macosArm64"

}
fun Platform.buildTypeName(): String = when (this) {
    Platform.Windows -> name
    Platform.LinuxX64 -> "Linux X64"
    Platform.LinuxArm64 -> "Linux Arm64"
    Platform.MacOSX64 -> "Mac OS X64"
    Platform.MacosArm64 -> "Mac OS Arm64"
}

fun Platform.expectedArch(): String? = when (this) {
    Platform.Windows -> null
    Platform.LinuxX64 -> "x64"
    Platform.LinuxArm64 -> "aarch64"
    Platform.MacOSX64 -> "x86_64"
    Platform.MacosArm64 -> "aarch64"
}

fun Platform.buildTypeId(): String = when(this) {
    Platform.MacosArm64 -> buildTypeName().replace(" ", "_")
    else -> osName()
}

fun Platform.osName(): String = buildTypeName().substringBefore(" ")

fun Platform.teamcityAgentName(): String = osName()


const val BUILD_CONFIGURE_VERSION_ID = "Build_Version"
const val BUILD_ALL_ID = "Build_All"
const val DEPLOY_CONFIGURE_VERSION_ID = "Deploy_Configure"
const val DEPLOY_PUBLISH_ID = "Deploy_Publish"

val BUILD_CREATE_STAGING_REPO_ABSOLUTE_ID = AbsoluteId("KotlinTools_CreateSonatypeStagingRepository")

class KnownBuilds(private val project: Project) {
    private fun buildWithId(id: String): BuildType {
        return project.buildTypes.single { it.id.toString().endsWith(id) }
    }

    val buildVersion: BuildType get() = buildWithId(BUILD_CONFIGURE_VERSION_ID)
    val buildAll: BuildType get() = buildWithId(BUILD_ALL_ID)
    fun buildOn(platform: Platform): BuildType = buildWithId("Build_${platform.buildTypeId()}")
    val deployVersion: BuildType get() = buildWithId(DEPLOY_CONFIGURE_VERSION_ID)
    val deployPublish: BuildType get() = buildWithId(DEPLOY_PUBLISH_ID)
    fun deployOn(platform: Platform): BuildType = buildWithId("Deploy_${platform.buildTypeId()}")
}

val Project.knownBuilds: KnownBuilds get() = KnownBuilds(this)


fun Project.buildType(name: String, platform: Platform, configure: BuildType.() -> Unit) = BuildType {
    // ID is prepended with Project ID, so don't repeat it here
    // ID should conform to identifier rules, so just letters, numbers and underscore
    id("${name}_${platform.buildTypeId()}")
    // Display name of the build configuration
    this.name = "$name (${platform.buildTypeName()})"

    requirements {
        contains("teamcity.agent.jvm.os.name", platform.teamcityAgentName())
        platform.expectedArch()?.let {
            contains("teamcity.agent.jvm.os.arch", it)
        }
    }

    params {
        // These parameters are needed for macOS agents to be compatible
        if (platform == Platform.MacOSX64) param("env.JDK_17", "")
        if (platform == Platform.MacosArm64) param("env.JDK_16", "")
    }

    commonConfigure()
    configure()
}.also { buildType(it) }


fun BuildType.commonConfigure() {
    requirements {
        noLessThan("teamcity.agent.hardware.memorySizeMb", "6144")
    }

    // Allow to fetch build status through API for badges
    allowExternalStatus = true

    // Configure VCS, by default use the same and only VCS root from which this configuration is fetched
    vcs {
        root(DslContext.settingsRoot)
        showDependenciesChanges = true
        checkoutMode = CheckoutMode.ON_AGENT
    }

    failureConditions {
        errorMessage = true
        nonZeroExitCode = true
        executionTimeoutMin = 120
    }

    features {
        feature {
            id = "perfmon"
            type = "perfmon"
        }
    }
}

fun BuildType.dependsOn(build: IdOwner, configure: Dependency.() -> Unit) =
        apply {
            dependencies.dependency(build, configure)
        }

fun BuildType.dependsOnSnapshot(build: IdOwner, onFailure: FailureAction = FailureAction.FAIL_TO_START, configure: SnapshotDependency.() -> Unit = {}) = apply {
    dependencies.dependency(build) {
        snapshot {
            configure()
            onDependencyFailure = onFailure
            onDependencyCancel = FailureAction.CANCEL
        }
    }
}

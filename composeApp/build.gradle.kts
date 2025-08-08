import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.ComposeHotRun
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.hotReload)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(compose.materialIconsExtended)
            implementation(libs.splitPaneDesktop)
            implementation(libs.haze)
            implementation(libs.hazeMaterials)
            implementation(libs.materialKolor)

            implementation(libs.kermit)
            implementation(libs.multiplatformSettings)

            implementation(libs.kotlinxCoroutinesSwing)

            implementation(libs.smali)
            implementation(libs.smaliDexlib2)
            implementation(libs.smaliBaksmali)
            implementation(libs.smaliUtil)

            implementation(libs.dex2jarNicoMexis)
            implementation(libs.jadx)
            implementation(libs.jadxSmaliInput)
            implementation(libs.cfr)
            implementation(libs.vineflower)

            implementation(libs.antlr4)

            implementation(libs.apksig)
            implementation(libs.ddmlib)

            implementation(libs.apktoolLib)

        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }

    }
}

val appVersion = "1.0.7a"

compose.desktop {
    application {
        mainClass = "MainKt"

        buildTypes.release.proguard {
            configurationFiles.from(
                project.file("proguard-rules.pro")
            )
            // TODO enable when ready.
            isEnabled.set(false)
        }

        jvmArgs("-Dapp.version=$appVersion")

        nativeDistributions {
            // add zipfs support for dex2jar
            modules(
                "jdk.zipfs",
            )
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "dalvikus"
            packageVersion = appVersion
            description = "Dalvikus reverse engineering tool"
            vendor = "Leonhard Kohl-Loerting"
            copyright = "© 2025 Leonhard Kohl-Loerting - License: GPL-3.0"
            licenseFile.set(project.file("../LICENSE"))

            linux {
                iconFile.set(project.file("desktopAppIcons/logo.png"))
                debPackageVersion = appVersion
                debMaintainer = "Leonhard Kohl-Loerting"
            }

            windows {
                iconFile.set(project.file("desktopAppIcons/logo.ico"))
                exePackageVersion = appVersion
                msiPackageVersion = appVersion

                menu = true
                shortcut = true

            }
            macOS {
                iconFile.set(project.file("desktopAppIcons/logo.icns"))
                bundleID = "me.lkl.dalvikus.desktopApp"
            }
        }
    }
}

//https://github.com/JetBrains/compose-hot-reload
composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}

tasks.withType<ComposeHotRun>().configureEach {
    mainClass.set("MainKt")
}

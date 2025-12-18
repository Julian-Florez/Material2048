import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        // Updated to fully qualified name after moving Main.kt to a package
        mainClass = "com.myg.material2048.desktop.MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Material2048"
            packageVersion = "1.0.0"
            
            // Fixes "Failed to launch JVM" by including all JRE modules
            includeAllModules = true
            
            windows {
                console = true // Enable console to see startup errors
            }
        }
    }
}

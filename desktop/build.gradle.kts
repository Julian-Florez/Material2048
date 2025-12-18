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
                implementation(compose.material3)
                implementation(compose.materialIconsExtended) // Added for extra icons
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
            
            // Set vendor name for Windows installer metadata
            vendor = "MaterialYou Games"
            
            // Fixes "Failed to launch JVM" by including all JRE modules
            includeAllModules = true
            
            windows {
                console = false // Disable console
                iconFile.set(project.file("src/desktopMain/resources/icon.ico"))
                // Ensure vendor is also set specifically for Windows if needed, though top-level usually works for MSI
                // Some packagers might look for specific windows properties.
                // For MSI, the 'vendor' property above is key.
            }
            
            linux {
                iconFile.set(project.file("../app/src/main/ic_launcher-playstore.png"))
            }
            
            macOS {
                iconFile.set(project.file("../app/src/main/ic_launcher-playstore.png")) // .icns is standard but PNG might work or fail gracefully
            }
        }
    }
}

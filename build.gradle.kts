import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    kotlin("multiplatform") version "1.9.20" // Updated to match Compose compiler requirement
    id("com.android.application") version "8.1.4"
    id("org.jetbrains.compose") version "1.5.3"
    id("maven-publish")
    id("org.jetbrains.kotlin.native.cocoapods") version "1.9.20"
}

// Apply iOS deployment tasks
apply(from = "ios-deploy.gradle")

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.korge.org/")  // Add KorGE repository
    maven { url = uri("https://jitpack.io") }
}

group = "com.example"
version = "1.0"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    cocoapods {
        summary = "KMP Image Manipulator Shared Module"
        homepage = "https://github.com/yourproject/kmp-image-proto"
        version = "1.0"
        ios.deploymentTarget = "14.0"
        framework {
            baseName = "shared"
            isStatic = false
        }
        podfile = project.file("iosApp/Podfile")
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDirs("com.example.imagemanipulator.shared")

            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("io.insert-koin:koin-core:3.4.0")
                // We'll handle image loading with platform-specific code instead
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.core:core-ktx:1.12.0")
                implementation(libs.androidx.activity.compose)
                implementation(compose.ui)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.uiTooling)
                implementation("io.ak1:drawbox:1.0.3")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            kotlin.srcDirs("com.example.imagemanipulator.ios")

            dependencies {
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
            }
        }
        
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "com.example.imagemanipulator.android"
    compileSdk = 34
    
    sourceSets {
        named("main") {
            manifest.srcFile("com.example.imagemanipulator.android/AndroidManifest.xml")
            java.srcDirs("com.example.imagemanipulator.android")
            kotlin.srcDirs("com.example.imagemanipulator.android")
            res.srcDirs("src/main/res")
            resources.srcDirs("src/main/resources")
        }
    }

    defaultConfig {
        applicationId = "com.example.imagemanipulator.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    
    // Disable lint checks for now
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

// Add Java toolchain configuration
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

compose {
    kotlinCompilerPlugin.set("1.5.3")
}

publishing {
    publications {
        // We'll enable publishing when needed
    }
}

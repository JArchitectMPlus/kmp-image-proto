// iOS deployment tasks
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

// Add tasks to deploy to iOS devices and simulators
tasks.register("iosDeployIphoneSimulator") {
    dependsOn("linkDebugFrameworkIosSimulatorArm64")
    doLast {
        // Get the simulator ID
        val simulatorId = getBootedSimulatorId()
        if (simulatorId.isEmpty()) {
            throw GradleException("No booted simulator found. Please start a simulator first.")
        }
        
        // Copy the framework to the expected location
        val frameworkSource = project.buildDir.resolve("bin/iosSimulatorArm64/debugFramework/shared.framework")
        val frameworkDest = project.buildDir.resolve("cocoapods/framework/shared.framework")
        
        // Create the destination directory if it doesn't exist
        frameworkDest.parentFile.mkdirs()
        
        // Copy the framework
        copy {
            from(frameworkSource)
            into(frameworkDest.parentFile)
        }
        
        // Generate the Xcode project using XcodeGen
        val iosAppDir = project.projectDir.resolve("iosApp")
        exec {
            workingDir = iosAppDir
            executable = "xcodegen"
            args = listOf("generate")
            isIgnoreExitValue = true
        }
        
        // Build the iOS app
        val xcodeProjectPath = iosAppDir.resolve("ImageManipulator.xcodeproj")
        if (xcodeProjectPath.exists()) {
            // Build for simulator
            exec {
                workingDir = iosAppDir
                executable = "xcodebuild"
                args = listOf(
                    "-project", "ImageManipulator.xcodeproj",
                    "-scheme", "ImageManipulator",
                    "-configuration", "Debug",
                    "-sdk", "iphonesimulator",
                    "-derivedDataPath", "${project.buildDir}/ios-derived-data",
                    "build"
                )
            }
            
            // Get the path to the built app
            val appPath = "${project.buildDir}/ios-derived-data/Build/Products/Debug-iphonesimulator/ImageManipulator.app"
            
            // Install and launch on simulator
            exec {
                executable = "xcrun"
                args = listOf("simctl", "install", simulatorId, appPath)
            }
            
            exec {
                executable = "xcrun"
                args = listOf("simctl", "launch", simulatorId, "com.example.ImageManipulator")
            }
            
            println("App is now running on the simulator")
        } else {
            println("Xcode project not found at $xcodeProjectPath")
            println("Please open the project in Xcode and run it manually")
        }
    }
}

// Helper function to get the ID of the currently running simulator
fun getBootedSimulatorId(): String {
    val simulatorsProcess = ProcessBuilder("xcrun", "simctl", "list", "devices").start()
    val simulatorsList = simulatorsProcess.inputStream.bufferedReader().readText()
    
    // Find the booted simulator
    val regex = ".*\\(([0-9A-F\\-]+)\\) \\(Booted\\)".toRegex()
    val matchResult = regex.find(simulatorsList)
    
    return matchResult?.groupValues?.get(1) ?: ""
}

// Task to deploy to a physical iPhone
tasks.register("iosDeployIphone") {
    dependsOn("linkDebugFrameworkIosArm64")
    doLast {
        println("To deploy to a physical iPhone:")
        println("1. Open the project in Xcode")
        println("2. Connect your iPhone")
        println("3. Select your device in the target dropdown")
        println("4. Click Run")
        println("Note: You need to have a valid Apple Developer account and proper signing configuration")
    }
}
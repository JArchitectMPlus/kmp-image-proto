 # Project Overview
 This project aims to develop a mobile image manipulation application using Kotlin Multiplatform, targeting both iOS and Android platforms. The core functionality involves a canvas where users can add, manipulate, and manage image and text layers. The shared logic for image manipulation is implemented in Kotlin Multiplatform, ensuring consistency and code reusability across platforms.

 ## Key Features

 -   Configurable canvas supporting 3:2 aspect ratio and 1:1 circular formats.
 -   Layer system for adding, managing, and manipulating multiple image and text layers.
 -   Image layers with scaling (10% to 190%), positioning, and 360-degree rotation.
 -   Text layers with scaling (10% to 190%), positioning, rotation, and text curving.
 -   Layer management controls for removing, reordering, and undo/redo.
 -   Export functionality to save the canvas as a flattened PNG with transparency and white background, and also as base64.

 ## Technical Implementation

 -   Kotlin Multiplatform for shared image manipulation logic.
 -   Native UI implementations for iOS and Android.
 -   Performant rendering with responsive design across various device dimensions.

 ## Canvas Requirements

 - Configurable canvas that supports both 3:2 aspect ratio and 1:1 circular formats
 - Implement a layer system that allows users to add, manage, and manipulate multiple image and text layers
 - Canvas should maintain transparency and export as a PNG with white background

 ## Layer Manipulation Features
 - **Image Layers**:
 - Support adding PNG and JPG images as layers
 - Allow scaling from 10% to 190% of original size
 - Enable positioning anywhere within the canvas boundaries
 - Implement 360-degree rotation functionality

 - **Text Layers**:
 - Enable adding text
 - Support scaling from 10% to 190%
 - Allow positioning anywhere within canvas boundaries
 - Enable 360-degree rotation
 - Implement text curving functionality (straight line to semi-circle)

 ## Export Functionality
 - On completion, allow saving the entire canvas as a flattened PNG
 - Maintain transparency with white background
 - Include functionality to export the image as base64


# Running the Project

## Running on iOS

To install and run the application on an iOS device or simulator:

1.  Ensure you have Xcode installed on a macOS machine.
2.  Connect your iOS device or have the iOS Simulator running.
3.  Execute the appropriate Gradle iOS run task from your project's root directory:

    *   For an iPhone device:
    *   ```bash
        ./gradlew iosDeployiPhone
        ```
    *   For an iPhone simulator:
    *  ```bash
        ./gradlew iosDeployiPhoneSimulator
        ```
       
4.  The application should launch on the connected device or simulator.

## Running on Android

To install and run the application on an Android device or emulator:

1.  Ensure you have Android Studio installed and set up.
2.  Connect your Android device or have the Android Emulator running.
3.  Execute the appropriate Gradle Android run task from your project's root directory:

    *   For an Android device:
    *   ```bash
        ./gradlew androidDeploy
        ```
    *   For an Android emulator:
    *   ```bash
        ./gradlew androidDeployEmulator
        ```
        
4.  The application should launch on the connected device or emulator.



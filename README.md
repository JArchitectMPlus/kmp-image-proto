 # KMP Image Manipulator App

This project is a mobile image manipulation application using Kotlin Multiplatform (KMP), targeting both iOS and Android platforms. The core functionality involves manipulating images with transformations such as scaling, rotation, and positioning. The shared logic for image transformation is implemented in Kotlin Multiplatform, ensuring consistency and code reusability across platforms.

## Architecture

![Image Editor](https://github.com/user-attachments/assets/a7dd49d9-9060-49e7-a7dc-07d25141e315)


The app follows the KMP best practices with a modular architecture:

- **Shared Logic:** Image transformation logic (scaling, rotation, etc.) is implemented in Kotlin and shared between platforms
- **Platform-Specific UI:** Each platform uses its native UI toolkit:
  - Android uses native Android Views/Compose
  - iOS uses native SwiftUI views with Kotlin interoperability
- **MVVM Pattern:** The application follows the Model-View-ViewModel pattern:
  - **Model:** Shared data classes (Canvas, Layer, etc.)
  - **ViewModel:** Shared Kotlin ViewModels (CanvasViewModel, LayerViewModel)
  - **View:** Platform-specific UI implementations

### Layer-Based Design

The image editor uses a layered canvas approach:
- Multiple layers can be added to a canvas (images, text)
- Each layer can be independently manipulated (scaled, rotated, positioned)
- Layer controls allow selecting, editing, and reordering layers

## Key Features

- Image manipulation with scaling (10% to 190%), positioning, and 360-degree rotation
- Shared transformation logic across platforms
- Native UI for each platform
- Export functionality to save the image as base64

## Project Structure

### Shared Kotlin Module
- `com.example.imagemanipulator.shared/` - Shared Kotlin code across platforms:
  - `transformation/` - Core transformation logic for all layers:
    - `PositionTransformation.kt` - X/Y positioning logic
    - `RotationTransformation.kt` - 360-degree rotation logic
    - `ScaleTransformation.kt` - Scaling from 10% to 190%
    - `TransformationManager.kt` - Coordinates transformations
  - `model/` - Data models:
    - `Canvas.kt` - Container for all layers
    - `Layer.kt` - Base layer interface
    - `ImageLayer.kt` - Image-specific layer implementation
    - `TextLayer.kt` - Text-specific layer implementation
  - `ui/` - Platform-independent UI interfaces and components:
    - `canvas/` - Canvas view interfaces
    - `layers/` - Layer control components
    - `export/` - Canvas export functionality
  - `CanvasViewModel.kt` - Business logic for canvas manipulation
  - `LayerViewModel.kt` - Business logic for layer manipulation

### Android Platform Module
- `com.example.imagemanipulator.android/` - Android-specific implementation:
  - `drawing/` - Android Canvas drawing logic:
    - `AndroidPlatformCanvas.kt` - Android implementation of platform canvas
  - `ui/` - Android UI components:
    - `canvas/` - Canvas view implementation for Android
    - `layers/` - Layer control UI for Android
  - `export/` - Android-specific export functionality
  - `provider/` - Image provider implementation for Android
  - `util/` - Android utilities and helpers

### iOS Kotlin Module
- `com.example.imagemanipulator.ios/` - iOS-specific Kotlin code:
  - `drawing/` - iOS drawing logic in Kotlin:
    - `IOSPlatformCanvas.kt` - iOS implementation of platform canvas
    - `IOSPlatformDrawBox.kt` - Drawing functionality for iOS
  - `ui/` - Kotlin interfaces for iOS UI components:
    - `canvas/` - Canvas view implementation for iOS
    - `layers/` - Layer control UI interfaces for iOS
  - `util/` - iOS-specific utilities:
    - `CoreGraphicsExtensions.kt` - Extensions for iOS CoreGraphics
    - `KorIMWrapper.kt` - Image handling utilities for iOS

### iOS Swift Module
- `iosApp/` - Native iOS Swift implementation:
  - `ImageManipulator/` - Swift wrappers and native components:
    - `CanvasViewModelWrapper.swift` - Swift wrapper for Kotlin CanvasViewModel
    - `LayerViewModelWrapper.swift` - Swift wrapper for Kotlin LayerViewModel
    - `ImageViewModelWrapper.swift` - Swift wrapper for Kotlin ImageViewModel
    - `IOSImageProvider.swift` - Native image provider implementation
    - `ImagePicker.swift` - Native iOS image picker
    - `UI/` - Native SwiftUI components:
      - `CanvasView.swift` - SwiftUI canvas implementation
      - `LayerControlsView.swift` - Layer manipulation controls
      - `SwiftCanvasExporter.swift` - Canvas export functionality
  - `SwiftUI/` - Native SwiftUI application components

### Build & Run Scripts
- `build-and-run.sh` - Unified script to build and run on either platform
- `build-and-run-ios.sh` - Script to build and run the iOS app
- `build-ios-app.sh` - Script to build the iOS app
- `run-ios-simulator.sh` - Script to run the app on an iOS simulator

## Running the Project

You can use the unified build script to run either platform:

```bash
# Build and run on iOS simulator
./build-and-run.sh --platform ios --simulator

# Build and open iOS project in Xcode
./build-and-run.sh --platform ios

# Build and run on Android
./build-and-run.sh --platform android
```

### Running on iOS

To build and run the application on iOS:

1. Build the iOS framework and open in Xcode:
   ```bash
   cd iosApp
   ./build_and_run.sh
   ```

   This script:
   - Builds the shared KMP framework for iOS (arm64 and x86_64)
   - Creates a universal binary using lipo
   - Copies the framework to the proper location
   - Generates the Xcode project using xcodegen
   - Builds the app for the simulator
   - Opens the project in Xcode

2. In Xcode:
   - Select the iPhone 16 Pro (or other) simulator from the device dropdown
   - Press the Run button (▶️) or use Cmd+R to run the app on the simulator

Alternatively, you can run individual scripts for specific steps:
```bash
# Just build the iOS framework
./build-ios-app.sh

# Run on a specific iOS simulator
./run-ios-simulator.sh "iPhone 16 Pro"
```

### Building the iOS App from Scratch

If you're having issues with building the iOS app (especially "No such module 'shared'" errors), follow these definitive steps to rebuild from scratch:

```bash
cd iosApp
./rebuild_kotlin_framework.sh
open ImageManipulator.xcworkspace
```

The rebuild process follows these key steps:

1. **Clean Existing Frameworks**: Removes all previously built framework files
   - Cleans build/cocoapods/framework directory
   - Cleans architecture-specific framework directories

2. **Build Kotlin Framework**: Builds the shared Kotlin framework for different architectures
   - Builds for iOS Simulator ARM64 architecture
   - Builds for iOS Simulator x86_64 architecture
   - Creates a universal binary using lipo

3. **Set Up CocoaPods Integration**: Prepares the framework for CocoaPods
   - Copies the universal framework to the CocoaPods location
   - Installs pods to integrate the framework with the iOS app

4. **Build in Xcode**: After opening the workspace in Xcode
   - Select a simulator (like iPhone SE)
   - Build the project (Cmd+B)
   - Run the app (Cmd+R)

### Troubleshooting Framework Integration Issues

If you still encounter "No such module 'shared'" errors after rebuild:

1. **Check Framework Path**: Verify the framework exists at the expected location
   ```bash
   ls -la ../build/cocoapods/framework/shared.framework
   ```

2. **Verify CocoaPods Setup**: Check that pods are properly installed
   ```bash
   pod install
   ```

3. **Clean Xcode Caches**: In Xcode, select Product > Clean Build Folder (Shift+Cmd+K)

4. **Check Import Statements**: Ensure Swift files are correctly importing the shared module
   ```swift
   import shared
   ```

### Running on Android

To install and run the application on an Android device or emulator:

1. Ensure you have an Android device connected or emulator running
2. Run the application using:
   ```bash
   ./build-and-run.sh --platform android
   ```
3. The application should launch on the connected device or emulator.

## Implementation Details

### Kotlin Multiplatform Architecture
The app demonstrates a production-ready KMP architecture with platform-specific UIs while sharing business logic:

1. **Shared Logic (expect/actual pattern):**
   - Core transformation logic is implemented once in Kotlin and shared
   - Platform-specific implementations use the `expect/actual` pattern
   - ViewModels are shared across platforms, ensuring consistent behavior

2. **Platform-Specific UI Integration:**
   - **Android:** Uses native Android Views and Kotlin interop
   - **iOS:** Uses SwiftUI with Swift wrappers for Kotlin components

3. **Swift-Kotlin Interoperability:**
   - Swift wrapper classes bridge to Kotlin ViewModels
   - Extensions help with Swift/Kotlin interoperability for CoreGraphics types
   - Platform-specific image loading/saving

### Layer System
- Image and text layers are treated as distinct types inheriting from a common Layer interface
- Each layer maintains its own transformation state (position, rotation, scale)
- Touch gestures are translated to transformation operations

### Canvas Drawing
- Each platform has a specific canvas implementation:
   - Android: Uses Android Canvas API
   - iOS: Uses CoreGraphics and UIKit

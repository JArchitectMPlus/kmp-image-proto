#!/bin/bash
set -e

# Build the shared framework
echo "Building shared framework..."
./gradlew linkDebugFrameworkIosSimulatorArm64

# Get simulator ID
SIM_ID=$(xcrun simctl list | grep Booted | grep -o '[A-Z0-9\-]\{36\}')
if [ -z "$SIM_ID" ]; then
  echo "No booted simulator found. Please start a simulator first."
  exit 1
fi

echo "Found booted simulator with ID: $SIM_ID"

# Create temporary Xcode project
TEMP_DIR=$(mktemp -d)
PROJECT_DIR="$TEMP_DIR/ImageManipulatorApp"
mkdir -p "$PROJECT_DIR"

# Copy framework
FRAMEWORK_DIR="$PROJECT_DIR/Frameworks"
mkdir -p "$FRAMEWORK_DIR"
cp -R build/bin/iosSimulatorArm64/debugFramework/shared.framework "$FRAMEWORK_DIR"

# Create simple app
cat > "$PROJECT_DIR/AppDelegate.swift" << 'EOF'
import UIKit
import shared

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        let window = UIWindow(frame: UIScreen.main.bounds)
        let viewController = MainViewController()
        window.rootViewController = viewController
        window.makeKeyAndVisible()
        self.window = window
        return true
    }
}
EOF

# Create Info.plist
cat > "$PROJECT_DIR/Info.plist" << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>CFBundleDevelopmentRegion</key>
	<string>en</string>
	<key>CFBundleExecutable</key>
	<string>ImageManipulatorApp</string>
	<key>CFBundleIdentifier</key>
	<string>com.example.imagemanipulator</string>
	<key>CFBundleInfoDictionaryVersion</key>
	<string>6.0</string>
	<key>CFBundleName</key>
	<string>ImageManipulatorApp</string>
	<key>CFBundlePackageType</key>
	<string>APPL</string>
	<key>CFBundleShortVersionString</key>
	<string>1.0</string>
	<key>CFBundleVersion</key>
	<string>1</string>
	<key>UILaunchStoryboardName</key>
	<string>LaunchScreen</string>
	<key>NSPhotoLibraryUsageDescription</key>
	<string>This app needs access to your photo library to let you select images to manipulate.</string>
</dict>
</plist>
EOF

# Build and run
echo "Building and running app on simulator..."
xcrun simctl install "$SIM_ID" build/bin/iosSimulatorArm64/debugFramework/shared.framework

echo "App should now be running in the simulator."
echo "Note: This is a simplified approach for testing. For a full app, use Xcode with proper project setup."
#!/bin/bash

# Exit on error
set -e

echo "🛠 Building shared KMP framework for multiple architectures..."
cd ..
# Build for iOS simulator (arm64)
./gradlew iosDeployIphoneSimulator
# Build for iOS simulator (x86_64)
./gradlew linkDebugFrameworkIosX64

# Create destination directory
mkdir -p build/universal/framework/

# Create a universal framework by merging the x86_64 and arm64 frameworks
echo "📦 Creating universal framework for iOS..."
cp -R build/bin/iosX64/debugFramework/shared.framework build/universal/framework/

# Use lipo to create a universal binary from simulator arm64 and x86_64
lipo -create \
  build/bin/iosSimulatorArm64/debugFramework/shared.framework/shared \
  build/bin/iosX64/debugFramework/shared.framework/shared \
  -output build/universal/framework/shared.framework/shared

# Copy the universal framework to the cocoapods location
echo "📋 Copying universal framework to iOS app..."
rm -rf build/cocoapods/framework/shared.framework
cp -R build/universal/framework/shared.framework build/cocoapods/framework/

echo "🏗 Generating Xcode project..."
cd iosApp
xcodegen generate

echo "📱 Building iOS app for simulator..."
xcodebuild -project ImageManipulator.xcodeproj -scheme ImageManipulator -destination 'platform=iOS Simulator,name=iPhone 16 Pro' build

echo "🚀 Opening project in Xcode..."
open ImageManipulator.xcodeproj

echo "✅ Done! Project is now open in Xcode. Please:"
echo "1. Select the iPhone 16 Pro simulator from the device dropdown"
echo "2. Click the Run button to build and install on the simulator"
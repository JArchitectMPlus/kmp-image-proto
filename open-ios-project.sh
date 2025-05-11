#!/bin/bash
set -e

# Build the iOS frameworks
./gradlew buildIos

# Navigate to the iOS app directory
cd iosApp

# Check if xcodegen is available
if command -v xcodegen &> /dev/null; then
    echo "Generating Xcode project with XcodeGen..."
    xcodegen generate
    
    if [ -d "ImageManipulator.xcodeproj" ]; then
        echo "Opening Xcode project..."
        open ImageManipulator.xcodeproj
    else
        echo "Opening iOS app folder in Finder..."
        open .
    fi
else
    echo "XcodeGen not found. Opening iOS app folder in Finder..."
    open .
fi

echo ""
echo "✅ iOS project is now ready for development!"
echo "To run the app on the simulator:"
echo "1. Select a simulator from the device dropdown in Xcode"
echo "2. Press the Run button (▶️) or use Cmd+R"
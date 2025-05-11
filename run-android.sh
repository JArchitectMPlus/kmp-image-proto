#!/bin/bash
set -e

echo "Building and installing Android app..."
./gradlew installDebug

echo ""
echo "✅ Android app has been installed!"
echo "If the app doesn't start automatically, please open it manually on your device."
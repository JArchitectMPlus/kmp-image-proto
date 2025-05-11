#!/bin/bash
set -e

# Run the Gradle task to deploy to the iOS simulator
./gradlew iosDeployIphoneSimulator

echo "✅ App should now be running on the iOS simulator"
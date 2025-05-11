#!/bin/bash
set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}Building iOS app framework...${NC}"
./gradlew linkDebugFrameworkIosSimulatorArm64

# Ensure the framework is in the expected location
mkdir -p build/cocoapods/framework
cp -R build/bin/iosSimulatorArm64/debugFramework/shared.framework build/cocoapods/framework/

echo -e "${GREEN}✓ Framework built successfully${NC}"
echo ""
echo -e "${YELLOW}To run the iOS app:${NC}"
echo "1. Open the project in Xcode:"
echo -e "   ${GREEN}cd iosApp && open .${NC}"
echo "2. Build and run the app in Xcode"
echo ""
echo -e "${YELLOW}If you have XcodeGen installed:${NC}"
echo -e "   ${GREEN}cd iosApp && xcodegen generate && open ImageManipulator.xcodeproj${NC}"
echo ""
echo -e "${YELLOW}Alternatively, you can use our open-ios-project.sh script:${NC}"
echo -e "   ${GREEN}./open-ios-project.sh${NC}"
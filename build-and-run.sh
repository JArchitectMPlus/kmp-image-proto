#!/bin/bash

# Build and run script for KMP Image Manipulator

# Define colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Parse arguments
PLATFORM=""
SIMULATOR=false

print_usage() {
  echo -e "${YELLOW}Usage: $0 [--platform ios|android] [--simulator]${NC}"
  echo
  echo "Options:"
  echo "  --platform ios|android    Specify platform to build (ios or android)"
  echo "  --simulator              Run iOS app in simulator (iOS only)"
  echo
  echo "Examples:"
  echo "  $0 --platform ios --simulator   # Build and run iOS app in simulator"
  echo "  $0 --platform android           # Build and run Android app on device"
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  key="$1"
  case $key in
    --platform)
      PLATFORM="$2"
      shift
      shift
      ;;
    --simulator)
      SIMULATOR=true
      shift
      ;;
    --help)
      print_usage
      exit 0
      ;;
    *)
      echo -e "${RED}Unknown option: $1${NC}"
      print_usage
      exit 1
      ;;
  esac
done

# Check if platform is provided
if [ -z "$PLATFORM" ]; then
  echo -e "${RED}Error: Platform not specified${NC}"
  print_usage
  exit 1
fi

# Define common build steps for shared code
build_shared() {
  echo -e "${GREEN}Building shared module...${NC}"
  ./gradlew :com.example.imagemanipulator.shared:build
}

# Build and run iOS app
build_ios() {
  echo -e "${GREEN}Building iOS module...${NC}"
  ./gradlew :com.example.imagemanipulator.ios:build
  
  # Create shared.framework for iOS
  echo -e "${GREEN}Creating shared framework...${NC}"
  ./gradlew :com.example.imagemanipulator.ios:packForXcode
  
  if [ "$SIMULATOR" = true ]; then
    # Install and run on simulator
    echo -e "${GREEN}Installing and running on iOS simulator...${NC}"
    ./install-ios-simulator.sh
  else
    # Open Xcode project
    echo -e "${GREEN}Opening Xcode project...${NC}"
    open ./iosApp/ImageManipulator.xcodeproj
  fi
}

# Build and run Android app
build_android() {
  echo -e "${GREEN}Building and installing Android app...${NC}"
  ./gradlew :com.example.imagemanipulator.android:installDebug
  
  # Launch the app
  echo -e "${GREEN}Launching Android app...${NC}"
  ./gradlew :com.example.imagemanipulator.android:run
}

# Main build process
build_shared

case $PLATFORM in
  ios)
    build_ios
    ;;
  android)
    build_android
    ;;
  *)
    echo -e "${RED}Error: Invalid platform '$PLATFORM'. Use 'ios' or 'android'${NC}"
    print_usage
    exit 1
    ;;
esac

echo -e "${GREEN}Build and run completed!${NC}"
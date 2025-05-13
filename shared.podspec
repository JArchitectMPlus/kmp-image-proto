Pod::Spec.new do |spec|
  spec.name         = 'shared'
  spec.version      = '1.0'
  spec.summary      = 'KMP Image Manipulator Shared Module'
  spec.homepage     = 'https://github.com/yourproject/kmp-image-proto'
  spec.source       = { :git => "Not Published", :tag => "Cocoapods/#{spec.name}/#{spec.version}" }
  spec.authors      = { 'Your Name' => 'your.email@example.com' }
  spec.license      = { :type => 'MIT', :text => 'Copyright (c) 2023 Your Name' }
  spec.ios.deployment_target = '14.0'

  # Only include headers from the framework
  spec.source_files = 'build/cocoapods/framework/shared.framework/Headers/*.h'
  
  spec.public_header_files = 'build/cocoapods/framework/shared.framework/Headers/*.h'
  spec.vendored_frameworks = 'build/cocoapods/framework/shared.framework'

  # Ensure the Swift files are not processed by CocoaPods
  spec.preserve_paths = 'iosApp/ImageManipulator/*'
  
  # Use the CocoaPods module map
  spec.module_map = 'build/cocoapods/framework/shared.framework/Modules/module.modulemap'

  spec.pod_target_xcconfig = {
    'KOTLIN_PROJECT_PATH' => '',
    'PRODUCT_MODULE_NAME' => 'shared',
    'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64',
    'FRAMEWORK_SEARCH_PATHS' => '$(inherited) "${PODS_ROOT}/../../build/cocoapods/framework"'
  }
  
  spec.user_target_xcconfig = {
    'FRAMEWORK_SEARCH_PATHS' => '$(inherited) "${PODS_ROOT}/../../build/cocoapods/framework"'
  }

  spec.script_phase = {
    :name => 'Build Kotlin/Native shared module',
    :execution_position => :before_compile,
    :shell_path => '/bin/sh',
    :script => <<-SCRIPT
      set -ev
      REPO_ROOT="$PODS_TARGET_SRCROOT"
      "$REPO_ROOT/gradlew" -p "$REPO_ROOT" :iosDeployIphoneSimulator
    SCRIPT
  }
end
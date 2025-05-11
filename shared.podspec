Pod::Spec.new do |spec|
  spec.name         = 'shared'
  spec.version      = '1.0'
  spec.summary      = 'KMP Image Manipulator Shared Module'
  spec.homepage     = 'https://github.com/yourproject/kmp-image-proto'
  spec.source       = { :git => "Not Published", :tag => "Cocoapods/#{spec.name}/#{spec.version}" }
  spec.ios.deployment_target = '14.0'
  
  spec.source_files = 'build/cocoapods/framework/shared.framework/Headers/*.h'
  spec.public_header_files = 'build/cocoapods/framework/shared.framework/Headers/*.h'
  spec.vendored_frameworks = 'build/cocoapods/framework/shared.framework'
  
  spec.pod_target_xcconfig = {
    'KOTLIN_PROJECT_PATH' => '',
    'PRODUCT_MODULE_NAME' => 'shared',
  }
end
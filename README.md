 # Project Overview
 This project aims to develop a mobile image manipulation application using Kotlin Multiplatform, targeting both iOS and Android platforms. The core functionality involves a canvas where users can add, manipulate, and manage image and text layers. The shared logic for image manipulation is implemented in Kotlin Multiplatform, ensuring consistency and code reusability across platforms.

 ## Key Features

 -   Configurable canvas supporting 3:2 aspect ratio and 1:1 circular formats.
 -   Layer system for adding, managing, and manipulating multiple image and text layers.
 -   Image layers with scaling (10% to 190%), positioning, and 360-degree rotation.
 -   Text layers with scaling (10% to 190%), positioning, rotation, and text curving.
 -   Layer management controls for removing, reordering, and undo/redo.
 -   Export functionality to save the canvas as a flattened PNG with transparency and white background, and also as base64.

 ## Technical Implementation

 -   Kotlin Multiplatform for shared image manipulation logic.
 -   Native UI implementations for iOS and Android.
 -   Performant rendering with responsive design across various device dimensions.

 ## Canvas Requirements

 - Configurable canvas that supports both 3:2 aspect ratio and 1:1 circular formats
 - Implement a layer system that allows users to add, manage, and manipulate multiple image and text layers
 - Canvas should maintain transparency and export as a PNG with white background

 ## Layer Manipulation Features
 - **Image Layers**:
 - Support adding PNG and JPG images as layers
 - Allow scaling from 10% to 190% of original size
 - Enable positioning anywhere within the canvas boundaries
 - Implement 360-degree rotation functionality

 - **Text Layers**:
 - Enable adding text with customizable properties
 - Include placeholder for 14 colors and 6 fonts (to be implemented later)
 - Support scaling from 10% to 190%
 - Allow positioning anywhere within canvas boundaries
 - Enable 360-degree rotation
 - Implement text curving functionality (straight line to semi-circle)

 ## Layer Management System
 - Create layer management controls that allow:
 - Removing individual layers
 - Reordering layers (moving forward/backward in z-index)
 - Implementing undo/redo functionality with layer history tracking

 ## Export Functionality
 - On completion, allow saving the entire canvas as a flattened PNG
 - Maintain transparency with white background
 - Include functionality to export the image as base64

 ## Technical Implementation Notes
 - Use Kotlin Multiplatform to share image manipulation logic between iOS and Android
 - Create native UI implementations for each platform that interact with the shared module
 - Ensure performant rendering even with multiple layers and transformations
 - Implement a responsive design that works across various device dimensions

Get started by customizing your environment (defined in the .idx/dev.nix file) with the tools and IDE extensions you'll need for your project!

Learn more at https://firebase.google.com/docs/studio/customize-workspace
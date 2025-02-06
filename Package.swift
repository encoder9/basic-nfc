// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "BasicNfc",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "BasicNfc",
            targets: ["BasicNFCPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "BasicNFCPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/BasicNFCPlugin"),
        .testTarget(
            name: "BasicNFCPluginTests",
            dependencies: ["BasicNFCPlugin"],
            path: "ios/Tests/BasicNFCPluginTests")
    ]
)
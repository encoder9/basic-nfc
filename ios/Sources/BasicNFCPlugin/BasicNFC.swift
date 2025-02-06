import Foundation

@objc public class BasicNFC: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}

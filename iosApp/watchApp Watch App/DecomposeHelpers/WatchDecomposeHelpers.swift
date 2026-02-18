import SwiftUI
import Combine
import Shared

final class ObservableValue<T: AnyObject>: ObservableObject {
    @Published
    var value: T

    private var cancellation: Cancellation?

    init(_ value: Value<T>) {
        self.value = value.value
        self.cancellation = value.subscribe { [weak self] value in
            self?.value = value
        }
    }

    deinit {
        cancellation?.cancel()
    }
}

@propertyWrapper
struct StateValue<T: AnyObject>: DynamicProperty {
    @ObservedObject
    private var obj: ObservableValue<T>

    var wrappedValue: T {
        obj.value
    }

    init(_ value: Value<T>) {
        obj = ObservableValue(value)
    }
}

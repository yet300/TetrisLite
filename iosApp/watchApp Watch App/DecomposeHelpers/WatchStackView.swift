import SwiftUI
import Shared

struct WatchStackView<T: AnyObject, Content: View>: View {
    @StateValue
    var stackValue: ChildStack<AnyObject, T>

    var onBack: (_ toIndex: Int32) -> Void

    @ViewBuilder
    var childContent: (T) -> Content

    private var stack: [Child<AnyObject, T>] {
        stackValue.items
    }

    var body: some View {
        NavigationStack(
            path: Binding(
                get: { stack.dropFirst() },
                set: { updatedPath in onBack(Int32(updatedPath.count)) }
            )
        ) {
            if let first = stack.first?.instance {
                childContent(first)
                    .navigationDestination(for: Child<AnyObject, T>.self) { child in
                        if let instance = child.instance {
                            childContent(instance)
                        } else {
                            EmptyView()
                        }
                    }
            }
        }
    }
}

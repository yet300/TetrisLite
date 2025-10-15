//
//  StackView.swift
//  iosApp
//
//  Created by Ruslan Yet on 08.10.25.
//


import SwiftUI
#if os(iOS)
import UIKit
#endif
import Shared

struct StackView<T: AnyObject, Content: View>: View {
    @StateValue
    var stackValue: ChildStack<AnyObject, T>

    var getTitle: (T) -> String
    var onBack: (_ toIndex: Int32) -> Void
    
    @ViewBuilder
    var childContent: (T) -> Content
    
    private var stack: [Child<AnyObject, T>] { stackValue.items }

    var body: some View {
        if #available(iOS 16.1, macOS 13.0, *) {
            NavigationStack(
                path: Binding(
                    get: { stack.dropFirst() },
                    set: { updatedPath in onBack(Int32(updatedPath.count)) }
                )
            ) {
                if let first = stack.first?.instance {
                    childContent(first)
                        .navigationDestination(for: Child<AnyObject, T>.self) {
                            childContent($0.instance!)
                        }
                }
            }
        } else {
            #if os(iOS)
            StackInteropView(
                components: stack.map { $0.instance! },
                getTitle: getTitle,
                onBack: onBack,
                childContent: childContent
            )
            #else
            if let first = stack.first?.instance {
                childContent(first)
            }
            #endif
        }
    }
}

#if os(iOS)

private struct StackInteropView<T: AnyObject, Content: View>: UIViewControllerRepresentable {
    var components: [T]
    var getTitle: (T) -> String
    var onBack: (_ toIndex: Int32) -> Void
    var childContent: (T) -> Content
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    func makeUIViewController(context: Context) -> UINavigationController {
        context.coordinator.syncChanges(self)
        let navigationController = UINavigationController(
            rootViewController: context.coordinator.viewControllers.first!)
        
        return navigationController
    }
    
    func updateUIViewController(_ navigationController: UINavigationController, context: Context) {
        context.coordinator.syncChanges(self)
        navigationController.setViewControllers(context.coordinator.viewControllers, animated: true)
    }
    
    private func createViewController(_ component: T, _ coordinator: Coordinator) -> NavigationItemHostingController {
        let controller = NavigationItemHostingController(rootView: childContent(component))
        controller.coordinator = coordinator
        controller.component = component
        controller.onBack = onBack
        controller.navigationItem.title = getTitle(component)
        return controller
    }
    
    class Coordinator: NSObject {
        var parent: StackInteropView<T, Content>
        var viewControllers = [NavigationItemHostingController]()
        var preservedComponents = [T]()
        
        init(_ parent: StackInteropView<T, Content>) {
            self.parent = parent
        }
        
        func syncChanges(_ parent: StackInteropView<T, Content>) {
            self.parent = parent
            let count = max(preservedComponents.count, parent.components.count)
            
            for i in 0..<count {
                if (i >= parent.components.count) {
                    viewControllers.removeLast()
                } else if (i >= preservedComponents.count) {
                    viewControllers.append(parent.createViewController(parent.components[i], self))
                } else if (parent.components[i] !== preservedComponents[i]) {
                    viewControllers[i] = parent.createViewController(parent.components[i], self)
                }
            }
            
            preservedComponents = parent.components
        }
    }
    
    class NavigationItemHostingController: UIHostingController<Content> {
        fileprivate(set) weak var coordinator: Coordinator?
        fileprivate(set) var component: T?
        fileprivate(set) var onBack: ((_ toIndex: Int32) -> Void)?
        
        override func viewDidAppear(_ animated: Bool) {
            super.viewDidAppear(animated)
            
            guard let components = coordinator?.preservedComponents else { return }
            guard let index = components.firstIndex(where: { $0 === component }) else { return }
            
            if (index < components.count - 1) {
                onBack?(Int32(index))
            }
        }
    }
}
#endif

// stubs for XCode < 14:
#if compiler(<5.7)
private struct NavigationStack<Path, Root>: View {
    var path: Path
    @ViewBuilder var root: () -> Root
    var body: some View {
        EmptyView()
    }
}

private extension View {
    public func navigationDestination<D, C>(for data: D.Type, @ViewBuilder destination: @escaping (D) -> C) -> some View where D: Hashable, C: View {
        EmptyView()
    }
}
#endif

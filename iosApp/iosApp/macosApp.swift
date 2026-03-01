#if os(macOS)

import SwiftUI
import Shared

@main
struct macosApp: App {
    private let root: RootComponent

    init() {
        let appGraph = NativeAppGraphKt.createNativeAppGraph()

        self.root = RootComponentFactoryKt.createRootComponent(
            componentContext: DefaultComponentContext(
                lifecycle: LifecycleRegistryKt.LifecycleRegistry(),
                stateKeeper: nil,
                instanceKeeper: nil,
                backHandler: nil
            ),
            graph: appGraph
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView(rootComponent: root)
                .frame(minWidth: 800, minHeight: 600)
        }
        .windowStyle(.hiddenTitleBar)
        .windowResizability(.contentSize)
    }
}

#endif

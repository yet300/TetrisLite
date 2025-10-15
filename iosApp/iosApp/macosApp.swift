#if os(macOS)

import SwiftUI
import Shared

@main
struct macosApp: App {
    private let root: RootComponent

    init() {
        InitKoinKt.doInitKoin()
        

        self.root = DefaultRootComponent(
            componentContext: DefaultComponentContext(
                lifecycle: LifecycleRegistryKt.LifecycleRegistry(),
                stateKeeper: nil,
                instanceKeeper: nil,
                backHandler: nil
            )
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView(rootComponent: root)
        }
    }
}

#endif

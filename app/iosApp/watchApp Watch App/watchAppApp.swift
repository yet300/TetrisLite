//
//  watchAppApp.swift
//  watchApp Watch App
//
//  Created by Ruslan Gadzhiev on 11.01.26.
//

import SwiftUI
import Shared

@main
struct watchApp_Watch_AppApp: App {
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
        }
    }
}

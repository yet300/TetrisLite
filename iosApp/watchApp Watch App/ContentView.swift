//
//  ContentView.swift
//  watchApp Watch App
//
//  Created by Ruslan Gadzhiev on 11.01.26.
//

import SwiftUI
import Shared

struct ContentView: View {
    let rootComponent: RootComponent

    var body: some View {
        WatchRootView(rootComponent: rootComponent)
    }
}

#Preview {
    ContentView(rootComponent: PreviewRootComponent())
}

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

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(rootComponent: PreviewRootComponent())
    }
}

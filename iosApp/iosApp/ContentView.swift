import SwiftUI
import Shared

struct ContentView: View {
    let rootComponent: RootComponent
    
    var body: some View {
        RootView(rootComponent)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(rootComponent: PreviewRootComponent())
    }
}

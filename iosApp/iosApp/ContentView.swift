import SwiftUI
import Shared

struct ContentView: View {
    let rootComponent: RootComponent

    @Environment(\.horizontalSizeClass) var horizontalSizeClass
    @Environment(\.verticalSizeClass) var verticalSizeClass
    
    var body: some View {
        GeometryReader { geometry in
            RootView(rootComponent)
                .frame(width: geometry.size.width, height: geometry.size.height)
        }
        .ignoresSafeArea(.all, edges: .all)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(rootComponent: PreviewRootComponent())
    }
}

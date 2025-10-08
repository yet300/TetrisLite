import SwiftUI
import Shared

struct RootView: View {
    private let root: RootComponent
    
    init(_ root: RootComponent) {
        self.root = root
    }
        
    var body: some View {
        StackView(
            stackValue: StateValue(root.childStack),
            getTitle: { _ in "Heh" },
            onBack: { _ in
                root.onBackClicked()
            }
        ) { child in
            childView(for: child)
        }
        
    }
    
}

@ViewBuilder
private func childView(for child: RootComponentChild) -> some View {
    switch child {
    case let child as HomeChild:
        HomeView(child.component)
    case let child as GameChild:
        GameView(child.component)
    default:
        EmptyView()
    }
}

private typealias HomeChild = RootComponentChild.Home
private typealias GameChild = RootComponentChild.Game

struct RootView_Previews: PreviewProvider {
    static var previews: some View {
        RootView(PreviewRootComponent())
    }
}

import SwiftUI
import Shared

struct RootView: View {
    private let root: RootComponent

    @Environment(\.horizontalSizeClass) var horizontalSizeClass
    @Environment(\.verticalSizeClass) var verticalSizeClass
    
    init(_ root: RootComponent) {
        self.root = root
    }

    var isLandscape: Bool {
        horizontalSizeClass == .regular && verticalSizeClass == .compact
    }

    var isIPad: Bool {
        horizontalSizeClass == .regular && verticalSizeClass == .regular
    }
        
    var body: some View {
        GeometryReader { geometry in
            StackView(
                stackValue: StateValue(root.childStack),
                getTitle: { _ in "Heh" },
                onBack: { _ in
                    root.onBackClicked()
                }
            ) { child in
                childView(for: child)
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
        }
        .ignoresSafeArea(.all, edges: .all)
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

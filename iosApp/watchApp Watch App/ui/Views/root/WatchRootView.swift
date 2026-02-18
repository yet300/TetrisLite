import SwiftUI
import Shared

struct WatchRootView: View {
    private let rootComponent: RootComponent

    init(rootComponent: RootComponent) {
        self.rootComponent = rootComponent
    }

    var body: some View {
        WatchStackView(
            stackValue: StateValue(rootComponent.childStack),
            onBack: { _ in rootComponent.onBackClicked() }
        ) { child in
            ZStack {
                WatchBackground()
                watchChildView(for: child)
            }
            .tint(WatchPalette.accent)
        }
    }
}

@ViewBuilder
private func watchChildView(for child: RootComponentChild) -> some View {
    switch child {
    case let child as RootComponentChild.Home:
        WatchHomeView(child.component)
    case let child as RootComponentChild.Game:
        WatchGameView(child.component)
    default:
        EmptyView()
    }
}

#Preview {
    WatchRootView(rootComponent: PreviewRootComponent())
}

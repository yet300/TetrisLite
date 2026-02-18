import SwiftUI
import Shared

struct WatchHomeView: View {
    private let component: HomeComponent

    @StateValue
    private var model: HomeComponentModel

    @StateValue
    private var bottomSheetSlot: ChildSlot<AnyObject, HomeComponentBottomSheetChild>

    init(_ component: HomeComponent) {
        self.component = component
        _model = StateValue(component.model)
        _bottomSheetSlot = StateValue(component.childBottomSheetNavigation)
    }

    var body: some View {
        NavigationStack {
            ZStack {
                WatchBackground()

                ScrollView {
                    VStack(spacing: 12) {
                        // Title
                        Text(Strings.appTitle)
                            .font(.system(.title3, design: .rounded))
                            .fontWeight(.bold)
                            .foregroundStyle(WatchPalette.accent)
                            .padding(.top, 4)

                        if let content = model as? HomeComponentModelContent {
                            VStack(spacing: 8) {

                                // Difficulty Selector
                                WatchDifficultySelector(difficulty: Binding(
                                    get: { content.settings.difficulty },
                                    set: { component.onDifficultyChanged(difficulty: $0) }
                                ))

                                // Play / Resume
                                let isResume = content.hasSavedGame
                                WatchButton(
                                    title: isResume ? Strings.resumeGame : Strings.startNewGame,
                                    systemImage: "play.fill",
                                    action: { isResume ? component.onResumeGame() : component.onStartNewGame() },
                                    backgroundColor: WatchPalette.accent.opacity(0.15),
                                    foregroundColor: .white,
                                    iconColor: WatchPalette.accent
                                )

                                if isResume {
                                    WatchButton(
                                        title: Strings.startNewGame,
                                        systemImage: "plus.circle.fill",
                                        action: { component.onStartNewGame() }
                                    )
                                }

                                HStack(spacing: 16) {
                                    WatchCircleButton(
                                        systemImage: "clock.fill",
                                        action: { component.onOpenHistory() },
                                        size: 52,
                                        iconSize: 22
                                    )

                                    WatchCircleButton(
                                        systemImage: "gearshape.fill",
                                        action: { component.onOpenSettings() },
                                        size: 52,
                                        iconSize: 22
                                    )
                                }
                                .padding(.top, 4)
                            }
                            .padding(.top, 8)

                            Spacer(minLength: 4)

                        } else {
                            ProgressView()
                                .padding(.top, 20)
                        }
                    }
                    .padding(.horizontal)
                }
            }
            .navigationTitle(Strings.appTitle)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar(.hidden)
            .sheet(item: Binding<WatchSheetItem?>(
                get: {
                    if let child = bottomSheetSlot.child?.instance {
                        return WatchSheetItem(child: child)
                    }
                    return nil
                },
                set: { item in
                    if item == nil {
                        component.onDismissBottomSheet()
                    }
                }
            )) { sheetItem in
                WatchSheetView(child: sheetItem.child)
            }
        }
    }
}

private struct WatchSheetView: View {
    let child: HomeComponentBottomSheetChild

    var body: some View {
        switch child {
        case let child as HomeComponentBottomSheetChildHistoryChild:
            WatchHistoryView(child.component)
        case let child as HomeComponentBottomSheetChildSettingsChild:
            WatchSettingsView(child.component)
        default:
            EmptyView()
        }
    }
}

private struct WatchSheetItem: Identifiable {
    let child: HomeComponentBottomSheetChild
    let id: String

    init(child: HomeComponentBottomSheetChild) {
        self.child = child
        switch child {
        case is HomeComponentBottomSheetChildHistoryChild:
            self.id = "history"
        case is HomeComponentBottomSheetChildSettingsChild:
            self.id = "settings"
        default:
            self.id = "sheet-\(ObjectIdentifier(child as AnyObject).hashValue)"
        }
    }
}

#Preview {
    WatchHomeView(PreviewHomeComponent())
}

import SwiftUI
import Shared

struct HomeView: View {
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
        NavigationView {
            ZStack {
                // Background gradient
                LinearGradient(
                    colors: [.blue.opacity(0.3), .purple.opacity(0.3)],
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                .ignoresSafeArea()
                
                switch model {
                case is HomeComponentModelLoading:
                    ProgressView()
                    
                case let content as HomeComponentModelContent:
                    VStack(spacing: 32) {
                        Spacer()
                        
                        // Title
                        Text("Tetris Lite")
                            .font(.system(size: 48, weight: .bold, design: .rounded))
                            .foregroundStyle(
                                LinearGradient(
                                    colors: [.blue, .purple],
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                            )
                        
                        Spacer()
                        
                        // Difficulty Selector
                        DifficultySelector(
                            selectedDifficulty: Binding(
                                get: { content.settings.difficulty },
                                set: { newDifficulty in
                                    component.onDifficultyChanged(difficulty: newDifficulty)
                                }
                            ),
                            onSelect: { difficulty in
                                component.onDifficultyChanged(difficulty: difficulty)
                            }
                        )
                        
                        // Action Buttons
                        VStack(spacing: 16) {
                            GlassButton(
                                title: "Start New Game",
                                icon: "play.fill",
                                action: { component.onStartNewGame() }
                            )
                            
                            if content.hasSavedGame {
                                GlassButton(
                                    title: "Resume Game",
                                    icon: "arrow.clockwise",
                                    style: .secondary,
                                    action: { component.onResumeGame() }
                                )
                            }
                        }
                        .padding(.horizontal, 32)
                        
                        Spacer()
                    }
                    
                default:
                    EmptyView()
                }
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button {
                        component.onOpenHistory()
                    } label: {
                        Image(systemName: "clock.fill")
                            .foregroundColor(.primary)
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button {
                        component.onOpenSettings()
                    } label: {
                        Image(systemName: "gearshape.fill")
                            .foregroundColor(.primary)
                    }
                }
            }
            .sheet(item: Binding<SheetItem?>(
                get: {
                    if let child = bottomSheetSlot.child?.instance {
                        return SheetItem(child: child)
                    } else {
                        return nil
                    }
                },
                set: { item in
                    if item == nil {
                        component.onDismissBottomSheet()
                    }
                }
            )) { sheetItem in
                // Разворачиваем обратно
                BottomSheetView(child: sheetItem.child)
            }
        }
    }
}

private struct BottomSheetView: View {
    let child: HomeComponentBottomSheetChild
    
    var body: some View {
        switch child {
        case let child as HistoryChild:
            HistoryView(child.component)
        case let child as SettingsChild:
            SettingsView(child.component)
        default:
            EmptyView()
        }
    }
}

struct SheetItem: Identifiable {
    let child: HomeComponentBottomSheetChild
    
    var id: String {
        switch child {
        case is HomeComponentBottomSheetChildHistoryChild:
            return "history"
        case is HomeComponentBottomSheetChildSettingsChild:
            return "settings"
        default:
            return UUID().uuidString
        }
    }
}

struct DifficultySelector: View {
    @Binding var selectedDifficulty: Difficulty
    let onSelect: (Difficulty) -> Void
    
    var body: some View {
        VStack(alignment: .center, spacing: 12) {
            Text("Difficulty")
                .font(.headline)
                .foregroundColor(.secondary)

            Picker("Difficulty", selection: $selectedDifficulty) {
                ForEach([Difficulty.easy, Difficulty.normal, Difficulty.hard], id: \.self) { difficulty in
                    Text(difficulty.name.capitalized).tag(difficulty)
                }
            }
            .pickerStyle(.segmented)
            .controlSize(.large)
        }
        .padding(.horizontal, 32)
    }
}

struct GlassButton: View {
    let title: String
    let icon: String
    var style: ButtonStyle = .primary
    let action: () -> Void
    
    enum ButtonStyle {
        case primary
        case secondary
    }
    
    var body: some View {
        Button(action: action) {
            HStack {
                Image(systemName: icon)
                Text(title)
                    .font(.headline)
            }
            .foregroundColor(style == .primary ? .white : .primary)
            .padding()
            .frame(maxWidth: .infinity)
            .background(
                ZStack {
                    if style == .primary {
                        LinearGradient(
                            colors: [.blue.opacity(0.5), .purple.opacity(0.4)],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )
                    }
                    LinearGradient(
                        colors: [.white.opacity(0.25), .white.opacity(0.0)],
                        startPoint: .topLeading,
                        endPoint: .bottom
                    )
                }
            )
            .overlay(
                RoundedRectangle(cornerRadius: 24)
                    .stroke(
                        LinearGradient(
                            colors: [.white.opacity(0.4), .white.opacity(0.1)],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        ),
                        lineWidth: 2
                    )
            )
            .clipShape(RoundedRectangle(cornerRadius: 24))
            .shadow(color: .black.opacity(0.2), radius: 10, x: 0, y: 5)
        }
    }
}

private typealias HistoryChild = HomeComponentBottomSheetChildHistoryChild
private typealias SettingsChild = HomeComponentBottomSheetChildSettingsChild

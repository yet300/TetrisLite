import SwiftUI
import Shared

struct GameView: View {
    private let component: GameComponent
    
    @StateValue
    private var model: GameComponentModel
    
    @StateValue
    private var dialog: ChildSlot<AnyObject, GameComponentDialogChild>
    
    @StateValue
    private var sheet: ChildSlot<AnyObject, GameComponentSheetChild>
    
    @Environment(\.dismiss) private var dismiss
    @Environment(\.colorScheme) var colorScheme

    private var textColor: Color {
        colorScheme == .dark ? Color.green : Color(red: 0, green: 0.5, blue: 0)
    }
    
    @State private var lastDragTranslation: CGSize = .zero
    @State private var didStartDragging = false

    @Environment(\.horizontalSizeClass) var horizontalSizeClass
    @Environment(\.verticalSizeClass) var verticalSizeClass

    var isIPadOrLandscape: Bool {
        horizontalSizeClass == .regular
    }
    
    init(_ component: GameComponent) {
        self.component = component
        _model = StateValue(component.model)
        _dialog = StateValue(component.childSlot)
        _sheet = StateValue(component.sheetSlot)
    }
    
    var body: some View {
        ZStack {
            if model.isLoading {
                ProgressView()
            } else if let gameState = model.gameState {
                HStack(spacing: isIPadOrLandscape ? 32 : 0) {
                    if isIPadOrLandscape {
                        Spacer()
                    }

                    VStack(spacing: 16) {
                        // Top Bar
                        HStack {
                            Button {
                                component.onPause()
                            } label: {
                                Image(systemName: "pause.fill")
                                    .foregroundColor(textColor)
                                    .frame(width: 32, height: 32)
                                    .padding(12)
                                    .glassPanelStyle(cornerRadius: 99)
                            }
                            .buttonStyle(.plain)

                            Spacer()

                            GameStatsView(
                                score: gameState.score,
                                lines: Int32(gameState.linesCleared),
                                time: model.elapsedTime
                            )

                            Spacer()

                            NextPieceView(piece: gameState.nextPiece)
                        }
                        .padding(.horizontal)

                        GeometryReader { geometry in
                        GameBoardView(
                            gameState: gameState,
                            settings: model.settings,
                            ghostY:  model.ghostPieceY?.int32Value
                        )
                        .gesture(
                            TapGesture()
                                .onEnded { component.onRotate() }
                                .simultaneously(with: DragGesture(minimumDistance: 0)
                                    .onChanged { value in
                                        if !didStartDragging {
                                            component.onDragStarted()
                                            didStartDragging = true
                                        }
                                        
                                        let deltaX = value.translation.width - self.lastDragTranslation.width
                                        let deltaY = value.translation.height - self.lastDragTranslation.height
                                        self.lastDragTranslation = value.translation
                                        
                                        component.onDragged(deltaX: Float(deltaX), deltaY: Float(deltaY))
                                    }
                                    .onEnded { _ in
                                        component.onDragEnded()
                                        
                                        self.lastDragTranslation = .zero
                                        self.didStartDragging = false
                                    }
                                )
                        )
                        .onAppear {
                            component.onBoardSizeChanged(height: Float(geometry.size.height))
                        }
                    }
                    .aspectRatio(CGFloat(gameState.board.width) / CGFloat(gameState.board.height), contentMode: .fit)
                    .padding(8)
                    .glassPanelStyle()
                    .padding()
                    
                    Spacer()
                }
                    .frame(maxWidth: isIPadOrLandscape ? 600 : .infinity)

                    if isIPadOrLandscape {
                        Spacer()
                    }
                }
                
                if let child = dialog.child?.instance {
                    GlassDialogContainer {
                        DialogView(component: component, model: model, child: child)
                    }
                    .transition(.opacity.combined(with: .scale(scale: 0.9)))
                }
            }
        }
        .navigationBarBackButtonHidden(true)
        .sheet(item: Binding<SheetItem?>(
            get: {
                if let child = sheet.child?.instance {
                    return SheetItem(child: child)
                } else {
                    return nil
                }
            },
            set: { item in
                if item == nil {
                    component.onDismissSheet()
                }
            }
        )) { sheetItem in
            SheetView(child: sheetItem.child)
        }
        .keyboardAware { key in
            switch key.lowercased() {
            case "a", "directionleft": component.onMoveLeft()
            case "d", "directionright": component.onMoveRight()
            case "s", "directiondown": component.onMoveDown()
            case "w", " ", "directionup": component.onRotate()
            case "\r": component.onHardDrop()
            case "\u{1b}", "p": // escape or 'p'
                component.onPause()
            default: break
            }
        }
    }
}


private struct SheetView: View {
    let child: GameComponentSheetChild
    
    var body: some View {
        switch child {
        case let child as SettingsChild:
            SettingsView(child.component)
        default:
            EmptyView()
        }
    }
}

private struct SheetItem: Identifiable {
    let child: GameComponentSheetChild
    
    var id: String {
        switch child {
        case is GameComponentSheetChildSettings:
            return "settings"
        default:
            return UUID().uuidString
        }
    }
}

private struct DialogView: View {
    let component: GameComponent
    let model: GameComponentModel
    let child: GameComponentDialogChild
    
    var body: some View {
        switch child {
        case is GameOverChild:
            GameOverDialogContent(component: component, model: model)
        case is PauseChild:
            PauseDialogContent(component: component)
        case let child as ErrorChild:
            ErrorDialogContent(message: child.message, onDismiss: component.onDismissSheet)
        default:
            EmptyView()
        }
    }
}
private typealias GameOverChild = GameComponentDialogChildGameOver
private typealias PauseChild = GameComponentDialogChildPause
private typealias ErrorChild = GameComponentDialogChildError
private typealias SettingsChild = GameComponentSheetChildSettings


struct GameStatsView: View {
    let score: Int64
    let lines: Int32
    let time: Int64
    
    var body: some View {
        HStack(spacing: 16) {
            StatItem(label: Strings.score, value: "\(score)")
            StatItem(label: Strings.lines, value: "\(lines)")
            StatItem(label: Strings.time, value: formatTime(time))
        }
        .padding(12)
        .glassPanelStyle(cornerRadius: 16)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
    
    private func formatTime(_ milliseconds: Int64) -> String {
        let seconds = (milliseconds / 1000) % 60
        let minutes = (milliseconds / 1000) / 60
        return String(format: "%d:%02d", minutes, seconds)
    }
}

struct StatItem: View {
    let label: String
    let value: String
    
    @Environment(\.colorScheme) var colorScheme

    private var textColor: Color {
        colorScheme == .dark ? Color.green : Color(red: 0, green: 0.5, blue: 0)
    }
    
    var body: some View {
        VStack(spacing: 4) {
            Text(label)
                .font(.caption)
                .foregroundColor(textColor)
                .shadow(color: .black.opacity(0.3), radius: 1)

            Text(value)
                .font(.headline)
                .fontWeight(.bold)
                .foregroundColor(.primary)
                .shadow(color: .black.opacity(0.3), radius: 1)
        }
    }
}

struct NextPieceView: View {
    let piece: Tetromino
    
    @Environment(\.colorScheme) var colorScheme

    private var textColor: Color {
        colorScheme == .dark ? Color.green : Color(red: 0, green: 0.5, blue: 0)
    }
    
    var body: some View {
        VStack(spacing: 4) {
            Text(Strings.next)
                .font(.caption)
                .foregroundColor(textColor)
                .shadow(color: .black.opacity(0.3), radius: 1)
            
            // Render the actual piece
            Canvas { context, size in
                let cellSize: CGFloat = 12
                let blocks = piece.blocks
                
                // Calculate bounds to center the piece
                let minX = blocks.map { $0.x }.min() ?? 0
                let maxX = blocks.map { $0.x }.max() ?? 0
                let minY = blocks.map { $0.y }.min() ?? 0
                let maxY = blocks.map { $0.y }.max() ?? 0
                
                let pieceWidth = CGFloat(maxX - minX + 1) * cellSize
                let pieceHeight = CGFloat(maxY - minY + 1) * cellSize
                
                let offsetX = (size.width - pieceWidth) / 2 - CGFloat(minX) * cellSize
                let offsetY = (size.height - pieceHeight) / 2 - CGFloat(minY) * cellSize
                
                // Draw each block
                for block in blocks {
                    let x = CGFloat(block.x) * cellSize + offsetX
                    let y = CGFloat(block.y) * cellSize + offsetY
                    
                    let rect = CGRect(x: x, y: y, width: cellSize - 1, height: cellSize - 1)
                    context.fill(Path(rect), with: .color(getTetrominoColor(type: piece.type)))
                }
            }
            .frame(width: 60, height: 60)
        }
        .padding(8)
        .glassPanelStyle(cornerRadius: 16)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
    
    private func getTetrominoColor(type: TetrominoType) -> Color {
        switch type {
        case .i: return Color(red: 0, green: 0.94, blue: 0.94)
        case .o: return Color(red: 0.94, green: 0.94, blue: 0)
        case .t: return Color(red: 0.63, green: 0, blue: 0.94)
        case .s: return Color(red: 0, green: 0.94, blue: 0)
        case .z: return Color(red: 0.94, green: 0, blue: 0)
        case .j: return Color(red: 0, green: 0, blue: 0.94)
        case .l: return Color(red: 0.94, green: 0.63, blue: 0)
        default: return .gray
        }
    }
}

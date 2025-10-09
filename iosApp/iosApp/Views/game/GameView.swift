import SwiftUI
import Shared

struct GameView: View {
    private let component: GameComponent
    
    @StateValue
    private var model: GameComponentModel
    
    @StateValue
    private var dialog: ChildSlot<AnyObject, GameComponentDialogChild>
    
    @Environment(\.dismiss) private var dismiss
    
    @State private var lastDragTranslation: CGSize = .zero
    @State private var didStartDragging = false
    
    init(_ component: GameComponent) {
        self.component = component
        _model = StateValue(component.model)
        _dialog = StateValue(component.childSlot)
    }
    
    var body: some View {
        ZStack {
            LinearGradient(
                colors: [.purple.opacity(0.6), .blue.opacity(0.6)],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()
            
            if model.isLoading {
                ProgressView()
            } else if let gameState = model.gameState {
                VStack(spacing: 16) {
                    // Top Bar
                    HStack {
                        Button {
                            component.onPause()
                        } label: {
                            Image(systemName: "pause.fill")
                                .font(.title2)
                                .foregroundColor(.white)
                                .padding(12)
                                .glassPanelStyle(cornerRadius: 99)
                        }
                        
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
                                .onEnded {
                                    component.onRotate()
                                }
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
                
                if let child = dialog.child?.instance {
                    GlassDialogContainer {
                        DialogView(component: component, model: model, child: child)
                    }
                    .transition(.opacity.combined(with: .scale(scale: 0.9)))
                }
            }
        }
        .navigationBarBackButtonHidden(true)
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


struct GameStatsView: View {
    let score: Int32
    let lines: Int32
    let time: Int64
    
    var body: some View {
        HStack(spacing: 16) {
            StatItem(label: "Score", value: "\(score)")
            StatItem(label: "Lines", value: "\(lines)")
            StatItem(label: "Time", value: formatTime(time))
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
    
    var body: some View {
        VStack(spacing: 4) {
            Text(label)
                .font(.caption)
                .foregroundColor(.secondary)
            Text(value)
                .font(.headline)
        }
    }
}

struct NextPieceView: View {
    let piece: Tetromino
    
    var body: some View {
        VStack(spacing: 4) {
            Text("Next")
                .font(.caption)
                .foregroundColor(.secondary)
            
            // Render the actual piece
            Canvas { context, size in
                let cellSize: CGFloat = 12
                let blocks = piece.blocks.compactMap { $0 as? Position }
                
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

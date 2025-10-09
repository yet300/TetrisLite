import SwiftUI
import Shared

struct GameView: View {
    private let component: GameComponent
    
    @StateValue
    private var model: GameComponentModel
    
    @State private var showPauseDialog = false
    @Environment(\.dismiss) private var dismiss
    
    init(_ component: GameComponent) {
        self.component = component
        _model = StateValue(component.model)
    }
    
    var body: some View {
        ZStack {
            if model.isLoading {
                ProgressView()
            } else if model.isGameOver {
                GameOverView(
                    score: model.finalScore,
                    lines: model.finalLinesCleared,
                    onQuit: {
                        component.onQuit()
                        dismiss()
                    }
                )
            } else if let gameState = model.gameState {
                VStack(spacing: 16) {
                    // Top Bar
                    HStack {
                        Button {
                            component.onPause()
                            showPauseDialog = true
                        } label: {
                            Image(systemName: "pause.fill")
                                .font(.title2)
                                .foregroundColor(.primary)
                                .padding(12)
                                .background(.ultraThinMaterial)
                                .clipShape(Circle())
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
                    
                    // Game Board
                    GameBoardView(
                        gameState: gameState,
                        settings: model.settings,
                        ghostY:  model.ghostPieceY?.int32Value
                    )
                    .aspectRatio(CGFloat(gameState.board.width) / CGFloat(gameState.board.height), contentMode: .fit)
                    .padding()
                    .background(
                        RoundedRectangle(cornerRadius: 20)
                            .fill(.ultraThinMaterial)
                    )
                    .padding()
                    
                    Spacer()
                }
            }
        }
        .navigationBarBackButtonHidden(true)
        .alert("Game Paused", isPresented: $showPauseDialog) {
            Button("Resume") {
                component.onResume()
            }
            Button("Quit", role: .destructive) {
                component.onQuit()
                dismiss()
            }
        } message: {
            Text("What would you like to do?")
        }
        .gesture(
            DragGesture()
                .onEnded { value in
                    handleSwipe(value: value)
                }
        )
        .onTapGesture {
            component.onRotate()
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
                showPauseDialog = true
            default: break
            }
        }
    }
    
    private func handleSwipe(value: DragGesture.Value) {
        let horizontalAmount = value.translation.width
        let verticalAmount = value.translation.height
        
        if abs(horizontalAmount) > abs(verticalAmount) {
            if horizontalAmount < -50 {
                component.onMoveLeft()
            } else if horizontalAmount > 50 {
                component.onMoveRight()
            }
        } else {
            if verticalAmount > 100 {
                component.onHardDrop()
            } else if verticalAmount > 50 {
                component.onMoveDown()
            }
        }
    }
}

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
        .background(.regularMaterial)
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
        .background(.regularMaterial)
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

struct GameOverView: View {
    let score: Int32
    let lines: Int32
    let onQuit: () -> Void
    
    var body: some View {
        VStack(spacing: 24) {
            Text("Game Over")
                .font(.system(size: 48, weight: .bold))
                .foregroundColor(.red)
            
            VStack(spacing: 12) {
                Text("Final Score: \(score)")
                    .font(.title2)
                Text("Lines Cleared: \(lines)")
                    .font(.title3)
                    .foregroundColor(.secondary)
            }
            
            Button("Back to Home") {
                onQuit()
            }
            .font(.headline)
            .foregroundColor(.white)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 16)
            .background(
                RoundedRectangle(cornerRadius: 16)
                    .fill(Color.blue)
            )
            .padding(.horizontal, 32)
        }
        .padding()
        .background(.regularMaterial)
        .clipShape(RoundedRectangle(cornerRadius: 24))
        .padding()
    }
}

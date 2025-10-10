import SwiftUI
import Shared

struct GameBoardView: View {
    let gameState: GameState
    let settings: GameSettings
    let ghostY: Int32?
    
    var body: some View {
        Canvas { context, size in
            let cellSize = size.width / CGFloat(gameState.board.width)
            
            // Draw background
            context.fill(
                Path(CGRect(origin: .zero, size: size)),
                with: .color(getBackgroundColor())
            )
            
            // Draw locked blocks
            for (position, type) in gameState.board.cells {
                if let pos = position as? Position, let tetrominoType = type as? TetrominoType {
                    if pos.y >= 0 {
                        drawBlock(
                            context: context,
                            x: CGFloat(pos.x) * cellSize,
                            y: CGFloat(pos.y) * cellSize,
                            size: cellSize - 1,
                            color: getTetrominoColor(type: tetrominoType)
                        )
                    }
                }
            }
            
            // Draw ghost piece
            if let piece = gameState.currentPiece,
               let landingY = ghostY,
               landingY > gameState.currentPosition.y {
                for block in piece.blocks {
                    if let blockPos = block as? Position {
                        let absoluteX = gameState.currentPosition.x + blockPos.x
                        let absoluteY = landingY + blockPos.y
                        
                        if absoluteY >= 0 && absoluteY < gameState.board.height {
                            drawBlock(
                                context: context,
                                x: CGFloat(absoluteX) * cellSize,
                                y: CGFloat(absoluteY) * cellSize,
                                size: cellSize - 1,
                                color: getTetrominoColor(type: piece.type).opacity(0.3)
                            )
                        }
                    }
                }
            }
            
            // Draw current piece
            if let piece = gameState.currentPiece {
                for block in piece.blocks {
                    if let blockPos = block as? Position {
                        let absoluteX = gameState.currentPosition.x + blockPos.x
                        let absoluteY = gameState.currentPosition.y + blockPos.y
                        
                        if absoluteY >= 0 && absoluteY < gameState.board.height {
                            drawBlock(
                                context: context,
                                x: CGFloat(absoluteX) * cellSize,
                                y: CGFloat(absoluteY) * cellSize,
                                size: cellSize - 1,
                                color: getTetrominoColor(type: piece.type)
                            )
                        }
                    }
                }
            }
            
            // Draw grid lines
            context.stroke(
                Path { path in
                    for x in 0...Int(gameState.board.width) {
                        path.move(to: CGPoint(x: CGFloat(x) * cellSize, y: 0))
                        path.addLine(to: CGPoint(x: CGFloat(x) * cellSize, y: size.height))
                    }
                    for y in 0...Int(gameState.board.height) {
                        path.move(to: CGPoint(x: 0, y: CGFloat(y) * cellSize))
                        path.addLine(to: CGPoint(x: size.width, y: CGFloat(y) * cellSize))
                    }
                },
                with: .color(.secondarySystemFill.opacity(0.2)),
                lineWidth: 1
            )
        }
        .border(Color.secondarySystemFill.opacity(0.5), width: 2)
    }
    
    private func drawBlock(context: GraphicsContext, x: CGFloat, y: CGFloat, size: CGFloat, color: Color) {
        let rect = CGRect(x: x, y: y, width: size, height: size)
        context.fill(Path(rect), with: .color(color))
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
    
    private func getBackgroundColor() -> Color {
        return .systemBackground.opacity(0.4)
    }
}

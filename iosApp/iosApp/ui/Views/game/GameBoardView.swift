import SwiftUI
import Shared

struct GameBoardView: View {
    let gameState: GameState
    let settings: GameSettings
    let ghostY: Int32?
    let lineSweeps: [AppleGameLineSweepEntry]
    let lockGlows: [AppleGameLockGlowEntry]
    
    var body: some View {
        TimelineView(.animation(minimumInterval: 1.0 / 24.0)) { timeline in
            Canvas { context, size in
                let cellSize = size.width / CGFloat(gameState.board.width)
                let boardRect = CGRect(origin: .zero, size: size)

                context.drawBoardChrome(
                    settings: settings,
                    boardRect: boardRect,
                    columns: Int(gameState.board.width),
                    rows: Int(gameState.board.height),
                    cellSize: cellSize,
                    profile: .ios,
                    shimmerPhase: boardShimmerPhase(at: timeline.date)
                )

                for (pos, tetrominoType) in gameState.board.cells {
                    if pos.y >= 0 {
                        context.drawStyledBlock(
                            type: tetrominoType,
                            settings: settings,
                            topLeft: CGPoint(x: CGFloat(pos.x) * cellSize, y: CGFloat(pos.y) * cellSize),
                            cellSize: cellSize
                        )
                    }
                }

                if let piece = gameState.currentPiece, let landingY = ghostY, landingY > gameState.currentPosition.y {
                    for blockPos in piece.blocks {
                        let absoluteX = gameState.currentPosition.x + blockPos.x
                        let absoluteY = landingY + blockPos.y

                        if absoluteY >= 0 && absoluteY < gameState.board.height {
                            context.drawStyledBlock(
                                type: piece.type,
                                settings: settings,
                                topLeft: CGPoint(x: CGFloat(absoluteX) * cellSize, y: CGFloat(absoluteY) * cellSize),
                                cellSize: cellSize,
                                alpha: 0.3
                            )
                        }
                    }
                }

                if let piece = gameState.currentPiece {
                    for blockPos in piece.blocks {
                        let absoluteX = gameState.currentPosition.x + blockPos.x
                        let absoluteY = gameState.currentPosition.y + blockPos.y

                        if absoluteY >= 0 && absoluteY < gameState.board.height {
                            context.drawStyledBlock(
                                type: piece.type,
                                settings: settings,
                                topLeft: CGPoint(x: CGFloat(absoluteX) * cellSize, y: CGFloat(absoluteY) * cellSize),
                                cellSize: cellSize
                            )
                        }
                    }
                }

                context.drawBoardLineSweeps(
                    lineSweeps: lineSweeps,
                    theme: settings.themeConfig.visualTheme,
                    boardRect: boardRect,
                    totalRows: Int(gameState.board.height),
                    cellSize: cellSize,
                    date: timeline.date
                )

                context.drawBoardLockGlows(
                    lockGlows: lockGlows,
                    theme: settings.themeConfig.visualTheme,
                    boardRect: boardRect,
                    totalColumns: Int(gameState.board.width),
                    totalRows: Int(gameState.board.height),
                    cellSize: cellSize,
                    date: timeline.date
                )

                context.drawBoardGrid(
                    theme: settings.themeConfig.visualTheme,
                    boardRect: boardRect,
                    columns: Int(gameState.board.width),
                    rows: Int(gameState.board.height),
                    cellSize: cellSize,
                    profile: .ios
                )
            }
        }
    }
}

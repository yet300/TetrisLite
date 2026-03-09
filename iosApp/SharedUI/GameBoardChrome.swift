import SwiftUI
import Shared

enum AppleBoardChromeProfile: Equatable {
    case ios
    case watch

    var cornerRadius: CGFloat {
        switch self {
        case .ios: return 0
        case .watch: return 6
        }
    }

    var borderLineWidth: CGFloat {
        switch self {
        case .ios: return 2
        case .watch: return 0.8
        }
    }

    var gridOpacity: CGFloat {
        switch self {
        case .ios: return 0.16
        case .watch: return 0.11
        }
    }
}

func boardShimmerPhase(at date: Date) -> CGFloat {
    let seconds = date.timeIntervalSinceReferenceDate
    let raw = seconds.truncatingRemainder(dividingBy: 3.6) / 3.6
    return CGFloat(raw)
}

func boardShouldRenderShimmer(settings: GameSettings) -> Bool {
    settings.themeConfig.visualTheme == .neon || settings.themeConfig.pieceStyle == .glass
}

extension GraphicsContext {
    mutating func drawBoardChrome(
        settings: GameSettings,
        boardRect: CGRect,
        columns: Int,
        rows: Int,
        cellSize: CGFloat,
        profile: AppleBoardChromeProfile,
        shimmerPhase: CGFloat
    ) {
        let theme = settings.themeConfig.visualTheme
        let boardPath = Path(
            roundedRect: boardRect,
            cornerRadius: profile.cornerRadius
        )

        fill(boardPath, with: .color(themeBackgroundColor(theme: theme)))

        for row in 0..<max(rows, 1) {
            for column in 0..<max(columns, 1) {
                let alpha = (row + column).isMultiple(of: 2) ? 0.035 : 0.018
                let cellRect = CGRect(
                    x: boardRect.minX + CGFloat(column) * cellSize,
                    y: boardRect.minY + CGFloat(row) * cellSize,
                    width: cellSize,
                    height: cellSize
                )
                fill(Path(cellRect), with: .color(.white.opacity(alpha)))
            }
        }

        fill(
            boardPath,
            with: .linearGradient(
                Gradient(colors: [
                    .white.opacity(0.08),
                    .clear,
                    .black.opacity(0.22),
                ]),
                startPoint: CGPoint(x: boardRect.minX, y: boardRect.minY),
                endPoint: CGPoint(x: boardRect.minX, y: boardRect.maxY)
            )
        )

        fill(
            boardPath,
            with: .linearGradient(
                Gradient(colors: [
                    .white.opacity(0.035),
                    .clear,
                    .black.opacity(0.12),
                ]),
                startPoint: CGPoint(x: boardRect.minX, y: boardRect.minY),
                endPoint: CGPoint(x: boardRect.maxX, y: boardRect.minY)
            )
        )

        let rimRect = CGRect(
            x: boardRect.minX,
            y: boardRect.minY,
            width: boardRect.width,
            height: max(1.5, cellSize * 0.18)
        )
        fill(Path(rimRect), with: .color(.white.opacity(0.06)))

        if boardShouldRenderShimmer(settings: settings) {
            drawBoardShimmer(
                boardPath: boardPath,
                boardRect: boardRect,
                settings: settings,
                shimmerPhase: shimmerPhase
            )
        }

        stroke(
            boardPath,
            with: .color(themeGridColor(theme: theme).opacity(0.45)),
            lineWidth: profile.borderLineWidth
        )
    }

    mutating func drawBoardGrid(
        theme: VisualTheme,
        boardRect: CGRect,
        columns: Int,
        rows: Int,
        cellSize: CGFloat,
        profile: AppleBoardChromeProfile
    ) {
        let gridColor = themeGridColor(theme: theme).opacity(profile.gridOpacity)

        stroke(
            Path { path in
                for x in 0...columns {
                    path.move(
                        to: CGPoint(
                            x: boardRect.minX + CGFloat(x) * cellSize,
                            y: boardRect.minY
                        )
                    )
                    path.addLine(
                        to: CGPoint(
                            x: boardRect.minX + CGFloat(x) * cellSize,
                            y: boardRect.maxY
                        )
                    )
                }

                for y in 0...rows {
                    path.move(
                        to: CGPoint(
                            x: boardRect.minX,
                            y: boardRect.minY + CGFloat(y) * cellSize
                        )
                    )
                    path.addLine(
                        to: CGPoint(
                            x: boardRect.maxX,
                            y: boardRect.minY + CGFloat(y) * cellSize
                        )
                    )
                }
            },
            with: .color(gridColor),
            lineWidth: profile == .ios ? 1 : 0.5
        )
    }

    private mutating func drawBoardShimmer(
        boardPath: Path,
        boardRect: CGRect,
        settings: GameSettings,
        shimmerPhase: CGFloat
    ) {
        let accent = themeAccentColor(theme: settings.themeConfig.visualTheme)
        let shimmerStrength: CGFloat = settings.themeConfig.visualTheme == .neon ? 0.2 : 0.13
        let sweepWidth = boardRect.width * 0.34
        let startX = boardRect.minX - sweepWidth
        let currentX = startX + (boardRect.width + sweepWidth * 2) * shimmerPhase

        let startPoint = CGPoint(x: currentX - sweepWidth, y: boardRect.minY)
        let endPoint = CGPoint(x: currentX + sweepWidth, y: boardRect.maxY)

        fill(
            boardPath,
            with: .linearGradient(
                Gradient(colors: [
                    .clear,
                    accent.opacity(Double(shimmerStrength)),
                    .white.opacity(Double(shimmerStrength * 0.8)),
                    .clear,
                ]),
                startPoint: startPoint,
                endPoint: endPoint
            )
        )
    }
}

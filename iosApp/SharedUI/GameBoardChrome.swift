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

    mutating func drawBoardLineSweeps(
        lineSweeps: [AppleGameLineSweepEntry],
        theme: VisualTheme,
        boardRect: CGRect,
        totalRows: Int,
        cellSize: CGFloat,
        date: Date
    ) {
        guard !lineSweeps.isEmpty else {
            return
        }

        let style = appleThemeEffectStyle(theme: theme)

        for sweep in lineSweeps {
            let progress = sweep.progress(at: date)
            guard progress < 1 else {
                continue
            }

            let leadingX = boardRect.minX + (boardRect.width * progress)
            let sweepWidth = max(cellSize * 2.2, boardRect.width * (0.18 + 0.18 * sweep.power))
            let opacity = Double((1 - progress) * (sweep.isHigh ? 0.62 : 0.42)) * style.sweepOpacityBoost

            for row in sweep.clearedRows where row >= 0 && row < totalRows {
                let rowY = boardRect.minY + CGFloat(row) * cellSize
                let rowRect = CGRect(
                    x: boardRect.minX,
                    y: rowY,
                    width: boardRect.width,
                    height: cellSize
                )
                let bandRect = CGRect(
                    x: max(boardRect.minX, leadingX - sweepWidth),
                    y: rowY,
                    width: min(boardRect.maxX, leadingX + sweepWidth) - max(boardRect.minX, leadingX - sweepWidth),
                    height: cellSize
                )

                fill(
                    Path(rowRect),
                    with: .linearGradient(
                        Gradient(colors: [
                            style.sweepFill.opacity(opacity * 0.08),
                            style.sweepPrimary.opacity(opacity * 0.18),
                            style.sweepFill.opacity(opacity * 0.08),
                        ]),
                        startPoint: CGPoint(x: rowRect.minX, y: rowRect.midY),
                        endPoint: CGPoint(x: rowRect.maxX, y: rowRect.midY)
                    )
                )

                fill(
                    Path(bandRect),
                    with: .linearGradient(
                        Gradient(colors: [
                            .clear,
                            style.sweepPrimary.opacity(opacity * 0.45),
                            style.sweepFill.opacity(opacity),
                            style.sweepSecondary.opacity(opacity * 0.55),
                            .clear,
                        ]),
                        startPoint: CGPoint(x: bandRect.minX, y: bandRect.midY),
                        endPoint: CGPoint(x: bandRect.maxX, y: bandRect.midY)
                    )
                )
            }
        }
    }

    mutating func drawBoardLockGlows(
        lockGlows: [AppleGameLockGlowEntry],
        theme: VisualTheme,
        boardRect: CGRect,
        totalColumns: Int,
        totalRows: Int,
        cellSize: CGFloat,
        date: Date
    ) {
        guard !lockGlows.isEmpty else {
            return
        }

        let style = appleThemeEffectStyle(theme: theme)
        let boardPath = Path(boardRect)

        for glow in lockGlows {
            let progress = glow.progress(at: date)
            guard progress < 1 else {
                continue
            }

            let visibleCells =
                glow.lockedCells.filter {
                    $0.x >= 0 && $0.x < totalColumns && $0.y >= 0 && $0.y < totalRows
                }

            guard !visibleCells.isEmpty else {
                continue
            }

            let minX = visibleCells.min(by: { $0.x < $1.x })?.x ?? 0
            let maxX = visibleCells.max(by: { $0.x < $1.x })?.x ?? 0
            let minY = visibleCells.min(by: { $0.y < $1.y })?.y ?? 0
            let maxY = visibleCells.max(by: { $0.y < $1.y })?.y ?? 0

            let lockRect = CGRect(
                x: boardRect.minX + CGFloat(minX) * cellSize,
                y: boardRect.minY + CGFloat(minY) * cellSize,
                width: CGFloat(maxX - minX + 1) * cellSize,
                height: CGFloat(maxY - minY + 1) * cellSize
            )
            let expandedRect = lockRect.insetBy(
                dx: -cellSize * (0.35 + 0.25 * glow.power),
                dy: -cellSize * (0.35 + 0.25 * glow.power)
            )
            let center = CGPoint(x: lockRect.midX, y: lockRect.midY)
            let radius = max(lockRect.width, lockRect.height) * (0.72 + progress * 0.55) + cellSize * (1.3 + 1.9 * glow.power)
            let opacity = Double((1 - progress) * (glow.isHigh ? 0.30 : 0.17)) * style.lockGlowOpacityBoost

            fill(
                boardPath,
                with: .radialGradient(
                    Gradient(colors: [
                        style.lockGlowSecondary.opacity(opacity * 0.22),
                        style.lockGlowPrimary.opacity(opacity),
                        style.lockGlowSecondary.opacity(opacity * 0.45),
                        .clear,
                    ]),
                    center: center,
                    startRadius: 0,
                    endRadius: radius
                )
            )

            fill(
                Path(roundedRect: expandedRect, cornerRadius: cellSize * style.lockGlowCornerRadiusFactor),
                with: .linearGradient(
                    Gradient(colors: [
                        style.lockGlowPrimary.opacity(opacity * 0.22),
                        style.lockGlowSecondary.opacity(opacity * 0.38),
                        style.lockGlowPrimary.opacity(opacity * 0.28),
                    ]),
                    startPoint: CGPoint(x: expandedRect.minX, y: expandedRect.minY),
                    endPoint: CGPoint(x: expandedRect.maxX, y: expandedRect.maxY)
                )
            )
        }
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

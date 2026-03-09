import SwiftUI
import Shared

struct GameInputActions {
    let onPause: () -> Void
    let onHold: () -> Void
    let onRotate: () -> Void
    let onDragStarted: () -> Void
    let onDragged: (Float, Float) -> Void
    let onDragEnded: () -> Void
    let onBoardSizeChanged: (Float) -> Void
}

struct GameAdaptiveLayout: View {
    let gameState: GameState
    let settings: GameSettings
    let elapsedTime: Int64
    let ghostY: Int32?
    let actions: GameInputActions

    @Environment(\.horizontalSizeClass) private var horizontalSizeClass
    @Environment(\.verticalSizeClass) private var verticalSizeClass

    var body: some View {
        GeometryReader { geometry in
            let config =
                GameAdaptiveConfiguration(
                    size: geometry.size,
                    horizontalSizeClass: horizontalSizeClass,
                    verticalSizeClass: verticalSizeClass
                )

            Group {
                switch config.layoutMode {
                case .compact:
                    CompactGameLayout(
                        gameState: gameState,
                        settings: settings,
                        elapsedTime: elapsedTime,
                        ghostY: ghostY,
                        actions: actions,
                        metrics: config.metrics
                    )
                case .medium:
                    MediumGameLayout(
                        gameState: gameState,
                        settings: settings,
                        elapsedTime: elapsedTime,
                        ghostY: ghostY,
                        actions: actions,
                        metrics: config.metrics
                    )
                case .expanded:
                    ExpandedGameLayout(
                        gameState: gameState,
                        settings: settings,
                        elapsedTime: elapsedTime,
                        ghostY: ghostY,
                        actions: actions,
                        metrics: config.metrics
                    )
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .padding(.horizontal, config.metrics.horizontalPadding)
            .padding(.vertical, config.metrics.verticalPadding)
        }
    }
}

private struct CompactGameLayout: View {
    let gameState: GameState
    let settings: GameSettings
    let elapsedTime: Int64
    let ghostY: Int32?
    let actions: GameInputActions
    let metrics: GameAdaptiveMetrics

    var body: some View {
        VStack(spacing: metrics.sectionSpacing) {
            CompactHeaderPane(
                gameState: gameState,
                elapsedTime: elapsedTime,
                actions: actions,
                metrics: metrics
            )

            GameBoardPane(
                gameState: gameState,
                settings: settings,
                ghostY: ghostY,
                actions: actions
            )
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .layoutPriority(1)

            CompactFooterPane(
                gameState: gameState,
                settings: settings,
                elapsedTime: elapsedTime,
                metrics: metrics
            )
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

private struct MediumGameLayout: View {
    let gameState: GameState
    let settings: GameSettings
    let elapsedTime: Int64
    let ghostY: Int32?
    let actions: GameInputActions
    let metrics: GameAdaptiveMetrics

    var body: some View {
        HStack(spacing: metrics.sectionSpacing) {
            GameBoardPane(
                gameState: gameState,
                settings: settings,
                ghostY: ghostY,
                actions: actions
            )
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .layoutPriority(1)

            VStack(alignment: .leading, spacing: metrics.sectionSpacing) {
                GameControlButtonsRow(
                    buttonSize: metrics.buttonSize,
                    iconSize: metrics.buttonIconSize,
                    actions: actions
                )

                GameStatsView(
                    score: gameState.score,
                    lines: Int32(gameState.linesCleared),
                    level: gameState.level,
                    time: elapsedTime,
                    density: .regular,
                    includeTime: false
                )

                PiecePreviewView(
                    title: Strings.hold,
                    piece: gameState.holdPiece,
                    settings: settings,
                    previewSize: metrics.regularPieceSize,
                    density: .regular
                )

                NextQueuePreviewView(
                    title: Strings.next,
                    pieces: gameState.visibleNextPieces,
                    settings: settings,
                    previewSize: metrics.regularPieceSize,
                    density: .regular
                )

                Spacer(minLength: 0)

                TimeStatView(
                    time: elapsedTime,
                    density: .regular
                )
            }
            .frame(width: metrics.secondaryPaneWidth, alignment: .topLeading)
            .frame(maxHeight: .infinity, alignment: .top)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

private struct ExpandedGameLayout: View {
    let gameState: GameState
    let settings: GameSettings
    let elapsedTime: Int64
    let ghostY: Int32?
    let actions: GameInputActions
    let metrics: GameAdaptiveMetrics

    var body: some View {
        HStack(spacing: metrics.sectionSpacing) {
            VStack(alignment: .leading, spacing: metrics.sectionSpacing) {
                PiecePreviewView(
                    title: Strings.hold,
                    piece: gameState.holdPiece,
                    settings: settings,
                    previewSize: metrics.expandedPieceSize * 0.72,
                    density: .spacious
                )

                GameStatsView(
                    score: gameState.score,
                    lines: Int32(gameState.linesCleared),
                    level: gameState.level,
                    time: elapsedTime,
                    density: .spacious,
                    includeTime: false
                )

                Spacer(minLength: 0)

                GameControlButtonsRow(
                    buttonSize: metrics.buttonSize,
                    iconSize: metrics.buttonIconSize,
                    actions: actions
                )
            }
            .frame(width: metrics.secondaryPaneWidth, alignment: .topLeading)
            .frame(maxHeight: .infinity, alignment: .top)

            GameBoardPane(
                gameState: gameState,
                settings: settings,
                ghostY: ghostY,
                actions: actions
            )
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .layoutPriority(2)

            VStack(alignment: .leading, spacing: metrics.sectionSpacing) {
                NextQueuePreviewView(
                    title: Strings.next,
                    pieces: gameState.visibleNextPieces,
                    settings: settings,
                    previewSize: metrics.expandedPieceSize * 0.58,
                    density: .spacious
                )

                Spacer(minLength: 0)

                TimeStatView(
                    time: elapsedTime,
                    density: .spacious
                )
            }
            .frame(width: metrics.tertiaryPaneWidth, alignment: .topLeading)
            .frame(maxHeight: .infinity, alignment: .top)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

private struct CompactHeaderPane: View {
    let gameState: GameState
    let elapsedTime: Int64
    let actions: GameInputActions
    let metrics: GameAdaptiveMetrics

    var body: some View {
        ViewThatFits(in: .horizontal) {
            HStack(alignment: .top, spacing: metrics.sectionSpacing) {
                GameControlButtonsRow(
                    buttonSize: metrics.buttonSize,
                    iconSize: metrics.buttonIconSize,
                    actions: actions
                )

                GameStatsView(
                    score: gameState.score,
                    lines: Int32(gameState.linesCleared),
                    level: gameState.level,
                    time: elapsedTime,
                    density: .compact,
                    includeTime: false
                )
                .frame(maxWidth: .infinity, alignment: .leading)
            }

            VStack(alignment: .leading, spacing: metrics.sectionSpacing) {
                GameControlButtonsRow(
                    buttonSize: metrics.buttonSize,
                    iconSize: metrics.buttonIconSize,
                    actions: actions
                )

                GameStatsView(
                    score: gameState.score,
                    lines: Int32(gameState.linesCleared),
                    level: gameState.level,
                    time: elapsedTime,
                    density: .compact,
                    includeTime: false
                )
            }
        }
    }
}

private struct CompactFooterPane: View {
    let gameState: GameState
    let settings: GameSettings
    let elapsedTime: Int64
    let metrics: GameAdaptiveMetrics

    var body: some View {
        ViewThatFits(in: .horizontal) {
            HStack(alignment: .top, spacing: metrics.sectionSpacing) {
                QueueCompactRow(
                    holdPiece: gameState.holdPiece,
                    nextPieces: gameState.visibleNextPieces,
                    settings: settings,
                    pieceSize: metrics.compactPieceSize
                )
                .frame(maxWidth: .infinity, alignment: .leading)

                TimeStatView(
                    time: elapsedTime,
                    density: .compact
                )
                .frame(width: 92)
            }

            VStack(alignment: .leading, spacing: metrics.sectionSpacing) {
                QueueCompactRow(
                    holdPiece: gameState.holdPiece,
                    nextPieces: gameState.visibleNextPieces,
                    settings: settings,
                    pieceSize: metrics.compactPieceSize
                )

                TimeStatView(
                    time: elapsedTime,
                    density: .compact
                )
            }
        }
    }
}

private struct QueueCompactRow: View {
    let holdPiece: Tetromino?
    let nextPieces: [Tetromino]
    let settings: GameSettings
    let pieceSize: CGFloat

    var body: some View {
        HStack(spacing: 8) {
            PiecePreviewView(
                title: Strings.hold,
                piece: holdPiece,
                settings: settings,
                previewSize: pieceSize,
                density: .compact
            )
            NextQueuePreviewView(
                title: Strings.next,
                pieces: nextPieces,
                settings: settings,
                previewSize: pieceSize,
                density: .compact,
                axis: .horizontal
            )
        }
    }
}

private struct GameControlButtonsRow: View {
    let buttonSize: CGFloat
    let iconSize: CGFloat
    let actions: GameInputActions

    var body: some View {
        HStack(spacing: 10) {
            Button(action: actions.onPause) {
                Image(systemName: "pause.fill")
                    .font(.system(size: iconSize, weight: .semibold))
                    .foregroundStyle(GameHudPalette.label)
                    .frame(width: buttonSize, height: buttonSize)
                    .glassPanelStyle(cornerRadius: buttonSize / 2)
                    .clipShape(.circle)
            }
            .buttonStyle(.plain)
            .keyboardShortcut("p", modifiers: [])
            .gameHoverEffect()
            .accessibilityLabel(Strings.paused)

            Button(action: actions.onHold) {
                Image(systemName: "arrow.left.arrow.right")
                    .font(.system(size: iconSize, weight: .semibold))
                    .foregroundStyle(GameHudPalette.label)
                    .frame(width: buttonSize, height: buttonSize)
                    .glassPanelStyle(cornerRadius: buttonSize / 2)
                    .clipShape(.circle)
            }
            .buttonStyle(.plain)
            .keyboardShortcut("h", modifiers: [])
            .gameHoverEffect()
            .accessibilityLabel(Strings.hold)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private struct GameBoardPane: View {
    let gameState: GameState
    let settings: GameSettings
    let ghostY: Int32?
    let actions: GameInputActions

    @State private var lastDragTranslation: CGSize = .zero
    @State private var didStartDragging = false
    @State private var lastReportedBoardHeight: CGFloat = 0

    private var boardAspectRatio: CGFloat {
        CGFloat(gameState.board.width) / CGFloat(gameState.board.height)
    }

    var body: some View {
        GeometryReader { geometry in
            let boardSize = fittedBoardSize(in: geometry.size)

            GameBoardView(
                gameState: gameState,
                settings: settings,
                ghostY: ghostY
            )
            .frame(width: boardSize.width, height: boardSize.height)
            .contentShape(Rectangle())
            .position(x: geometry.size.width / 2, y: geometry.size.height / 2)
            .gesture(boardGesture(boardWidth: boardSize.width))
            .onAppear {
                reportBoardHeight(boardSize.height)
            }
            .onChange(of: geometry.size) { _, newSize in
                let updatedBoardSize = fittedBoardSize(in: newSize)
                reportBoardHeight(updatedBoardSize.height)
            }
        }
        .glassPanelStyle(cornerRadius: 22)
        .clipShape(.rect(cornerRadius: 22))
    }

    private func boardGesture(boardWidth: CGFloat) -> some Gesture {
        TapGesture()
            .onEnded {
                actions.onRotate()
            }
            .simultaneously(
                with:
                    DragGesture(minimumDistance: 0)
                    .onChanged { value in
                        if !didStartDragging {
                            actions.onDragStarted()
                            didStartDragging = true
                        }

                        let deltaX = value.translation.width - lastDragTranslation.width
                        let deltaY = value.translation.height - lastDragTranslation.height
                        lastDragTranslation = value.translation

                        let tunedDeltaX = deltaX * horizontalDragSensitivity(for: boardWidth)
                        actions.onDragged(Float(tunedDeltaX), Float(deltaY))
                    }
                    .onEnded { _ in
                        finishDrag()
                    }
            )
    }

    private func horizontalDragSensitivity(for boardWidth: CGFloat) -> CGFloat {
        #if os(iOS)
        let columns = max(CGFloat(gameState.board.width), 1)
        let cellWidth = max(boardWidth / columns, 1)
        let targetSwipeDistance = max(cellWidth * 0.9, 22)
        let normalizedSensitivity = 50 / targetSwipeDistance
        return min(max(normalizedSensitivity, 1), 2.4)
        #else
        return 1
        #endif
    }

    private func fittedBoardSize(in container: CGSize) -> CGSize {
        guard container.width > 0, container.height > 0 else {
            return .zero
        }

        let widthBoundByHeight = container.height * boardAspectRatio

        if widthBoundByHeight <= container.width {
            return CGSize(width: widthBoundByHeight, height: container.height)
        }

        let heightBoundByWidth = container.width / boardAspectRatio
        return CGSize(width: container.width, height: heightBoundByWidth)
    }

    private func finishDrag() {
        if didStartDragging {
            actions.onDragEnded()
        }
        didStartDragging = false
        lastDragTranslation = .zero
    }

    private func reportBoardHeight(_ height: CGFloat) {
        let roundedHeight = height.rounded(.toNearestOrAwayFromZero)
        guard abs(roundedHeight - lastReportedBoardHeight) >= 2 else {
            return
        }

        lastReportedBoardHeight = roundedHeight
        actions.onBoardSizeChanged(Float(roundedHeight))
    }
}

private enum GameAdaptiveLayoutMode {
    case compact
    case medium
    case expanded
}

private struct GameAdaptiveConfiguration {
    let layoutMode: GameAdaptiveLayoutMode
    let metrics: GameAdaptiveMetrics

    init(
        size: CGSize,
        horizontalSizeClass: UserInterfaceSizeClass?,
        verticalSizeClass: UserInterfaceSizeClass?
    ) {
        let width = size.width
        let height = size.height
        let isShortHeight = height < 520

        if width < 650 || (horizontalSizeClass == .compact && !isShortHeight) {
            layoutMode = .compact
        } else if width < 1180 || verticalSizeClass == .compact {
            layoutMode = .medium
        } else {
            layoutMode = .expanded
        }

        metrics = GameAdaptiveMetrics(layoutMode: layoutMode, size: size, isShortHeight: isShortHeight)
    }
}

private struct GameAdaptiveMetrics {
    let horizontalPadding: CGFloat
    let verticalPadding: CGFloat
    let sectionSpacing: CGFloat
    let buttonSize: CGFloat
    let buttonIconSize: CGFloat
    let compactPieceSize: CGFloat
    let regularPieceSize: CGFloat
    let expandedPieceSize: CGFloat
    let secondaryPaneWidth: CGFloat
    let tertiaryPaneWidth: CGFloat

    init(layoutMode: GameAdaptiveLayoutMode, size: CGSize, isShortHeight: Bool) {
        switch layoutMode {
        case .compact:
            horizontalPadding = isShortHeight ? 6 : 8
            verticalPadding = isShortHeight ? 6 : 10
            sectionSpacing = isShortHeight ? 6 : 8
            buttonSize = isShortHeight ? 44 : 48
            buttonIconSize = isShortHeight ? 18 : 20
            compactPieceSize = isShortHeight ? 50 : 58
            regularPieceSize = compactPieceSize
            expandedPieceSize = compactPieceSize
            secondaryPaneWidth = 0
            tertiaryPaneWidth = 0
        case .medium:
            horizontalPadding = 10
            verticalPadding = 10
            sectionSpacing = 10
            buttonSize = 50
            buttonIconSize = 22
            compactPieceSize = 58
            regularPieceSize = 86
            expandedPieceSize = regularPieceSize
            secondaryPaneWidth = Self.clamp(size.width * 0.28, min: 200, max: 320)
            tertiaryPaneWidth = 0
        case .expanded:
            horizontalPadding = 12
            verticalPadding = 12
            sectionSpacing = 12
            buttonSize = 54
            buttonIconSize = 24
            compactPieceSize = 62
            regularPieceSize = 90
            expandedPieceSize = 110
            secondaryPaneWidth = Self.clamp(size.width * 0.19, min: 210, max: 300)
            tertiaryPaneWidth = Self.clamp(size.width * 0.17, min: 190, max: 270)
        }
    }

    private static func clamp(_ value: CGFloat, min minValue: CGFloat, max maxValue: CGFloat) -> CGFloat {
        Swift.max(minValue, Swift.min(maxValue, value))
    }
}

private extension GameState {
    var visibleNextPieces: [Tetromino] {
        var pieces: [Tetromino] = [nextPiece]
        pieces.append(contentsOf: nextQueue.prefix(2))
        return pieces
    }
}

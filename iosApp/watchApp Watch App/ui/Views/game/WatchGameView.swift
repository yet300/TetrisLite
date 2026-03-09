import SwiftUI
import Shared
import Foundation

struct WatchGameView: View {
    private let component: GameComponent

    @StateValue
    private var model: GameComponentModel

    @StateValue
    private var dialog: ChildSlot<AnyObject, GameComponentDialogChild>

    @StateValue
    private var sheet: ChildSlot<AnyObject, GameComponentSheetChild>

    @State private var crownValue: Double = 0.0
    @State private var isDragging: Bool = false
    @State private var lastTranslation: CGSize = .zero
    @State private var shakePhase: CGFloat = 0
    @State private var shakeAmount: CGFloat = 0
    @State private var contentScale: CGFloat = 1
    @State private var flashOpacity: Double = 0
    @State private var floatingTexts: [AppleGameFloatingTextEntry] = []
    @State private var particleBursts: [AppleGameParticleBurstEntry] = []
    @State private var lineSweeps: [AppleGameLineSweepEntry] = []
    @State private var lockGlows: [AppleGameLockGlowEntry] = []

    init(_ component: GameComponent) {
        self.component = component
        _model = StateValue(component.model)
        _dialog = StateValue(component.childSlot)
        _sheet = StateValue(component.sheetSlot)
    }

    var body: some View {
        ZStack {
            WatchBackground()
            if model.isLoading {
                ProgressView()
            } else {
                HStack(spacing: 2) {
                    // LEFT: Game Board (Maximized)
                    if let gameState = model.gameState {
                        GeometryReader { geometry in
                            WatchMiniBoardView(
                                gameState: gameState,
                                settings: model.settings,
                                ghostY: model.ghostPieceY?.int32Value,
                                lineSweeps: lineSweeps,
                                lockGlows: lockGlows
                            )
                            .onAppear {
                                component.onBoardSizeChanged(height: Float(geometry.size.height))
                            }
                            .onChange(of: geometry.size.height) { _, newHeight in
                                component.onBoardSizeChanged(height: Float(newHeight))
                            }
                        }
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .contentShape(Rectangle())
                        .gesture(
                            TapGesture()
                            .onEnded {
                                component.onRotate()
                                WKInterfaceDevice.current().play(.click)
                            }
                            .simultaneously(with: LongPressGesture(minimumDuration: 0.5)
                                .onEnded { _ in
                                    component.onHardDrop()
                                    WKInterfaceDevice.current().play(.success)
                                }
                            )
                        )
                        .gesture(
                            DragGesture(minimumDistance: 5)
                            .onChanged { value in
                                if !isDragging {
                                    component.onDragStarted()
                                    isDragging = true
                                }

                                let diffX = value.translation.width - lastTranslation.width
                                let diffY = value.translation.height - lastTranslation.height

                                // Move piece horizontally based on drag threshold
                                if abs(diffX) > 18 {
                                    if diffX > 0 {
                                        component.onMoveRight()
                                    } else {
                                        component.onMoveLeft()
                                    }
                                    lastTranslation.width = value.translation.width
                                    WKInterfaceDevice.current().play(.click)
                                }

                                // Move piece down based on drag threshold
                                if diffY > 15 {
                                    component.onMoveDown()
                                    lastTranslation.height = value.translation.height
                                    WKInterfaceDevice.current().play(.click)
                                }
                            }
                            .onEnded { value in
                                // Hard drop on strong downward flick
                                if value.translation.height > 60 {
                                    component.onHardDrop()
                                    WKInterfaceDevice.current().play(.success)
                                }

                                component.onDragEnded()
                                isDragging = false
                                lastTranslation = .zero
                            }
                        )
                    } else {
                        ProgressView()
                    }

                    // RIGHT: Sidebar (Icon-only with labels)
                    VStack(spacing: 4) {
                        if let gameState = model.gameState {
                            // Mini Next Piece
                            VStack(spacing: 0) {
                                Text(Strings.next.uppercased())
                                    .font(.system(size: 6, weight: .bold))
                                    .foregroundStyle(themeAccentColor(theme: model.settings.themeConfig.visualTheme))
                                WatchNextPieceView(piece: gameState.nextPiece, settings: model.settings)
                                    .frame(width: 20, height: 20)
                            }
                        }

                        VStack(spacing: 0) {
                            Text(Strings.score.uppercased())
                                .font(.system(size: 6, weight: .bold))
                                .foregroundStyle(themeAccentColor(theme: model.settings.themeConfig.visualTheme))
                            Text(formatScore(model.gameState?.score ?? 0))
                                .font(.system(size: 9, weight: .bold, design: .monospaced))
                                .minimumScaleFactor(0.5)
                                .lineLimit(1)
                        }

                        VStack(spacing: 0) {
                            Text(Strings.level.uppercased())
                                .font(.system(size: 6, weight: .bold))
                                .foregroundStyle(themeAccentColor(theme: model.settings.themeConfig.visualTheme))
                            Text("\(model.gameState?.level ?? Int32(1))")
                                .font(.system(size: 9, weight: .bold, design: .monospaced))
                                .minimumScaleFactor(0.5)
                                .lineLimit(1)
                        }

                        VStack(spacing: 0) {
                            Text(Strings.time.uppercased())
                                .font(.system(size: 6, weight: .bold))
                                .foregroundStyle(themeAccentColor(theme: model.settings.themeConfig.visualTheme))
                            Text(formatElapsedTime(model.elapsedTime))
                                .font(.system(size: 9, weight: .bold, design: .monospaced))
                        }

                        Spacer()

                        Button {
                            WKInterfaceDevice.current().play(.click)
                            component.onPause()
                        } label: {
                            ZStack {
                                Circle()
                                    .fill(themeAccentColor(theme: model.settings.themeConfig.visualTheme).opacity(0.15))
                                    .frame(width: 24, height: 24)
                                    .overlay(
                                        Circle().stroke(themeAccentColor(theme: model.settings.themeConfig.visualTheme).opacity(0.3), lineWidth: 1)
                                    )
                                Image(systemName: "pause.fill")
                                    .font(.system(size: 8, weight: .bold))
                                    .foregroundStyle(themeAccentColor(theme: model.settings.themeConfig.visualTheme))
                            }
                            .contentShape(Circle())
                        }
                        .buttonStyle(.plain)
                    }
                    .frame(width: 28)
                }
                .padding(.horizontal, 1)
                .modifier(
                    WatchJuiceModifier(
                        shakePhase: shakePhase,
                        shakeAmount: shakeAmount,
                        scale: contentScale
                    )
                )
            }

            AppleGameEffectsOverlay(
                profile: .watch,
                theme: model.settings.themeConfig.visualTheme,
                flashOpacity: flashOpacity,
                floatingTexts: floatingTexts,
                particleBursts: particleBursts
            )

            if let child = dialog.child?.instance {
                WatchDialogView(component: component, model: model, child: child)
                    .transition(.opacity)
            }
        }
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
            WatchSheetView(child: sheetItem.child)
        }
        .tint(WatchPalette.accent)
        .navigationBarBackButtonHidden(true)
        .focusable()
        .digitalCrownRotation($crownValue, from: -1000, through: 1000, by: 1, sensitivity: .high, isContinuous: true, isHapticFeedbackEnabled: true)
        .onChange(of: crownValue) { oldValue, newValue in
            let delta = newValue - oldValue
            if abs(delta) >= 1.0 {
                if delta > 0 {
                    component.onMoveRight()
                } else {
                    component.onMoveLeft()
                }
            }
        }
        .onChange(of: model.visualEffectFeed.sequence) { _, newSequence in
            handleVisualEffectFeedChange(newSequence)
        }
    }

    private func handleVisualEffectFeedChange(_ sequence: Int64) {
        guard let burst = model.visualEffectFeed.latest else {
            return
        }
        triggerJuice(burst)
        component.onVisualEffectConsumed(sequence: sequence)
    }

    private func triggerJuice(_ burst: VisualEffectBurst) {
        for event in burst.events {
            switch event {
            case let shake as VisualEffectEventScreenShake:
                triggerShake(
                    intensity: shake.intensity,
                    power: CGFloat(shake.power)
                )
            case let flash as VisualEffectEventScreenFlash:
                triggerFlash(power: CGFloat(flash.power))
            case let text as VisualEffectEventFloatingText:
                addFloatingText(
                    resolveFloatingTextMessage(
                        text.textKey,
                        comboStreak: Int(burst.comboStreak)
                    ),
                    intensity: text.intensity,
                    power: CGFloat(text.power)
                )
                let clearedRows = clearedRows(from: burst)
                if isLineClearTextKey(text.textKey) && !clearedRows.isEmpty {
                    addLineSweep(
                        burstId: burst.id,
                        clearedRows: clearedRows,
                        intensity: text.intensity,
                        power: CGFloat(text.power)
                    )
                }
            case let explosion as VisualEffectEventExplosion:
                let lockCells = lockCells(from: burst)
                addParticleBurst(
                    burstId: burst.id,
                    intensity: explosion.intensity,
                    power: CGFloat(explosion.power),
                    particleCount: Int(explosion.particleCount)
                )
                if !lockCells.isEmpty {
                    addLockGlow(
                        burstId: burst.id,
                        lockedCells: lockCells,
                        intensity: explosion.intensity,
                        power: CGFloat(explosion.power)
                    )
                }
            default:
                continue
            }
        }
    }

    private func triggerShake(
        intensity: IntensityLevel,
        power: CGFloat
    ) {
        let isHigh = intensity == .high
        let amplitude = isHigh ? 6 + (9 * power) : 1.8 + (3.2 * power)

        shakeAmount = amplitude
        withAnimation(.linear(duration: isHigh ? 0.24 : 0.17)) {
            shakePhase += 1
        }

        withAnimation(.spring(response: 0.24, dampingFraction: 0.66)) {
            contentScale = isHigh ? 1.03 : 1.015
        }

        DispatchQueue.main.asyncAfter(deadline: .now() + 0.18) {
            withAnimation(.spring(response: 0.20, dampingFraction: 0.84)) {
                contentScale = 1
            }
        }
    }

    private func triggerFlash(power: CGFloat) {
        flashOpacity = Double(0.34 + (0.34 * power))
        withAnimation(.easeOut(duration: 0.16)) {
            flashOpacity = 0
        }
    }

    private func addFloatingText(
        _ text: String,
        intensity: IntensityLevel,
        power: CGFloat
    ) {
        let isHigh = intensity == .high
        let entry = AppleGameFloatingTextEntry(
            id: UUID().uuidString,
            text: text,
            isHigh: isHigh,
            power: power,
            createdAt: Date(),
            duration: isHigh ? 0.95 : 0.65
        )
        floatingTexts.append(entry)

        DispatchQueue.main.asyncAfter(deadline: .now() + entry.duration + 0.08) {
            floatingTexts.removeAll {
                $0.id == entry.id
            }
        }
    }

    private func addParticleBurst(
        burstId: Int64,
        intensity: IntensityLevel,
        power: CGFloat,
        particleCount: Int
    ) {
        let entry = AppleGameParticleBurstEntry(
            id: "\(burstId)-\(UUID().uuidString)",
            isHigh: intensity == .high,
            power: power,
            particleCount: max(16, min(64, particleCount)),
            seed: Int(burstId),
            createdAt: Date(),
            duration: 0.5
        )
        particleBursts.append(entry)

        DispatchQueue.main.asyncAfter(deadline: .now() + entry.duration + 0.08) {
            particleBursts.removeAll {
                $0.id == entry.id
            }
        }
    }

    private func addLineSweep(
        burstId: Int64,
        clearedRows: [Int],
        intensity: IntensityLevel,
        power: CGFloat
    ) {
        let entry = AppleGameLineSweepEntry(
            id: "sweep-\(burstId)-\(UUID().uuidString)",
            clearedRows: clearedRows,
            isHigh: intensity == .high,
            power: power,
            createdAt: Date(),
            duration: 0.36
        )
        lineSweeps.append(entry)

        DispatchQueue.main.asyncAfter(deadline: .now() + entry.duration + 0.08) {
            lineSweeps.removeAll {
                $0.id == entry.id
            }
        }
    }

    private func clearedRows(from burst: VisualEffectBurst) -> [Int] {
        burst.clearedRows.map { $0.intValue }
    }

    private func lockCells(from burst: VisualEffectBurst) -> [AppleGameBoardCell] {
        burst.lockCells.map {
            AppleGameBoardCell(x: Int($0.x), y: Int($0.y))
        }
    }

    private func addLockGlow(
        burstId: Int64,
        lockedCells: [AppleGameBoardCell],
        intensity: IntensityLevel,
        power: CGFloat
    ) {
        let entry = AppleGameLockGlowEntry(
            id: "glow-\(burstId)-\(UUID().uuidString)",
            lockedCells: lockedCells,
            isHigh: intensity == .high,
            power: power,
            createdAt: Date(),
            duration: 0.32
        )
        lockGlows.append(entry)

        DispatchQueue.main.asyncAfter(deadline: .now() + entry.duration + 0.08) {
            lockGlows.removeAll {
                $0.id == entry.id
            }
        }
    }

    private func resolveFloatingTextMessage(_ textKey: VisualTextKey, comboStreak: Int) -> String {
        let base: String
        switch textKey {
        case .single:
            base = "SINGLE!"
        case .double_:
            base = "DOUBLE!"
        case .triple:
            base = "TRIPLE!"
        case .tetris:
            base = "TETRIS!!!"
        case .clear:
            base = "CLEAR!"
        default:
            base = "CLEAR!"
        }

        if comboStreak >= 2 {
            return "\(base) COMBO x\(comboStreak)!"
        }
        return base
    }

    private func formatElapsedTime(_ milliseconds: Int64) -> String {
        let seconds = (milliseconds / 1000) % 60
        let minutes = (milliseconds / 1000) / 60
        return String(format: "%d:%02d", minutes, seconds)
    }

    private func formatScore(_ score: Int64) -> String {
        if score > 9999 {
            return "\(score / 1000)k"
        }
        return "\(score)"
    }
}

// MARK: - Dialog & Sheet Routing
private struct WatchDialogView: View {
    let component: GameComponent
    let model: GameComponentModel
    let child: GameComponentDialogChild

    var body: some View {
        switch child {
        case is GameComponentDialogChildGameOver:
            WatchGameOverScreen(
                score: model.finalScore,
                onRetry: { component.onRetry() },
                onQuit: { component.onQuit() }
            )
        case is GameComponentDialogChildPause:
            WatchPauseScreen(
                score: model.gameState?.score ?? 0,
                lines: model.gameState?.linesCleared ?? 0,
                accentColor: themeAccentColor(theme: model.settings.themeConfig.visualTheme),
                onResume: { component.onResume() },
                onSettings: { component.onSettings() },
                onQuit: { component.onQuit() }
            )
        case let child as GameComponentDialogChildError:
            WatchErrorScreen(message: child.message, onDismiss: component.onDismissDialog)
        default:
            EmptyView()
        }
    }
}

private struct WatchSheetView: View {
    let child: GameComponentSheetChild

    var body: some View {
        switch child {
        case let child as GameComponentSheetChildSettings:
            WatchSettingsView(child.component)
        default:
            EmptyView()
        }
    }
}

private struct SheetItem: Identifiable {
    let child: GameComponentSheetChild
    var id: String {
        if child is GameComponentSheetChildSettings {
            return "settings"
        }
        return UUID().uuidString
    }
}

private struct WatchErrorScreen: View {
    let message: String
    let onDismiss: () -> Void

    var body: some View {
        ZStack {
            Rectangle()
                .fill(.ultraThinMaterial)
                .ignoresSafeArea()

            VStack(spacing: 8) {
                Image(systemName: "exclamationmark.triangle.fill")
                    .font(.title3)
                    .foregroundStyle(.yellow)

                Text(message)
                    .font(.caption2)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)

                WatchButton(title: Strings.ok, systemImage: "check", action: onDismiss)
            }
        }
    }
}

// MARK: - Pause Screen
private struct WatchPauseScreen: View {
    let score: Int64
    let lines: Int64
    let accentColor: Color
    let onResume: () -> Void
    let onSettings: () -> Void
    let onQuit: () -> Void

    var body: some View {
        ZStack {
            Rectangle()
                .fill(.ultraThinMaterial)
                .ignoresSafeArea()

            VStack(spacing: 8) {
                VStack(spacing: 2) {
                    Text(Strings.paused)
                        .font(.headline)
                        .foregroundStyle(accentColor)
                    Text("\(Strings.score): \(score) • \(Strings.lines): \(lines)")
                        .font(.system(size: 8, weight: .medium, design: .monospaced))
                        .foregroundStyle(.secondary)
                }

                VStack(spacing: 4) {
                    WatchButton(
                        title: Strings.resume,
                        systemImage: "play.fill",
                        action: onResume,
                        backgroundColor: accentColor.opacity(0.15),
                        iconColor: accentColor
                    )

                    WatchButton(title: Strings.settings, systemImage: "gearshape.fill", action: onSettings)

                    WatchButton(
                        title: Strings.quit,
                        systemImage: "xmark.circle.fill",
                        action: onQuit,
                        backgroundColor: .red.opacity(0.2),
                        iconColor: .red
                    )
                }
                .padding(.horizontal, 8)
            }
        }
    }
}

// MARK: - Game Over Screen
private struct WatchGameOverScreen: View {
    let score: Int64
    let onRetry: () -> Void
    let onQuit: () -> Void

    var body: some View {
        ZStack {
            Rectangle()
                .fill(.ultraThinMaterial)
                .ignoresSafeArea()

            VStack(spacing: 6) {
                VStack(spacing: 2) {
                    Text(Strings.gameOver)
                        .font(.headline)
                        .foregroundStyle(.red)
                    Text(Strings.finalScore(Int(score)))
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                }

                VStack(spacing: 4) {
                    WatchButton(
                        title: Strings.retry,
                        systemImage: "arrow.clockwise",
                        action: onRetry,
                        backgroundColor: WatchPalette.accent.opacity(0.15),
                        iconColor: WatchPalette.accent
                    )

                    WatchButton(
                        title: Strings.quit,
                        systemImage: "xmark.circle.fill",
                        action: onQuit,
                        backgroundColor: .red.opacity(0.2),
                        iconColor: .red
                    )
                }
                .padding(.horizontal, 8)
            }
        }
    }
}

// MARK: - Mini Components
private struct WatchNextPieceView: View {
    let piece: Tetromino
    let settings: GameSettings

    var body: some View {
        Canvas { context, size in
            let cellSize: CGFloat = 5
            let blocks = piece.blocks

            let minX = blocks.map {
                $0.x
            }
            .min() ?? 0
            let maxX = blocks.map {
                $0.x
            }
            .max() ?? 0
            let minY = blocks.map {
                $0.y
            }
            .min() ?? 0
            let maxY = blocks.map {
                $0.y
            }
            .max() ?? 0

            let pieceWidth = CGFloat(maxX - minX + 1) * cellSize
            let pieceHeight = CGFloat(maxY - minY + 1) * cellSize

            let offsetX = (size.width - pieceWidth) / 2 - CGFloat(minX) * cellSize
            let offsetY = (size.height - pieceHeight) / 2 - CGFloat(minY) * cellSize

            for block in blocks {
                let x = CGFloat(block.x) * cellSize + offsetX
                let y = CGFloat(block.y) * cellSize + offsetY

                context.drawStyledBlock(
                    type: piece.type,
                    settings: settings,
                    topLeft: CGPoint(x: x, y: y),
                    cellSize: cellSize
                )
            }
        }
    }
}

private struct WatchMiniBoardView: View {
    let gameState: GameState
    let settings: GameSettings
    let ghostY: Int32?
    let lineSweeps: [AppleGameLineSweepEntry]
    let lockGlows: [AppleGameLockGlowEntry]

    var body: some View {
        GeometryReader { geometry in
            let widthCellSize = geometry.size.width / CGFloat(gameState.board.width)
            let heightCellSize = geometry.size.height / CGFloat(gameState.board.height)
            let cellSize = min(widthCellSize, heightCellSize)

            let boardWidth = cellSize * CGFloat(gameState.board.width)
            let boardHeight = cellSize * CGFloat(gameState.board.height)

            let offsetX = (geometry.size.width - boardWidth) / 2
            let offsetY = (geometry.size.height - boardHeight) / 2

            TimelineView(.animation(minimumInterval: 1.0 / 20.0)) { timeline in
                Canvas { context, size in
                    let boardRect = CGRect(x: offsetX, y: offsetY, width: boardWidth, height: boardHeight)

                    context.drawBoardChrome(
                        settings: settings,
                        boardRect: boardRect,
                        columns: Int(gameState.board.width),
                        rows: Int(gameState.board.height),
                        cellSize: cellSize,
                        profile: .watch,
                        shimmerPhase: boardShimmerPhase(at: timeline.date)
                    )

                    context.drawBoardGrid(
                        theme: settings.themeConfig.visualTheme,
                        boardRect: boardRect,
                        columns: Int(gameState.board.width),
                        rows: Int(gameState.board.height),
                        cellSize: cellSize,
                        profile: .watch
                    )

                    for (pos, tetrominoType) in gameState.board.cells {
                        if pos.y >= 0 {
                            context.drawStyledBlock(
                                type: tetrominoType,
                                settings: settings,
                                topLeft: CGPoint(x: offsetX + CGFloat(pos.x) * cellSize, y: offsetY + CGFloat(pos.y) * cellSize),
                                cellSize: cellSize
                            )
                        }
                    }

                    if let piece = gameState.currentPiece, let gy = ghostY {
                        for blockPos in piece.blocks {
                            let absoluteX = gameState.currentPosition.x + blockPos.x
                            let absoluteY = gy + blockPos.y
                            if absoluteY >= 0 && absoluteY < gameState.board.height {
                                context.drawStyledBlock(
                                    type: piece.type,
                                    settings: settings,
                                    topLeft: CGPoint(x: offsetX + CGFloat(absoluteX) * cellSize, y: offsetY + CGFloat(absoluteY) * cellSize),
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
                                    topLeft: CGPoint(x: offsetX + CGFloat(absoluteX) * cellSize, y: offsetY + CGFloat(absoluteY) * cellSize),
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
                }
            }
        }
    }
}

private struct WatchJuiceShakeEffect: GeometryEffect {
    var amount: CGFloat
    var phase: CGFloat

    var animatableData: CGFloat {
        get {
            phase
        }
        set {
            phase = newValue
        }
    }

    func effectValue(size: CGSize) -> ProjectionTransform {
        let x = amount * sin(phase * .pi * 12)
        let y = amount * 0.32 * cos(phase * .pi * 9)
        return ProjectionTransform(CGAffineTransform(translationX: x, y: y))
    }
}

private struct WatchJuiceModifier: ViewModifier {
    let shakePhase: CGFloat
    let shakeAmount: CGFloat
    let scale: CGFloat

    func body(content: Content) -> some View {
        content
            .modifier(WatchJuiceShakeEffect(amount: shakeAmount, phase: shakePhase))
            .scaleEffect(scale)
    }
}

#Preview {
    WatchGameView(PreviewGameComponent())
}

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
    @State private var floatingTexts: [WatchJuiceFloatingTextEntry] = []
    @State private var particleBursts: [WatchJuiceParticleBurstEntry] = []

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
                                ghostY: model.ghostPieceY?.int32Value
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
                                    .foregroundStyle(getAccentColor(for: model.settings.themeConfig.visualTheme))
                                WatchNextPieceView(piece: gameState.nextPiece, settings: model.settings)
                                    .frame(width: 20, height: 20)
                            }
                        }

                        VStack(spacing: 0) {
                            Text(Strings.score.uppercased())
                                .font(.system(size: 6, weight: .bold))
                                .foregroundStyle(getAccentColor(for: model.settings.themeConfig.visualTheme))
                            Text(formatScore(model.gameState?.score ?? 0))
                                .font(.system(size: 9, weight: .bold, design: .monospaced))
                                .minimumScaleFactor(0.5)
                                .lineLimit(1)
                        }

                        VStack(spacing: 0) {
                            Text(Strings.level.uppercased())
                                .font(.system(size: 6, weight: .bold))
                                .foregroundStyle(getAccentColor(for: model.settings.themeConfig.visualTheme))
                            Text("\(model.gameState?.level ?? Int32(1))")
                                .font(.system(size: 9, weight: .bold, design: .monospaced))
                                .minimumScaleFactor(0.5)
                                .lineLimit(1)
                        }

                        VStack(spacing: 0) {
                            Text(Strings.time.uppercased())
                                .font(.system(size: 6, weight: .bold))
                                .foregroundStyle(getAccentColor(for: model.settings.themeConfig.visualTheme))
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
                                    .fill(getAccentColor(for: model.settings.themeConfig.visualTheme).opacity(0.15))
                                    .frame(width: 24, height: 24)
                                    .overlay(
                                        Circle().stroke(getAccentColor(for: model.settings.themeConfig.visualTheme).opacity(0.3), lineWidth: 1)
                                    )
                                Image(systemName: "pause.fill")
                                    .font(.system(size: 8, weight: .bold))
                                    .foregroundStyle(getAccentColor(for: model.settings.themeConfig.visualTheme))
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

            WatchJuiceOverlayView(
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
            case let explosion as VisualEffectEventExplosion:
                addParticleBurst(
                    burstId: burst.id,
                    intensity: explosion.intensity,
                    power: CGFloat(explosion.power),
                    particleCount: Int(explosion.particleCount)
                )
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
        let entry = WatchJuiceFloatingTextEntry(
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
        let entry = WatchJuiceParticleBurstEntry(
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
                accentColor: getAccentColor(for: model.settings.themeConfig.visualTheme),
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

                drawStyledBlock(
                    context: &context,
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

    var body: some View {
        GeometryReader { geometry in
            let widthCellSize = geometry.size.width / CGFloat(gameState.board.width)
            let heightCellSize = geometry.size.height / CGFloat(gameState.board.height)
            let cellSize = min(widthCellSize, heightCellSize)

            let boardWidth = cellSize * CGFloat(gameState.board.width)
            let boardHeight = cellSize * CGFloat(gameState.board.height)

            let offsetX = (geometry.size.width - boardWidth) / 2
            let offsetY = (geometry.size.height - boardHeight) / 2

            Canvas { context, size in
                let boardRect = CGRect(x: offsetX, y: offsetY, width: boardWidth, height: boardHeight)
                let boardPath = RoundedRectangle(cornerRadius: 6, style: .continuous).path(in: boardRect)
                context.fill(boardPath, with: .color(getBackgroundColor(for: settings.themeConfig.visualTheme)))

                // Grid lines (optional for mini board)
                drawGrid(context: &context, size: CGSize(width: boardWidth, height: boardHeight), offsetX: offsetX, offsetY: offsetY, cellSize: cellSize, theme: settings.themeConfig.visualTheme)

                for (pos, tetrominoType) in gameState.board.cells {
                    if pos.y >= 0 {
                        drawStyledBlock(
                            context: &context,
                            type: tetrominoType,
                            settings: settings,
                            topLeft: CGPoint(x: offsetX + CGFloat(pos.x) * cellSize, y: offsetY + CGFloat(pos.y) * cellSize),
                            cellSize: cellSize
                        )
                    }
                }

                // Ghost Piece
                if let piece = gameState.currentPiece, let gy = ghostY {
                    for blockPos in piece.blocks {
                        let absoluteX = gameState.currentPosition.x + blockPos.x
                        let absoluteY = gy + blockPos.y
                        if absoluteY >= 0 && absoluteY < gameState.board.height {
                            drawStyledBlock(
                                context: &context,
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
                            drawStyledBlock(
                                context: &context,
                                type: piece.type,
                                settings: settings,
                                topLeft: CGPoint(x: offsetX + CGFloat(absoluteX) * cellSize, y: offsetY + CGFloat(absoluteY) * cellSize),
                                cellSize: cellSize
                            )
                        }
                    }
                }
            }
        }
    }

    private func getBackgroundColor(for theme: VisualTheme) -> Color {
        switch theme {
        case .retroGameboy: return Color(red: 0.6, green: 0.73, blue: 0.05)
        case .ocean: return Color(red: 0.0, green: 0.1, blue: 0.25)
        case .forest: return Color(red: 0.05, green: 0.12, blue: 0.05)
        default: return WatchPalette.boardBackground
        }
    }

    private func drawGrid(context: inout GraphicsContext, size: CGSize, offsetX: CGFloat, offsetY: CGFloat, cellSize: CGFloat, theme: VisualTheme) {
        let gridColor: Color
        switch theme {
        case .retroGameboy: gridColor = Color(red: 0.54, green: 0.67, blue: 0.05)
        case .neon: gridColor = Color(red: 0.0, green: 1.0, blue: 1.0).opacity(0.1)
        default: gridColor = Color.white.opacity(0.05)
        }

        context.stroke(
            Path { path in
                for x in 0...10 {
                    path.move(to: CGPoint(x: offsetX + CGFloat(x) * cellSize, y: offsetY))
                    path.addLine(to: CGPoint(x: offsetX + CGFloat(x) * cellSize, y: offsetY + size.height))
                }
                for y in 0...20 {
                    path.move(to: CGPoint(x: offsetX, y: offsetY + CGFloat(y) * cellSize))
                    path.addLine(to: CGPoint(x: offsetX + size.width, y: offsetY + CGFloat(y) * cellSize))
                }
            },
            with: .color(gridColor),
            lineWidth: 0.5
        )
    }
}

// MARK: - Styled Block Drawing Helper
extension View {
    func drawStyledBlock(
        context: inout GraphicsContext,
        type: TetrominoType,
        settings: GameSettings,
        topLeft: CGPoint,
        cellSize: CGFloat,
        alpha: CGFloat = 1.0
    ) {
        let baseColor = getTetrominoColor(type: type, theme: settings.themeConfig.visualTheme).opacity(alpha)
        let blockSize = CGSize(width: cellSize - 0.5, height: cellSize - 0.5)
        let rect = CGRect(origin: topLeft, size: blockSize)

        switch settings.themeConfig.pieceStyle {
        case .bordered:
            context.fill(Path(rect), with: .color(baseColor))
            let innerRect = rect.insetBy(dx: cellSize * 0.15, dy: cellSize * 0.15)
            context.stroke(Path(rect), with: .color(.white.opacity(alpha * 0.3)), lineWidth: 0.5)
            context.fill(Path(innerRect), with: .color(.white.opacity(alpha * 0.2)))

        case .gradient:
            let gradient = Gradient(colors: [baseColor, baseColor.opacity(0.6)])
            context.fill(Path(rect), with: .linearGradient(gradient, startPoint: topLeft, endPoint: CGPoint(x: topLeft.x + blockSize.width, y: topLeft.y + blockSize.height)))

        case .retroPixel:
            context.fill(Path(rect), with: .color(baseColor))
            let pixelSize = cellSize / 3
            let highlightRect = CGRect(x: topLeft.x, y: topLeft.y, width: pixelSize, height: pixelSize)
            context.fill(Path(highlightRect), with: .color(.white.opacity(alpha * 0.3)))

        case .glass:
            let path = RoundedRectangle(cornerRadius: cellSize * 0.2, style: .continuous).path(in: rect)
            context.fill(path, with: .color(baseColor.opacity(0.7)))
            context.stroke(path, with: .color(.white.opacity(alpha * 0.5)), lineWidth: 0.5)

        default: // Solid
            context.fill(Path(rect), with: .color(baseColor))
        }
    }

    private func getTetrominoColor(type: TetrominoType, theme: VisualTheme) -> Color {
        switch theme {
        case .retroGameboy:
            return (type == .i || type == .t || type == .z || type == .l) ? Color(red: 0.05, green: 0.22, blue: 0.05) : Color(red: 0.19, green: 0.38, blue: 0.19)

        case .monochrome:
            switch type {
            case .i: return .white
            case .o: return .gray.opacity(0.9)
            case .t: return .gray.opacity(0.8)
            case .s: return .gray.opacity(0.7)
            case .z: return .gray.opacity(0.6)
            case .j: return .gray.opacity(0.5)
            case .l: return .gray.opacity(0.4)
            default: return .white
            }

        case .neon:
            switch type {
            case .i: return Color(red: 0.0, green: 1.0, blue: 1.0)
            case .o: return Color(red: 1.0, green: 1.0, blue: 0.0)
            case .t: return Color(red: 1.0, green: 0.0, blue: 1.0)
            case .s: return Color(red: 0.0, green: 1.0, blue: 0.0)
            case .z: return Color(red: 1.0, green: 0.0, blue: 0.4)
            case .j: return Color(red: 0.0, green: 0.4, blue: 1.0)
            case .l: return Color(red: 1.0, green: 0.4, blue: 0.0)
            default: return .white
            }

        default: // Classic & Others
            switch type {
            case .i: return Color(red: 0.0, green: 0.94, blue: 0.94)
            case .o: return Color(red: 0.94, green: 0.94, blue: 0.0)
            case .t: return Color(red: 0.63, green: 0.0, blue: 0.94)
            case .s: return Color(red: 0.0, green: 0.94, blue: 0.0)
            case .z: return Color(red: 0.94, green: 0.0, blue: 0.0)
            case .j: return Color(red: 0.0, green: 0.0, blue: 0.94)
            case .l: return Color(red: 0.94, green: 0.63, blue: 0.0)
            default: return .gray
            }
        }
    }

    func getAccentColor(for theme: VisualTheme) -> Color {
        switch theme {
        case .classic: return WatchPalette.accent
        case .retroGameboy: return Color(red: 0.05, green: 0.22, blue: 0.05)
        case .retroNes: return Color(red: 1.0, green: 0.47, blue: 0.0)
        case .neon: return Color(red: 0.0, green: 1.0, blue: 1.0)
        case .pastel: return Color(red: 0.7, green: 0.9, blue: 1.0)
        case .monochrome: return .white
        case .ocean: return Color(red: 0.0, green: 0.8, blue: 1.0)
        case .sunset: return Color(red: 1.0, green: 0.4, blue: 0.2)
        case .forest: return Color(red: 0.1, green: 0.8, blue: 0.1)
        default: return WatchPalette.accent
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

private struct WatchJuiceFloatingTextEntry: Identifiable {
    let id: String
    let text: String
    let isHigh: Bool
    let power: CGFloat
    let createdAt: Date
    let duration: TimeInterval

    func progress(at date: Date) -> CGFloat {
        guard duration > 0 else {
            return 1
        }
        let elapsed = date.timeIntervalSince(createdAt)
        let raw = elapsed / duration
        return CGFloat(min(max(raw, 0), 1))
    }
}

private struct WatchJuiceParticleBurstEntry: Identifiable {
    let id: String
    let isHigh: Bool
    let power: CGFloat
    let particleCount: Int
    let seed: Int
    let createdAt: Date
    let duration: TimeInterval

    func progress(at date: Date) -> CGFloat {
        guard duration > 0 else {
            return 1
        }
        let elapsed = date.timeIntervalSince(createdAt)
        let raw = elapsed / duration
        return CGFloat(min(max(raw, 0), 1))
    }
}

private struct WatchJuiceOverlayView: View {
    let flashOpacity: Double
    let floatingTexts: [WatchJuiceFloatingTextEntry]
    let particleBursts: [WatchJuiceParticleBurstEntry]

    var body: some View {
        ZStack {
            if flashOpacity > 0.001 {
                Color.white
                    .opacity(flashOpacity)
                    .ignoresSafeArea()
            }

            TimelineView(.animation(minimumInterval: 1.0 / 45.0)) { timeline in
                ZStack {
                    ForEach(floatingTexts) { entry in
                        let progress = entry.progress(at: timeline.date)
                        if progress < 1 {
                            let pulse =
                                entry.isHigh
                                    ? 1 + (sin(progress * .pi * 8) * 0.08 * (1 - progress))
                                    : 1 + ((1 - progress) * 0.02)
                            let fontSize = entry.isHigh ? 18 + (4 * entry.power) : 11 + (2 * entry.power)
                            let rise = entry.isHigh ? 40.0 : 24.0

                            Text(entry.text)
                                .font(
                                    .system(
                                        size: fontSize,
                                        weight: .black,
                                        design: .rounded
                                    )
                                )
                                .foregroundStyle(entry.isHigh ? Color.yellow : Color.white)
                                .kerning(entry.isHigh ? 0.8 : 0.4)
                                .watchGameStroke(
                                    color: entry.isHigh ? Color(red: 0.2, green: 0.08, blue: 0) : Color.black.opacity(0.95),
                                    width: entry.isHigh ? 1.8 : 1.2
                                )
                                .scaleEffect(pulse)
                                .offset(y: -(rise * progress))
                                .opacity(1 - progress)
                        }
                    }

                    Canvas { context, size in
                        let center = CGPoint(x: size.width / 2, y: size.height / 2)

                        for burst in particleBursts {
                            let progress = burst.progress(at: timeline.date)
                            guard progress < 1 else {
                                continue
                            }

                            let maxRadius: CGFloat = 22 + ((48 - 22) * burst.power)
                            let alpha = (1 - progress) * (0.6 + (0.3 * burst.power))

                            for index in 0..<burst.particleCount {
                                let angle = watchSeededFloat(seed: burst.seed, index: index, salt: 11) * (.pi * 2)
                                let speedScale = 0.45 + (watchSeededFloat(seed: burst.seed, index: index, salt: 23) * 0.75)
                                let radius = maxRadius * progress * speedScale
                                let x = center.x + cos(angle) * radius
                                let y = center.y + sin(angle) * radius - (progress * 8)
                                let particleRadius = 0.8 + (watchSeededFloat(seed: burst.seed, index: index, salt: 37) * 1.6)

                                let rect = CGRect(
                                    x: x - particleRadius,
                                    y: y - particleRadius,
                                    width: particleRadius * 2,
                                    height: particleRadius * 2
                                )

                                context.fill(
                                    Path(ellipseIn: rect),
                                    with: .color((burst.isHigh ? Color(red: 1, green: 0.95, blue: 0.7) : .white).opacity(alpha))
                                )
                            }
                        }
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
        .allowsHitTesting(false)
    }
}

private func watchSeededFloat(
    seed: Int,
    index: Int,
    salt: Int
) -> CGFloat {
    var value = Int64(seed) * 1_103_515_245 + Int64(index) * 12_345 + Int64(salt) * 1_013_904_223
    value ^= (value << 13)
    value ^= (value >> 17)
    value ^= (value << 5)

    let positive = value & 0x7fff_ffff
    return CGFloat(Double(positive) / Double(0x7fff_ffff))
}

private extension View {
    func watchGameStroke(
        color: Color,
        width: CGFloat
    ) -> some View {
        self
            .shadow(color: color, radius: 0, x: width, y: 0)
            .shadow(color: color, radius: 0, x: -width, y: 0)
            .shadow(color: color, radius: 0, x: 0, y: width)
            .shadow(color: color, radius: 0, x: 0, y: -width)
            .shadow(color: color, radius: 0, x: width * 0.7, y: width * 0.7)
            .shadow(color: color, radius: 0, x: -width * 0.7, y: width * 0.7)
            .shadow(color: color, radius: 0, x: width * 0.7, y: -width * 0.7)
            .shadow(color: color, radius: 0, x: -width * 0.7, y: -width * 0.7)
    }
}

#Preview {
    WatchGameView(PreviewGameComponent())
}

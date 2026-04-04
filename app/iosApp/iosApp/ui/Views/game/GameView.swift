import Foundation
import SwiftUI
import Shared

struct GameView: View {
    private let component: GameComponent
    @Environment(\.accessibilityReduceMotion) private var reduceMotion

    @StateValue
    private var model: GameComponentModel

    @StateValue
    private var dialog: ChildSlot<AnyObject, GameComponentDialogChild>

    @StateValue
    private var sheet: ChildSlot<AnyObject, GameComponentSheetChild>

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
            if model.isLoading {
                ProgressView()
            } else if let gameState = model.gameState {
                let actions =
                    GameInputActions(
                        onPause: component.onPause,
                        onHold: component.onHold,
                        onRotate: component.onRotate,
                        onDragStarted: component.onDragStarted,
                        onDragged: { deltaX, deltaY in
                            component.onDragged(deltaX: deltaX, deltaY: deltaY)
                        },
                        onDragEnded: component.onDragEnded,
                        onBoardSizeChanged: { height in
                            component.onBoardSizeChanged(height: height)
                        },
                        onToggleMusic: { enabled in
                            component.onToggleMusic(enabled: enabled)
                        }
                    )

                GameAdaptiveLayout(
                    gameState: gameState,
                    settings: model.settings,
                    elapsedTime: model.elapsedTime,
                    ghostY: model.ghostPieceY?.int32Value,
                    lineSweeps: lineSweeps,
                    lockGlows: lockGlows,
                    reducedMotion: reduceMotion,
                    actions: actions
                )
                .modifier(
                    JuiceModifier(
                        shakePhase: shakePhase,
                        shakeAmount: shakeAmount,
                        scale: contentScale
                    )
                )

                AppleGameEffectsOverlay(
                    profile: .ios,
                theme: model.settings.themeConfig.visualTheme,
                flashOpacity: flashOpacity,
                floatingTexts: floatingTexts,
                particleBursts: particleBursts,
                reducedMotion: reduceMotion
            )

                if let child = dialog.child?.instance {
                    GlassDialogContainer {
                        DialogView(component: component, model: model, child: child)
                    }
                    .transition(.opacity.combined(with: .scale(scale: 0.9)))
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .gameNavigationBackButtonHidden()
        .sheet(
            item: Binding<SheetItem?>(
                get: {
                    if let child = sheet.child?.instance {
                        return SheetItem(child: child)
                    }
                    return nil
                },
                set: { item in
                    if item == nil {
                        component.onDismissSheet()
                    }
                }
            )
        ) { sheetItem in
            SheetView(child: sheetItem.child)
        }
        .keyboardAware { key in
            switch key.lowercased() {
            case "a", "directionleft":
                component.onMoveLeft()
            case "d", "directionright":
                component.onMoveRight()
            case "s", "directiondown":
                component.onMoveDown()
            case "w", " ", "directionup":
                switch model.settings.controlSettings.primaryRotateDirection {
                case .clockwise:
                    component.onRotate()
                case .counterclockwise:
                    component.onRotateCounterClockwise()
                case .oneEighty:
                    component.onRotate180()
                default:
                    component.onRotate()
                }
            case "q", "z":
                component.onRotateCounterClockwise()
            case "e", "x":
                component.onRotateClockwise()
            case "r":
                if model.settings.controlSettings.enable180Rotation {
                    component.onRotate180()
                }
            case "\r", "\n":
                component.onHardDrop()
            case "c", "h":
                component.onHold()
            case "\u{1b}", "p":
                component.onPause()
            default:
                break
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
                triggerShake(intensity: shake.intensity, power: CGFloat(shake.power))
            case let flash as VisualEffectEventScreenFlash:
                triggerFlash(power: CGFloat(flash.power))
            case let text as VisualEffectEventFloatingText:
                addFloatingText(
                    resolveFloatingTextMessage(text.textKey, comboStreak: Int(burst.comboStreak)),
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
        guard !reduceMotion else {
            shakeAmount = 0
            contentScale = 1
            return
        }
        let motion = appleThemeMotionStyle(theme: model.settings.themeConfig.visualTheme, reducedMotion: reduceMotion)
        let isHigh = intensity == .high
        let amplitude = isHigh ? 10 + (14 * power) : 3 + (5 * power)

        shakeAmount = amplitude
        withAnimation(.linear(duration: isHigh ? motion.shakeDurationHigh : motion.shakeDurationLow)) {
            shakePhase += 1
        }

        withAnimation(.spring(response: motion.scaleResponse, dampingFraction: motion.scaleDamping)) {
            contentScale = isHigh ? 1.04 : 1.02
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + motion.scaleResetDelay) {
            withAnimation(.spring(response: motion.scaleResetResponse, dampingFraction: motion.scaleResetDamping)) {
                contentScale = 1
            }
        }
    }

    private func triggerFlash(power: CGFloat) {
        flashOpacity = Double((reduceMotion ? 0.26 : 0.45) + ((reduceMotion ? 0.24 : 0.4) * power))
        let motion = appleThemeMotionStyle(theme: model.settings.themeConfig.visualTheme, reducedMotion: reduceMotion)
        withAnimation(.easeOut(duration: motion.flashFadeDuration)) {
            flashOpacity = 0
        }
    }

    private func addFloatingText(
        _ text: String,
        intensity: IntensityLevel,
        power: CGFloat
    ) {
        let motion = appleThemeMotionStyle(theme: model.settings.themeConfig.visualTheme, reducedMotion: reduceMotion)
        let isHigh = intensity == .high
        let entry = AppleGameFloatingTextEntry(
            id: UUID().uuidString,
            text: text,
            isHigh: isHigh,
            power: power,
            createdAt: Date(),
            duration: (isHigh ? 1.1 : 0.78) * (isHigh ? motion.floatingDurationHighMultiplier : motion.floatingDurationLowMultiplier)
        )
        floatingTexts.append(entry)

        DispatchQueue.main.asyncAfter(deadline: .now() + entry.duration + 0.1) {
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
        let motion = appleThemeMotionStyle(theme: model.settings.themeConfig.visualTheme, reducedMotion: reduceMotion)
        let entry = AppleGameParticleBurstEntry(
            id: "\(burstId)-\(UUID().uuidString)",
            isHigh: intensity == .high,
            power: power,
            particleCount: reduceMotion ? max(8, Int(CGFloat(particleCount) * 0.55)) : particleCount,
            seed: Int(burstId),
            createdAt: Date(),
            duration: 0.55 * motion.particleDurationMultiplier
        )
        particleBursts.append(entry)

        DispatchQueue.main.asyncAfter(deadline: .now() + entry.duration + 0.1) {
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
        let motion = appleThemeMotionStyle(theme: model.settings.themeConfig.visualTheme, reducedMotion: reduceMotion)
        let entry = AppleGameLineSweepEntry(
            id: "sweep-\(burstId)-\(UUID().uuidString)",
            clearedRows: clearedRows,
            isHigh: intensity == .high,
            power: power,
            createdAt: Date(),
            duration: 0.38 * motion.sweepDurationMultiplier
        )
        lineSweeps.append(entry)

        DispatchQueue.main.asyncAfter(deadline: .now() + entry.duration + 0.1) {
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
        let motion = appleThemeMotionStyle(theme: model.settings.themeConfig.visualTheme, reducedMotion: reduceMotion)
        let entry = AppleGameLockGlowEntry(
            id: "glow-\(burstId)-\(UUID().uuidString)",
            lockedCells: lockedCells,
            isHigh: intensity == .high,
            power: power,
            createdAt: Date(),
            duration: 0.34 * motion.lockGlowDurationMultiplier
        )
        lockGlows.append(entry)

        DispatchQueue.main.asyncAfter(deadline: .now() + entry.duration + 0.1) {
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
}

private extension View {
    @ViewBuilder
    func gameNavigationBackButtonHidden() -> some View {
        #if os(iOS) || os(macOS)
        navigationBarBackButtonHidden(true)
        #else
        self
        #endif
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
            return String(describing: type(of: child))
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
            ErrorDialogContent(message: child.message, onDismiss: component.onDismissDialog)
        default:
            EmptyView()
        }
    }
}

private typealias GameOverChild = GameComponentDialogChildGameOver
private typealias PauseChild = GameComponentDialogChildPause
private typealias ErrorChild = GameComponentDialogChildError
private typealias SettingsChild = GameComponentSheetChildSettings

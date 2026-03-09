import Foundation
import SwiftUI
import Shared

enum AppleGameEffectProfile: Equatable {
    case ios
    case watch

    var timelineInterval: TimeInterval {
        switch self {
        case .ios: return 1.0 / 60.0
        case .watch: return 1.0 / 45.0
        }
    }

    var highFontSize: CGFloat {
        switch self {
        case .ios: return 44
        case .watch: return 18
        }
    }

    var lowFontSize: CGFloat {
        switch self {
        case .ios: return 24
        case .watch: return 11
        }
    }

    var highRise: CGFloat {
        switch self {
        case .ios: return 220
        case .watch: return 40
        }
    }

    var lowRise: CGFloat {
        switch self {
        case .ios: return 130
        case .watch: return 24
        }
    }

    var particleRadiusRange: ClosedRange<CGFloat> {
        switch self {
        case .ios: return 2...8
        case .watch: return 0.8...2.4
        }
    }

    var burstRadiusRange: ClosedRange<CGFloat> {
        switch self {
        case .ios: return 80...210
        case .watch: return 22...48
        }
    }

    var particleLift: CGFloat {
        switch self {
        case .ios: return 36
        case .watch: return 8
        }
    }

    var sweepThickness: CGFloat {
        switch self {
        case .ios: return 84
        case .watch: return 28
        }
    }

    var glowRadius: CGFloat {
        switch self {
        case .ios: return 190
        case .watch: return 54
        }
    }

    var textStrokeWidthHigh: CGFloat {
        switch self {
        case .ios: return 2.8
        case .watch: return 1.8
        }
    }

    var textStrokeWidthLow: CGFloat {
        switch self {
        case .ios: return 1.9
        case .watch: return 1.2
        }
    }

    var highPulseAmplitude: CGFloat {
        switch self {
        case .ios: return 0.12
        case .watch: return 0.08
        }
    }

    var lowPulseAmplitude: CGFloat {
        switch self {
        case .ios: return 0.04
        case .watch: return 0.02
        }
    }
}

struct AppleThemeEffectStyle {
    let flashColor: Color
    let flashBoost: Double
    let textHigh: Color
    let textLow: Color
    let textStrokeHigh: Color
    let textStrokeLow: Color
    let particlePrimary: Color
    let particleSecondary: Color
    let particleOpacityBoost: Double
    let particleUsesSquares: Bool
    let sweepPrimary: Color
    let sweepSecondary: Color
    let sweepFill: Color
    let sweepOpacityBoost: Double
    let lockGlowPrimary: Color
    let lockGlowSecondary: Color
    let lockGlowOpacityBoost: Double
    let lockGlowCornerRadiusFactor: CGFloat
}

func appleThemeEffectStyle(theme: VisualTheme) -> AppleThemeEffectStyle {
    let accent = themeAccentColor(theme: theme)

    switch theme {
    case .retroGameboy:
        return AppleThemeEffectStyle(
            flashColor: Color(red: 0.78, green: 0.9, blue: 0.3),
            flashBoost: 0.72,
            textHigh: Color(red: 0.12, green: 0.24, blue: 0.07),
            textLow: Color(red: 0.2, green: 0.31, blue: 0.1),
            textStrokeHigh: Color(red: 0.78, green: 0.9, blue: 0.3),
            textStrokeLow: Color(red: 0.62, green: 0.74, blue: 0.23),
            particlePrimary: Color(red: 0.12, green: 0.24, blue: 0.07),
            particleSecondary: Color(red: 0.36, green: 0.52, blue: 0.14),
            particleOpacityBoost: 0.85,
            particleUsesSquares: true,
            sweepPrimary: Color(red: 0.18, green: 0.3, blue: 0.08),
            sweepSecondary: Color(red: 0.76, green: 0.9, blue: 0.28),
            sweepFill: Color(red: 0.52, green: 0.66, blue: 0.18),
            sweepOpacityBoost: 0.82,
            lockGlowPrimary: Color(red: 0.18, green: 0.3, blue: 0.08),
            lockGlowSecondary: Color(red: 0.74, green: 0.88, blue: 0.28),
            lockGlowOpacityBoost: 0.8,
            lockGlowCornerRadiusFactor: 0.0
        )
    case .retroNes:
        return AppleThemeEffectStyle(
            flashColor: Color(red: 1, green: 0.94, blue: 0.7),
            flashBoost: 0.84,
            textHigh: Color(red: 1, green: 0.94, blue: 0.72),
            textLow: Color.white,
            textStrokeHigh: Color(red: 0.36, green: 0.08, blue: 0.08),
            textStrokeLow: Color.black.opacity(0.95),
            particlePrimary: Color.white,
            particleSecondary: accent,
            particleOpacityBoost: 0.9,
            particleUsesSquares: true,
            sweepPrimary: accent,
            sweepSecondary: Color.white,
            sweepFill: Color(red: 1, green: 0.86, blue: 0.5),
            sweepOpacityBoost: 0.92,
            lockGlowPrimary: accent,
            lockGlowSecondary: Color.white,
            lockGlowOpacityBoost: 0.88,
            lockGlowCornerRadiusFactor: 0.0
        )
    case .neon:
        return AppleThemeEffectStyle(
            flashColor: Color(red: 0.62, green: 1, blue: 0.98),
            flashBoost: 0.95,
            textHigh: Color(red: 0.98, green: 0.96, blue: 1),
            textLow: Color(red: 0.8, green: 1, blue: 0.98),
            textStrokeHigh: Color(red: 0.2, green: 0.02, blue: 0.28),
            textStrokeLow: Color(red: 0.03, green: 0.14, blue: 0.2),
            particlePrimary: Color(red: 0.1, green: 1, blue: 0.96),
            particleSecondary: Color(red: 1, green: 0.16, blue: 0.9),
            particleOpacityBoost: 1.2,
            particleUsesSquares: false,
            sweepPrimary: Color(red: 0.12, green: 1, blue: 0.98),
            sweepSecondary: Color(red: 1, green: 0.18, blue: 0.9),
            sweepFill: Color.white,
            sweepOpacityBoost: 1.15,
            lockGlowPrimary: Color(red: 0.08, green: 1, blue: 0.96),
            lockGlowSecondary: Color(red: 1, green: 0.28, blue: 0.92),
            lockGlowOpacityBoost: 1.18,
            lockGlowCornerRadiusFactor: 0.36
        )
    case .pastel:
        return AppleThemeEffectStyle(
            flashColor: Color(red: 1, green: 0.97, blue: 0.9),
            flashBoost: 0.82,
            textHigh: Color(red: 0.92, green: 0.53, blue: 0.42),
            textLow: Color(red: 0.55, green: 0.47, blue: 0.64),
            textStrokeHigh: Color.white.opacity(0.95),
            textStrokeLow: Color(red: 0.82, green: 0.75, blue: 0.9),
            particlePrimary: Color(red: 1, green: 0.86, blue: 0.76),
            particleSecondary: accent,
            particleOpacityBoost: 0.8,
            particleUsesSquares: false,
            sweepPrimary: accent,
            sweepSecondary: Color.white,
            sweepFill: Color(red: 1, green: 0.91, blue: 0.8),
            sweepOpacityBoost: 0.78,
            lockGlowPrimary: accent,
            lockGlowSecondary: Color.white,
            lockGlowOpacityBoost: 0.72,
            lockGlowCornerRadiusFactor: 0.42
        )
    case .monochrome:
        return AppleThemeEffectStyle(
            flashColor: Color.white,
            flashBoost: 0.9,
            textHigh: Color.white,
            textLow: Color(red: 0.88, green: 0.88, blue: 0.88),
            textStrokeHigh: Color.black,
            textStrokeLow: Color.black.opacity(0.92),
            particlePrimary: Color.white,
            particleSecondary: Color(red: 0.66, green: 0.66, blue: 0.66),
            particleOpacityBoost: 0.88,
            particleUsesSquares: false,
            sweepPrimary: Color.white,
            sweepSecondary: Color(red: 0.72, green: 0.72, blue: 0.72),
            sweepFill: Color(red: 0.9, green: 0.9, blue: 0.9),
            sweepOpacityBoost: 0.88,
            lockGlowPrimary: Color.white,
            lockGlowSecondary: Color(red: 0.72, green: 0.72, blue: 0.72),
            lockGlowOpacityBoost: 0.84,
            lockGlowCornerRadiusFactor: 0.16
        )
    case .ocean:
        return AppleThemeEffectStyle(
            flashColor: Color(red: 0.8, green: 0.95, blue: 1),
            flashBoost: 0.86,
            textHigh: Color(red: 0.84, green: 0.97, blue: 1),
            textLow: Color(red: 0.68, green: 0.88, blue: 0.98),
            textStrokeHigh: Color(red: 0.0, green: 0.18, blue: 0.36),
            textStrokeLow: Color(red: 0.0, green: 0.14, blue: 0.28),
            particlePrimary: Color(red: 0.32, green: 0.9, blue: 0.95),
            particleSecondary: Color(red: 0.18, green: 0.45, blue: 0.9),
            particleOpacityBoost: 0.95,
            particleUsesSquares: false,
            sweepPrimary: Color(red: 0.26, green: 0.9, blue: 0.94),
            sweepSecondary: Color(red: 0.65, green: 0.93, blue: 1),
            sweepFill: accent,
            sweepOpacityBoost: 0.96,
            lockGlowPrimary: Color(red: 0.2, green: 0.88, blue: 0.94),
            lockGlowSecondary: Color(red: 0.58, green: 0.92, blue: 1),
            lockGlowOpacityBoost: 0.98,
            lockGlowCornerRadiusFactor: 0.36
        )
    case .sunset:
        return AppleThemeEffectStyle(
            flashColor: Color(red: 1, green: 0.86, blue: 0.72),
            flashBoost: 0.88,
            textHigh: Color(red: 1, green: 0.86, blue: 0.68),
            textLow: Color(red: 1, green: 0.72, blue: 0.58),
            textStrokeHigh: Color(red: 0.34, green: 0.09, blue: 0.02),
            textStrokeLow: Color(red: 0.28, green: 0.08, blue: 0.12),
            particlePrimary: Color(red: 1, green: 0.56, blue: 0.28),
            particleSecondary: Color(red: 1, green: 0.28, blue: 0.58),
            particleOpacityBoost: 1.0,
            particleUsesSquares: false,
            sweepPrimary: Color(red: 1, green: 0.54, blue: 0.22),
            sweepSecondary: Color(red: 1, green: 0.24, blue: 0.54),
            sweepFill: Color(red: 1, green: 0.82, blue: 0.62),
            sweepOpacityBoost: 1.02,
            lockGlowPrimary: Color(red: 1, green: 0.48, blue: 0.18),
            lockGlowSecondary: Color(red: 1, green: 0.26, blue: 0.5),
            lockGlowOpacityBoost: 1.0,
            lockGlowCornerRadiusFactor: 0.32
        )
    case .forest:
        return AppleThemeEffectStyle(
            flashColor: Color(red: 0.88, green: 1, blue: 0.88),
            flashBoost: 0.8,
            textHigh: Color(red: 0.88, green: 1, blue: 0.82),
            textLow: Color(red: 0.68, green: 0.92, blue: 0.66),
            textStrokeHigh: Color(red: 0.07, green: 0.2, blue: 0.07),
            textStrokeLow: Color(red: 0.05, green: 0.16, blue: 0.05),
            particlePrimary: Color(red: 0.34, green: 0.86, blue: 0.42),
            particleSecondary: Color(red: 0.68, green: 0.95, blue: 0.62),
            particleOpacityBoost: 0.92,
            particleUsesSquares: false,
            sweepPrimary: Color(red: 0.34, green: 0.86, blue: 0.42),
            sweepSecondary: Color(red: 0.82, green: 0.98, blue: 0.8),
            sweepFill: accent,
            sweepOpacityBoost: 0.92,
            lockGlowPrimary: Color(red: 0.3, green: 0.82, blue: 0.38),
            lockGlowSecondary: Color(red: 0.76, green: 0.97, blue: 0.72),
            lockGlowOpacityBoost: 0.9,
            lockGlowCornerRadiusFactor: 0.32
        )
    case .classic:
        fallthrough
    default:
        return AppleThemeEffectStyle(
            flashColor: Color.white,
            flashBoost: 0.88,
            textHigh: Color(red: 1, green: 0.93, blue: 0.62),
            textLow: Color.white,
            textStrokeHigh: Color(red: 0.23, green: 0.11, blue: 0.0),
            textStrokeLow: Color.black.opacity(0.95),
            particlePrimary: accent,
            particleSecondary: Color(red: 1, green: 0.93, blue: 0.6),
            particleOpacityBoost: 1.0,
            particleUsesSquares: false,
            sweepPrimary: accent,
            sweepSecondary: Color.white,
            sweepFill: Color.white,
            sweepOpacityBoost: 1.0,
            lockGlowPrimary: accent,
            lockGlowSecondary: Color.white,
            lockGlowOpacityBoost: 1.0,
            lockGlowCornerRadiusFactor: 0.35
        )
    }
}

struct AppleGameFloatingTextEntry: Identifiable {
    let id: String
    let text: String
    let isHigh: Bool
    let power: CGFloat
    let createdAt: Date
    let duration: TimeInterval

    func progress(at date: Date) -> CGFloat {
        effectProgress(now: date, duration: duration, createdAt: createdAt)
    }
}

struct AppleGameParticleBurstEntry: Identifiable {
    let id: String
    let isHigh: Bool
    let power: CGFloat
    let particleCount: Int
    let seed: Int
    let createdAt: Date
    let duration: TimeInterval

    func progress(at date: Date) -> CGFloat {
        effectProgress(now: date, duration: duration, createdAt: createdAt)
    }
}

struct AppleGameLineSweepEntry: Identifiable {
    let id: String
    let clearedRows: [Int]
    let isHigh: Bool
    let power: CGFloat
    let createdAt: Date
    let duration: TimeInterval

    func progress(at date: Date) -> CGFloat {
        effectProgress(now: date, duration: duration, createdAt: createdAt)
    }
}

struct AppleGameBoardCell: Equatable {
    let x: Int
    let y: Int
}

struct AppleGameLockGlowEntry: Identifiable {
    let id: String
    let lockedCells: [AppleGameBoardCell]
    let isHigh: Bool
    let power: CGFloat
    let createdAt: Date
    let duration: TimeInterval

    func progress(at date: Date) -> CGFloat {
        effectProgress(now: date, duration: duration, createdAt: createdAt)
    }
}

struct AppleGameEffectsOverlay: View {
    let profile: AppleGameEffectProfile
    let theme: VisualTheme
    let flashOpacity: Double
    let floatingTexts: [AppleGameFloatingTextEntry]
    let particleBursts: [AppleGameParticleBurstEntry]

    var body: some View {
        let style = appleThemeEffectStyle(theme: theme)
        ZStack {
            if flashOpacity > 0.001 {
                style.flashColor
                    .opacity(flashOpacity * style.flashBoost)
                    .ignoresSafeArea()
            }

            TimelineView(.animation(minimumInterval: profile.timelineInterval)) { timeline in
                ZStack {
                    Canvas { context, size in
                        let center = CGPoint(x: size.width / 2, y: size.height / 2)

                        drawParticleBursts(
                            context: &context,
                            center: center,
                            date: timeline.date
                        )
                    }

                    ForEach(floatingTexts) { entry in
                        let progress = entry.progress(at: timeline.date)
                        if progress < 1 {
                            floatingText(entry: entry, progress: progress)
                        }
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
        .allowsHitTesting(false)
    }

    @ViewBuilder
    private func floatingText(
        entry: AppleGameFloatingTextEntry,
        progress: CGFloat
    ) -> some View {
        let style = appleThemeEffectStyle(theme: theme)
        let pulse =
            entry.isHigh
            ? 1 + (sin(progress * .pi * 10) * profile.highPulseAmplitude * (1 - progress))
            : 1 + ((1 - progress) * profile.lowPulseAmplitude)

        let fontSize =
            entry.isHigh
            ? profile.highFontSize + (profile == .ios ? 12 * entry.power : 4 * entry.power)
            : profile.lowFontSize + (profile == .ios ? 6 * entry.power : 2 * entry.power)

        let rise = entry.isHigh ? profile.highRise : profile.lowRise
        let foreground = entry.isHigh ? style.textHigh : style.textLow
        let strokeColor = entry.isHigh ? style.textStrokeHigh : style.textStrokeLow

        Text(entry.text)
            .font(
                .system(
                    size: fontSize,
                    weight: .black,
                    design: profile == .watch ? .rounded : .default
                )
            )
            .foregroundStyle(foreground)
            .kerning(profile == .ios ? (entry.isHigh ? 1.4 : 0.9) : (entry.isHigh ? 0.8 : 0.4))
            .appleGameStroke(
                color: strokeColor,
                width: entry.isHigh ? profile.textStrokeWidthHigh : profile.textStrokeWidthLow
            )
            .scaleEffect(pulse)
            .offset(y: -(rise * progress))
            .opacity(1 - progress)
            .shadow(color: .black.opacity(profile == .ios ? 0.35 : 0.22), radius: profile == .ios ? 6 : 2, y: profile == .ios ? 2 : 0)
    }

    private func drawParticleBursts(
        context: inout GraphicsContext,
        center: CGPoint,
        date: Date
    ) {
        let style = appleThemeEffectStyle(theme: theme)

        for burst in particleBursts {
            let progress = burst.progress(at: date)
            guard progress < 1 else {
                continue
            }

            let maxRadius = profile.burstRadiusRange.lowerBound + (profile.burstRadiusRange.upperBound - profile.burstRadiusRange.lowerBound) * burst.power
            let alpha = (1 - progress) * (0.65 + (0.35 * burst.power)) * style.particleOpacityBoost

            for index in 0..<burst.particleCount {
                let angle = appleGameSeededFloat(seed: burst.seed, index: index, salt: 11) * (.pi * 2)
                let speedScale = 0.45 + (appleGameSeededFloat(seed: burst.seed, index: index, salt: 23) * 0.75)
                let radius = maxRadius * progress * speedScale
                let x = center.x + cos(angle) * radius
                let y = center.y + sin(angle) * radius - (progress * profile.particleLift)
                let particleRadius = profile.particleRadiusRange.lowerBound + appleGameSeededFloat(seed: burst.seed, index: index, salt: 37) * (profile.particleRadiusRange.upperBound - profile.particleRadiusRange.lowerBound)

                let rect = CGRect(
                    x: x - particleRadius,
                    y: y - particleRadius,
                    width: particleRadius * 2,
                    height: particleRadius * 2
                )

                let useSecondary = appleGameSeededFloat(seed: burst.seed, index: index, salt: 53) > 0.54
                let baseColor = useSecondary ? style.particleSecondary : style.particlePrimary
                let color =
                    burst.isHigh
                    ? baseColor.opacity(alpha * 1.08)
                    : baseColor.opacity(alpha * 0.92)

                if style.particleUsesSquares {
                    context.fill(Path(rect), with: .color(color))
                } else {
                    context.fill(Path(ellipseIn: rect), with: .color(color))
                }
            }
        }
    }
}

func isLineClearTextKey(_ key: VisualTextKey) -> Bool {
    switch key {
    case .single, .double_, .triple, .tetris, .clear:
        return true
    default:
        return false
    }
}

private func effectProgress(
    now: Date,
    duration: TimeInterval,
    createdAt: Date
) -> CGFloat {
    guard duration > 0 else {
        return 1
    }
    let elapsed = now.timeIntervalSince(createdAt)
    let raw = elapsed / duration
    return CGFloat(min(max(raw, 0), 1))
}

private func appleGameSeededFloat(
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
    func appleGameStroke(
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

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
    let isHigh: Bool
    let power: CGFloat
    let createdAt: Date
    let duration: TimeInterval

    func progress(at date: Date) -> CGFloat {
        effectProgress(now: date, duration: duration, createdAt: createdAt)
    }
}

struct AppleGameLockGlowEntry: Identifiable {
    let id: String
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
    let lineSweeps: [AppleGameLineSweepEntry]
    let lockGlows: [AppleGameLockGlowEntry]

    var body: some View {
        ZStack {
            if flashOpacity > 0.001 {
                Color.white
                    .opacity(flashOpacity)
                    .ignoresSafeArea()
            }

            TimelineView(.animation(minimumInterval: profile.timelineInterval)) { timeline in
                ZStack {
                    Canvas { context, size in
                        let center = CGPoint(x: size.width / 2, y: size.height / 2)

                        drawLockGlows(
                            context: &context,
                            size: size,
                            center: center,
                            date: timeline.date
                        )
                        drawLineSweeps(
                            context: &context,
                            size: size,
                            date: timeline.date
                        )
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
        let pulse =
            entry.isHigh
            ? 1 + (sin(progress * .pi * 10) * profile.highPulseAmplitude * (1 - progress))
            : 1 + ((1 - progress) * profile.lowPulseAmplitude)

        let fontSize =
            entry.isHigh
            ? profile.highFontSize + (profile == .ios ? 12 * entry.power : 4 * entry.power)
            : profile.lowFontSize + (profile == .ios ? 6 * entry.power : 2 * entry.power)

        let rise = entry.isHigh ? profile.highRise : profile.lowRise
        let foreground = entry.isHigh ? Color.yellow : Color.white
        let strokeColor = entry.isHigh ? Color(red: 0.23, green: 0.11, blue: 0.0) : Color.black.opacity(0.95)

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
        let accent = themeAccentColor(theme: theme)

        for burst in particleBursts {
            let progress = burst.progress(at: date)
            guard progress < 1 else {
                continue
            }

            let maxRadius = profile.burstRadiusRange.lowerBound + (profile.burstRadiusRange.upperBound - profile.burstRadiusRange.lowerBound) * burst.power
            let alpha = (1 - progress) * (0.65 + (0.35 * burst.power))

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

                let color = burst.isHigh ? Color(red: 1, green: 0.93, blue: 0.6) : accent
                context.fill(Path(ellipseIn: rect), with: .color(color.opacity(alpha)))
            }
        }
    }

    private func drawLineSweeps(
        context: inout GraphicsContext,
        size: CGSize,
        date: Date
    ) {
        let accent = themeAccentColor(theme: theme)

        for sweep in lineSweeps {
            let progress = sweep.progress(at: date)
            guard progress < 1 else {
                continue
            }

            let centerY = size.height * (0.5 - ((0.12 + (0.08 * sweep.power)) * (1 - progress)))
            let height = profile.sweepThickness * (0.7 + 0.5 * sweep.power)
            let sweepRect = CGRect(x: 0, y: centerY - height / 2, width: size.width, height: height)
            let glowOpacity = Double((1 - progress) * (sweep.isHigh ? 0.36 : 0.24))

            context.fill(
                Path(sweepRect),
                with: .linearGradient(
                    Gradient(colors: [
                        accent.opacity(glowOpacity * 0.25),
                        .white.opacity(glowOpacity),
                        accent.opacity(glowOpacity * 0.55),
                        .clear,
                    ]),
                    startPoint: CGPoint(x: sweepRect.minX, y: sweepRect.midY),
                    endPoint: CGPoint(x: sweepRect.maxX, y: sweepRect.midY)
                )
            )
        }
    }

    private func drawLockGlows(
        context: inout GraphicsContext,
        size: CGSize,
        center: CGPoint,
        date: Date
    ) {
        let accent = themeAccentColor(theme: theme)

        for glow in lockGlows {
            let progress = glow.progress(at: date)
            guard progress < 1 else {
                continue
            }

            let radius = profile.glowRadius * (0.45 + 0.4 * glow.power) * (0.55 + progress * 0.7)
            let opacity = Double((1 - progress) * (glow.isHigh ? 0.34 : 0.18))
            let gradient = Gradient(colors: [
                accent.opacity(opacity),
                accent.opacity(opacity * 0.55),
                .clear,
            ])

            context.fill(
                Path(CGRect(x: 0, y: 0, width: size.width, height: size.height)),
                with: .radialGradient(
                    gradient,
                    center: center,
                    startRadius: 0,
                    endRadius: radius
                )
            )
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

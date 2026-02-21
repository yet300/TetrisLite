import Foundation
import SwiftUI

struct JuiceFloatingTextEntry: Identifiable {
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

struct JuiceParticleBurstEntry: Identifiable {
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

struct JuiceOverlayView: View {
    let flashOpacity: Double
    let floatingTexts: [JuiceFloatingTextEntry]
    let particleBursts: [JuiceParticleBurstEntry]

    var body: some View {
        ZStack {
            if flashOpacity > 0.001 {
                Color.white
                    .opacity(flashOpacity)
                    .ignoresSafeArea()
            }

            TimelineView(.animation(minimumInterval: 1.0 / 60.0)) { timeline in
                ZStack {
                    ForEach(floatingTexts) { textEntry in
                        let progress = textEntry.progress(at: timeline.date)
                        if progress < 1 {
                            let pulse =
                                textEntry.isHigh
                                    ? 1 + (sin(progress * .pi * 10) * 0.12 * (1 - progress))
                                    : 1 + ((1 - progress) * 0.04)

                            Text(textEntry.text)
                                .font(
                                    .system(
                                        size: textEntry.isHigh
                                            ? 44 + (12 * textEntry.power)
                                            : 24 + (6 * textEntry.power),
                                        weight: textEntry.isHigh ? .black : .bold
                                    )
                                )
                                .foregroundStyle(textEntry.isHigh ? Color.yellow : Color.white)
                                .kerning(textEntry.isHigh ? 1.4 : 0.9)
                                .gameStroke(
                                    color: textEntry.isHigh ? Color(red: 0.23, green: 0.11, blue: 0.0) : Color.black.opacity(0.95),
                                    width: textEntry.isHigh ? 2.8 : 1.9
                                )
                                .scaleEffect(pulse)
                                .offset(y: -(textEntry.isHigh ? 220 : 130) * progress)
                                .opacity(1 - progress)
                                .shadow(color: .black.opacity(0.35), radius: 6, y: 2)
                        }
                    }

                    Canvas { context, size in
                        let center = CGPoint(x: size.width / 2, y: size.height / 2)

                        for burst in particleBursts {
                            let progress = burst.progress(at: timeline.date)
                            guard progress < 1 else {
                                continue
                            }

                            let maxRadius: CGFloat = 80 + ((210 - 80) * burst.power)
                            let alpha = (1 - progress) * (0.65 + (0.35 * burst.power))

                            for index in 0..<burst.particleCount {
                                let angle = seededFloat(seed: burst.seed, index: index, salt: 11) * (.pi * 2)
                                let speedScale = 0.45 + (seededFloat(seed: burst.seed, index: index, salt: 23) * 0.75)
                                let radius = maxRadius * progress * speedScale

                                let x = center.x + cos(angle) * radius
                                let y = center.y + sin(angle) * radius - (progress * 36)

                                let sizeScale = burst.isHigh ? 6 : 3
                                let particleRadius = 2 + (seededFloat(seed: burst.seed, index: index, salt: 37) * CGFloat(sizeScale))
                                let rect = CGRect(
                                    x: x - particleRadius,
                                    y: y - particleRadius,
                                    width: particleRadius * 2,
                                    height: particleRadius * 2
                                )

                                let color = burst.isHigh ? Color(red: 1, green: 0.93, blue: 0.6) : .white
                                context.fill(
                                    Path(ellipseIn: rect),
                                    with: .color(color.opacity(alpha))
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

private func seededFloat(
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
    func gameStroke(
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

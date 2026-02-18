import SwiftUI
import Shared

enum WatchPalette {
    static let accent = Color(red: 0.224, green: 1.0, blue: 0.078)
    static let accentSoft = Color(red: 0.180, green: 0.796, blue: 0.063)
    static let glassHighlight = Color.white.opacity(0.28)
    static let glassShadow = Color.black.opacity(0.6)
    static let boardBackground = Color.white.opacity(0.05)
}

struct WatchButton: View {
    let title: String
    let systemImage: String
    let action: () -> Void
    var backgroundColor: Color = Color.white.opacity(0.15)
    var foregroundColor: Color = .white
    var iconColor: Color? = nil

    var body: some View {
        Button(action: {
            WKInterfaceDevice.current().play(.click)
            action()
        }) {
            HStack(spacing: 10) {
                Image(systemName: systemImage)
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundStyle(iconColor ?? foregroundColor)

                Text(title)
                    .font(.system(size: 14, weight: .semibold, design: .rounded))
                    .foregroundStyle(foregroundColor)

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.system(size: 10, weight: .bold))
                    .foregroundStyle(.white.opacity(0.3))
            }
            .padding(.vertical, 12)
            .padding(.horizontal, 14)
            .background(backgroundColor)
            .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
        }
        .buttonStyle(.plain)
    }
}

struct WatchBackground: View {
    var body: some View {
        ZStack {
            Color.black
                .ignoresSafeArea()
            Circle()
                .fill(WatchPalette.accent.opacity(0.12))
                .blur(radius: 24)
                .offset(x: -20, y: -30)
            Circle()
                .fill(WatchPalette.accentSoft.opacity(0.1))
                .blur(radius: 26)
                .offset(x: 24, y: 34)
        }
    }
}

struct GlassCard<Content: View>: View {
    let cornerRadius: CGFloat
    let content: Content

    init(cornerRadius: CGFloat = 16, @ViewBuilder content: () -> Content) {
        self.cornerRadius = cornerRadius
        self.content = content()
    }

    var body: some View {
        content
            .padding(10)
            .background(
                RoundedRectangle(cornerRadius: cornerRadius, style: .continuous)
                    .fill(.ultraThinMaterial)
                    .overlay(
                        RoundedRectangle(cornerRadius: cornerRadius, style: .continuous)
                            .stroke(WatchPalette.glassHighlight, lineWidth: 1)
                    )
            )
            .shadow(color: WatchPalette.accent.opacity(0.2), radius: 6, x: 0, y: 4)
    }
}

struct GlassRowBackground: View {
    var body: some View {
        RoundedRectangle(cornerRadius: 14, style: .continuous)
            .fill(.ultraThinMaterial)
            .overlay(
                RoundedRectangle(cornerRadius: 14, style: .continuous)
                    .stroke(WatchPalette.glassHighlight, lineWidth: 1)
            )
    }
}

struct WatchCircleButton: View {
    let systemImage: String
    let action: () -> Void
    var size: CGFloat = 44
    var backgroundColor: Color = Color.white.opacity(0.12)
    var iconSize: CGFloat = 20

    var body: some View {
        Button(action: {
            WKInterfaceDevice.current().play(.click)
            action()
        }) {
            ZStack {
                Circle()
                    .fill(backgroundColor)
                Image(systemName: systemImage)
                    .font(.system(size: iconSize, weight: .semibold))
            }
            .frame(width: size, height: size)
        }
        .buttonStyle(.plain)
    }
}

struct WatchDifficultySelector: View {
    @Binding var difficulty: Difficulty
    @State private var crownValue: Double = 0.0

    init(difficulty: Binding<Difficulty>) {
        self._difficulty = difficulty
        self._crownValue = State(initialValue: Double(difficulty.wrappedValue.ordinal))
    }

    var body: some View {
        VStack(spacing: 2) {
            Text(difficulty.name.capitalized)
                .font(.system(size: 11, weight: .bold, design: .rounded))
                .foregroundStyle(WatchPalette.accent)

            HStack(spacing: 6) {
                ForEach(Difficulty.entries, id: \.self) { item in
                    Circle()
                        .fill(item == difficulty ? WatchPalette.accent : Color.white.opacity(0.2))
                        .frame(width: 4, height: 4)
                }
            }
        }
        .padding(.vertical, 4)
        .padding(.horizontal, 12)
        .background(Capsule().fill(Color.white.opacity(0.1)))
        .focusable()
        .digitalCrownRotation($crownValue, from: 0, through: Double(Difficulty.entries.count - 1), by: 1, sensitivity: .low, isContinuous: false, isHapticFeedbackEnabled: true)
        .onChange(of: crownValue) { _, newValue in
            let index = Int(round(newValue))
            if index < Difficulty.entries.count {
                difficulty = Difficulty.entries[index]
            }
        }
    }
}

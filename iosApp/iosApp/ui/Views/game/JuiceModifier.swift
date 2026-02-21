import SwiftUI

struct JuiceShakeEffect: GeometryEffect {
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
        let x = amount * sin(phase * .pi * 14)
        let y = amount * 0.35 * cos(phase * .pi * 11)
        return ProjectionTransform(CGAffineTransform(translationX: x, y: y))
    }
}

struct JuiceModifier: ViewModifier {
    let shakePhase: CGFloat
    let shakeAmount: CGFloat
    let scale: CGFloat

    func body(content: Content) -> some View {
        content
            .modifier(JuiceShakeEffect(amount: shakeAmount, phase: shakePhase))
            .scaleEffect(scale)
    }
}

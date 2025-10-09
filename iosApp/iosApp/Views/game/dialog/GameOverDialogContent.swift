import SwiftUI
import Shared

struct GameOverDialogContent: View {
    let component: GameComponent
    let model : GameComponentModel

    var body: some View {
        Text("Game Over")
            .font(.largeTitle.bold())
            .foregroundColor(.white)
            .shadow(color: .red, radius: 10)

        VStack {
            Text("Final Score: \(model.finalScore)")
                .font(.title2)
            Text("Lines Cleared: \(model.finalLinesCleared)")
                .font(.body)
                .foregroundColor(.white.opacity(0.8))
        }
        .foregroundColor(.white)

        VStack(spacing: 16) {
            GlassDialogButton(title: "Retry", isPrimary: true, action: component.onRetry)
            GlassDialogButton(title: "Back to Home", action: component.onQuit)
        }
    }
}



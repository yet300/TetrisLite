import SwiftUI
import Shared

struct GameOverDialogContent: View {
    let component: GameComponent
    let model : GameComponentModel

    var body: some View {
        Text(Strings.gameOver)
            .font(.largeTitle.bold())
            .foregroundColor(.white)
            .shadow(color: .red, radius: 10)

        VStack {
            Text(Strings.finalScore(Int(model.finalScore)))
                .font(.title2)
            Text(Strings.linesCleared(Int(model.finalLinesCleared)))
                .font(.body)
                .foregroundColor(.white.opacity(0.8))
        }
        .foregroundColor(.white)

        VStack(spacing: 16) {
            GlassDialogButton(title: Strings.retry, isPrimary: true, action: component.onRetry)
            GlassDialogButton(title: Strings.backToHome, action: component.onQuit)
        }
    }
}



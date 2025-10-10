import SwiftUI
import Shared

struct GameOverDialogContent: View {
    let component: GameComponent
    let model : GameComponentModel

    var body: some View {
        Text(Strings.gameOver)
            .font(.largeTitle.bold())
            .foregroundColor(.primaryLabel)
            .shadow(color: .systemRed, radius: 10)

        VStack {
            Text(Strings.finalScore(Int(model.finalScore)))
                .font(.title2)
                .foregroundColor(.primaryLabel)
            Text(Strings.linesCleared(Int(model.finalLinesCleared)))
                .font(.title2)
                .foregroundColor(.primaryLabel)
        }

        VStack(spacing: 16) {
            GlassDialogButton(title: Strings.retry, isPrimary: true, action: component.onRetry)
            GlassDialogButton(title: Strings.backToHome, action: component.onQuit)
        }
    }
}



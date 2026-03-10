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
            if let gameState = model.gameState {
                Text("Level \(gameState.level) • \(formatDuration(model.elapsedTime))")
                    .foregroundColor(.primaryLabel)
                Text("Pieces \(gameState.piecesPlaced) • Max combo \(gameState.maxCombo)")
                    .foregroundColor(.primaryLabel)
                Text("Tetrises \(gameState.tetrisesCleared) • T-Spins \(gameState.tSpinClears) • Perfect clears \(gameState.perfectClears)")
                    .multilineTextAlignment(.center)
                    .foregroundColor(.primaryLabel)
            }
        }

        VStack(spacing: 16) {
            GlassDialogButton(title: Strings.retry, isPrimary: true, action: component.onRetry)
            GlassDialogButton(title: Strings.backToHome, action: component.onQuit)
        }
    }

    private func formatDuration(_ milliseconds: Int64) -> String {
        let seconds = (milliseconds / 1000) % 60
        let minutes = (milliseconds / 1000) / 60
        return "\(minutes):" + String(format: "%02d", seconds)
    }
}

